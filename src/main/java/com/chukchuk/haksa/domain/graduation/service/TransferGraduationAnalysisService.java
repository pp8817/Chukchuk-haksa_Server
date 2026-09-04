// 3학년 편입생의 졸업요건을 저장된 학사 데이터로 분석한다.

package com.chukchuk.haksa.domain.graduation.service;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.graduation.dto.AreaRequirementDto;
import com.chukchuk.haksa.domain.graduation.dto.CourseInternalDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferGraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.TransferManualReviewReason;
import com.chukchuk.haksa.domain.graduation.dto.TransferRequirementProgressDto;
import com.chukchuk.haksa.domain.graduation.policy.DesignatedCourseEvaluator;
import com.chukchuk.haksa.domain.graduation.repository.GraduationQueryRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 편입 인정학점과 편입생 전용 졸업요건을 함께 계산한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransferGraduationAnalysisService {

  private static final int REQUIRED_TOTAL_CREDITS = 130;
  private static final int REQUIRED_REGISTERED_SEMESTERS = 4;
  private static final BigDecimal DEFAULT_REQUIRED_GPA = new BigDecimal("2.0");
  private static final BigDecimal SECONDARY_MAJOR_REQUIRED_GPA = new BigDecimal("2.5");
  private static final Set<String> TRANSFER_CREDIT_CODES =
      Set.of("07045", "07046", "00111", "07050");
  private static final Set<String> CORE_AREA_TYPES = Set.of("전핵", "전필", "전취");
  private static final Set<String> ELECTIVE_AREA_TYPES = Set.of("전선");

  private final StudentAcademicRecordService studentAcademicRecordService;
  private final StudentCourseRepository studentCourseRepository;
  private final StudentDesignatedCourseRepository studentDesignatedCourseRepository;
  private final StudentGraduationProgressService studentGraduationProgressService;
  private final DesignatedCourseEvaluator designatedCourseEvaluator;
  private final GraduationQueryRepository graduationQueryRepository;

  /**
   * 편입생에게 적용할 모든 자동 계산 요건과 수동 확인 상태를 반환한다.
   *
   * @param student 분석할 편입생
   * @return 편입생 졸업진단 응답
   */
  public GraduationProgressResponse analyze(Student student) {
    UUID studentId = student.getId();
    List<TransferManualReviewReason> reviewReasons = new ArrayList<>();

    StudentAcademicRecord academicRecord =
        studentAcademicRecordService.findStudentAcademicRecordByStudentId(studentId).orElse(null);
    Integer totalEarnedCredits =
        academicRecord == null ? null : academicRecord.getTotalEarnedCredits();
    BigDecimal cumulativeGpa = academicRecord == null ? null : academicRecord.getCumulativeGpa();

    final Boolean creditsFulfilled = fulfillCredits(totalEarnedCredits);
    final Integer remainingCredits = remainingCredits(totalEarnedCredits);
    final Boolean gpaFulfilled = fulfillGpa(student, cumulativeGpa);
    if (totalEarnedCredits == null || cumulativeGpa == null) {
      reviewReasons.add(TransferManualReviewReason.ACADEMIC_SUMMARY_INCOMPLETE);
    }

    List<StudentDesignatedCourse> designatedCourses =
        studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(studentId);
    List<StudentCourse> studentCourses =
        studentCourseRepository.findAllWithCourseByStudentId(studentId);
    DesignatedCourseEvaluator.Evaluation evaluation =
        designatedCourseEvaluator.evaluate(designatedCourses, studentCourses);

    boolean designatedCoursesNeedsRefresh = student.getDesignatedCoursesSnapshotVersion() == null;
    final Boolean designatedCoursesFulfilled =
        designatedCoursesNeedsRefresh
            ? null
            : evaluation.designatedCourses().stream()
                .map(progress -> progress.status().name())
                .allMatch("COMPLETED"::equals);
    if (designatedCoursesNeedsRefresh) {
      reviewReasons.add(TransferManualReviewReason.DESIGNATED_COURSES_NOT_VERIFIED);
    }

    List<AreaRequirementDto> requirements = findRequirements(student, reviewReasons);
    List<CourseInternalDto> completedCourses =
        requirements.isEmpty()
            ? List.of()
            : graduationQueryRepository.getLatestValidCourses(studentId);
    TransferRequirementProgressDto coreProgress =
        calculateAreaProgress(requirements, completedCourses, CORE_AREA_TYPES);
    TransferRequirementProgressDto electiveProgress =
        calculateElectiveProgress(requirements, completedCourses);

    if (coreProgress.fulfilled() == null || electiveProgress.fulfilled() == null) {
      if (!reviewReasons.contains(TransferManualReviewReason.GRADUATION_REQUIREMENTS_NOT_FOUND)) {
        reviewReasons.add(TransferManualReviewReason.GRADUATION_REQUIREMENTS_NOT_FOUND);
      }
    }

    Integer registeredSemesters = student.getTransferRegisteredSemesters();
    Boolean registeredSemestersFulfilled =
        registeredSemesters == null ? null : registeredSemesters >= REQUIRED_REGISTERED_SEMESTERS;
    if (registeredSemestersFulfilled == null) {
      reviewReasons.add(TransferManualReviewReason.REGISTERED_SEMESTERS_NOT_VERIFIED);
    }

    Boolean graduationReviewFulfilled =
        studentGraduationProgressService.getGraduationReviewFulfilled(studentId).orElse(null);
    if (graduationReviewFulfilled == null) {
      reviewReasons.add(TransferManualReviewReason.GRADUATION_REVIEW_NOT_AVAILABLE);
    }

    Boolean languageCertFulfilled =
        studentGraduationProgressService.getLanguageCertFulfilled(studentId).orElse(null);
    if (languageCertFulfilled == null) {
      reviewReasons.add(TransferManualReviewReason.LANGUAGE_CERT_NOT_VERIFIED);
    }

    boolean manualReviewRequired = !reviewReasons.isEmpty();
    Boolean graduationEligible =
        manualReviewRequired
            ? null
            : allFulfilled(
                creditsFulfilled,
                gpaFulfilled,
                coreProgress.fulfilled(),
                electiveProgress.fulfilled(),
                registeredSemestersFulfilled,
                designatedCoursesFulfilled,
                graduationReviewFulfilled,
                languageCertFulfilled);

    TransferGraduationProgressDto transferProgress =
        new TransferGraduationProgressDto(
            REQUIRED_TOTAL_CREDITS,
            totalEarnedCredits,
            remainingCredits,
            creditsFulfilled,
            evaluation.recognizedTransferCredits(),
            cumulativeGpa,
            requiredGpa(student),
            gpaFulfilled,
            student.getAcademicInfo() == null
                ? null
                : student.getAcademicInfo().getCompletedSemesters(),
            designatedCoursesNeedsRefresh,
            evaluation.designatedCourses(),
            manualReviewRequired,
            reviewReasons,
            coreProgress,
            electiveProgress,
            registeredSemestersFulfilled,
            designatedCoursesFulfilled,
            graduationReviewFulfilled,
            graduationEligible);

    return GraduationProgressResponse.forTransfer(transferProgress, languageCertFulfilled);
  }

  private List<AreaRequirementDto> findRequirements(
      Student student, List<TransferManualReviewReason> reviewReasons) {
    Department department =
        student.getMajor() != null ? student.getMajor() : student.getDepartment();
    Integer admissionYear =
        student.getAcademicInfo() == null ? null : student.getAcademicInfo().getAdmissionYear();
    if (department == null || department.getId() == null || admissionYear == null) {
      reviewReasons.add(TransferManualReviewReason.GRADUATION_REQUIREMENTS_NOT_FOUND);
      return List.of();
    }

    List<AreaRequirementDto> requirements =
        graduationQueryRepository.getAreaRequirementsWithCache(department.getId(), admissionYear);
    return requirements == null ? List.of() : requirements;
  }

  private TransferRequirementProgressDto calculateAreaProgress(
      List<AreaRequirementDto> requirements,
      List<CourseInternalDto> completedCourses,
      Set<String> areaTypes) {
    int requiredCredits =
        requirements.stream()
            .filter(requirement -> areaTypes.contains(normalize(requirement.areaType())))
            .mapToInt(AreaRequirementDto::requiredCredits)
            .sum();
    int earnedCredits =
        completedCourses.stream()
            .filter(course -> areaTypes.contains(normalize(course.getAreaType())))
            .filter(course -> !isTransferCredit(course.getCourseCode()))
            .mapToInt(course -> course.getCredits() == null ? 0 : course.getCredits())
            .sum();
    boolean hasRequirement =
        requirements.stream()
            .anyMatch(requirement -> areaTypes.contains(normalize(requirement.areaType())));
    return new TransferRequirementProgressDto(
        hasRequirement ? requiredCredits : null,
        hasRequirement ? earnedCredits : null,
        hasRequirement ? earnedCredits >= requiredCredits : null);
  }

  private TransferRequirementProgressDto calculateElectiveProgress(
      List<AreaRequirementDto> requirements, List<CourseInternalDto> completedCourses) {
    TransferRequirementProgressDto base =
        calculateAreaProgress(requirements, completedCourses, ELECTIVE_AREA_TYPES);
    if (base.requiredCredits() == null) {
      return base;
    }
    int minimumCredits = (base.requiredCredits() + 1) / 2;
    return new TransferRequirementProgressDto(
        minimumCredits, base.earnedCredits(), base.earnedCredits() >= minimumCredits);
  }

  private Boolean fulfillCredits(Integer totalEarnedCredits) {
    return totalEarnedCredits == null ? null : totalEarnedCredits >= REQUIRED_TOTAL_CREDITS;
  }

  private Integer remainingCredits(Integer totalEarnedCredits) {
    return totalEarnedCredits == null
        ? null
        : Math.max(0, REQUIRED_TOTAL_CREDITS - totalEarnedCredits);
  }

  private Boolean fulfillGpa(Student student, BigDecimal cumulativeGpa) {
    return cumulativeGpa == null ? null : cumulativeGpa.compareTo(requiredGpa(student)) >= 0;
  }

  private BigDecimal requiredGpa(Student student) {
    return student.getSecondaryMajor() == null
        ? DEFAULT_REQUIRED_GPA
        : SECONDARY_MAJOR_REQUIRED_GPA;
  }

  private Boolean allFulfilled(Boolean... values) {
    for (Boolean value : values) {
      if (!Boolean.TRUE.equals(value)) {
        return false;
      }
    }
    return true;
  }

  private boolean isTransferCredit(String courseCode) {
    return TRANSFER_CREDIT_CODES.contains(normalize(courseCode));
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim();
  }
}
