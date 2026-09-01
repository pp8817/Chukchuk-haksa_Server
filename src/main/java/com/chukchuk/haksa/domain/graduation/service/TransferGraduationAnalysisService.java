// 편입생의 자동 계산 가능한 졸업요건을 조합한다.

package com.chukchuk.haksa.domain.graduation.service;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferGraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.TransferManualReviewReason;
import com.chukchuk.haksa.domain.graduation.policy.DesignatedCourseEvaluator;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 현재 저장된 학사 데이터로 편입생 부분 졸업진단을 계산한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferGraduationAnalysisService {

  private static final int REQUIRED_TOTAL_CREDITS = 130;
  private static final BigDecimal DEFAULT_REQUIRED_GPA = new BigDecimal("2.0");
  private static final BigDecimal SECONDARY_MAJOR_REQUIRED_GPA = new BigDecimal("2.5");

  private final StudentAcademicRecordService studentAcademicRecordService;
  private final StudentCourseRepository studentCourseRepository;
  private final StudentDesignatedCourseRepository studentDesignatedCourseRepository;
  private final StudentGraduationProgressService studentGraduationProgressService;
  private final DesignatedCourseEvaluator designatedCourseEvaluator;

  /**
   * 편입생의 자동 계산 가능한 졸업요건을 API 응답으로 변환한다.
   *
   * @param student 분석할 편입생
   * @return 편입생 부분 진단 응답
   */
  public GraduationProgressResponse analyze(Student student) {
    UUID studentId = student.getId();
    StudentAcademicRecord academicRecord =
        studentAcademicRecordService.getStudentAcademicRecordByStudentId(studentId);
    List<StudentDesignatedCourse> designatedCourses =
        studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(studentId);
    List<StudentCourse> studentCourses =
        studentCourseRepository.findAllWithCourseByStudentId(studentId);

    DesignatedCourseEvaluator.Evaluation evaluation =
        designatedCourseEvaluator.evaluate(designatedCourses, studentCourses);
    Integer totalEarnedCredits = academicRecord.getTotalEarnedCredits();
    BigDecimal cumulativeGpa = academicRecord.getCumulativeGpa();

    Integer remainingCredits =
        totalEarnedCredits == null
            ? null
            : Math.max(0, REQUIRED_TOTAL_CREDITS - totalEarnedCredits);
    Boolean creditsFulfilled =
        totalEarnedCredits == null ? null : totalEarnedCredits >= REQUIRED_TOTAL_CREDITS;

    BigDecimal requiredGpa =
        student.getSecondaryMajor() == null ? DEFAULT_REQUIRED_GPA : SECONDARY_MAJOR_REQUIRED_GPA;
    Boolean gpaFulfilled = cumulativeGpa == null ? null : cumulativeGpa.compareTo(requiredGpa) >= 0;

    List<TransferManualReviewReason> manualReviewReasons = baseManualReviewReasons();
    if (totalEarnedCredits == null || cumulativeGpa == null) {
      manualReviewReasons.add(TransferManualReviewReason.ACADEMIC_SUMMARY_INCOMPLETE);
    }

    Instant designatedCoursesSnapshotVersion = student.getDesignatedCoursesSnapshotVersion();
    TransferGraduationProgressDto transferProgress =
        new TransferGraduationProgressDto(
            REQUIRED_TOTAL_CREDITS,
            totalEarnedCredits,
            remainingCredits,
            creditsFulfilled,
            evaluation.recognizedTransferCredits(),
            cumulativeGpa,
            requiredGpa,
            gpaFulfilled,
            student.getAcademicInfo().getCompletedSemesters(),
            designatedCoursesSnapshotVersion == null,
            evaluation.designatedCourses(),
            true,
            manualReviewReasons);

    return GraduationProgressResponse.forTransfer(
        transferProgress,
        studentGraduationProgressService.getLanguageCertFulfilled(studentId).orElse(null));
  }

  private List<TransferManualReviewReason> baseManualReviewReasons() {
    return new ArrayList<>(
        List.of(
            TransferManualReviewReason.TRANSFER_ENTRY_GRADE_UNKNOWN,
            TransferManualReviewReason.REGISTERED_SEMESTERS_NOT_VERIFIED,
            TransferManualReviewReason.REQUIRED_COURSES_NOT_ASSESSABLE,
            TransferManualReviewReason.ELECTIVE_RATIO_NOT_ASSESSABLE,
            TransferManualReviewReason.MINOR_OR_LINKED_MAJOR_NOT_ASSESSABLE,
            TransferManualReviewReason.GRADUATION_REVIEW_NOT_AVAILABLE));
  }
}
