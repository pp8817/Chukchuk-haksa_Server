// 편입생 졸업요건 부분 진단 서비스의 계산 계약을 검증한다.

package com.chukchuk.haksa.domain.graduation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferGraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.TransferManualReviewReason;
import com.chukchuk.haksa.domain.graduation.policy.DesignatedCourseEvaluator;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.model.embeddable.AcademicInfo;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferGraduationAnalysisServiceTests {

  private static final UUID STUDENT_ID = UUID.randomUUID();

  @Mock private StudentAcademicRecordService studentAcademicRecordService;
  @Mock private StudentCourseRepository studentCourseRepository;
  @Mock private StudentDesignatedCourseRepository studentDesignatedCourseRepository;
  @Mock private StudentGraduationProgressService studentGraduationProgressService;
  @Mock private DesignatedCourseEvaluator designatedCourseEvaluator;

  @InjectMocks private TransferGraduationAnalysisService service;

  private Student student;
  private StudentAcademicRecord academicRecord;

  @BeforeEach
  void setUp() {
    student = mock(Student.class);
    academicRecord = mock(StudentAcademicRecord.class);
    AcademicInfo academicInfo =
        AcademicInfo.builder().completedSemesters(3).isTransferStudent(true).build();

    lenient().when(student.getId()).thenReturn(STUDENT_ID);
    lenient().when(student.getAcademicInfo()).thenReturn(academicInfo);
    lenient().when(student.getSecondaryMajor()).thenReturn(null);
    lenient().when(studentAcademicRecordService.getStudentAcademicRecordByStudentId(STUDENT_ID))
        .thenReturn(academicRecord);
    lenient().when(studentCourseRepository.findAllWithCourseByStudentId(STUDENT_ID))
        .thenReturn(List.of());
    lenient()
        .when(studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(STUDENT_ID))
        .thenReturn(List.of());
    lenient()
        .when(designatedCourseEvaluator.evaluate(List.of(), List.of()))
        .thenReturn(new DesignatedCourseEvaluator.Evaluation(List.of(), 0));
    lenient()
        .when(studentGraduationProgressService.getLanguageCertFulfilled(STUDENT_ID))
        .thenReturn(Optional.of(true));
  }

  @Test
  void calculatesTransferCreditsAndGpaFromCumulativeAcademicRecord() {
    when(academicRecord.getTotalEarnedCredits()).thenReturn(112);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("3.2"));

    GraduationProgressResponse response = service.analyze(student);
    TransferGraduationProgressDto progress = response.getTransferProgress();

    assertThat(progress.requiredTotalCredits()).isEqualTo(130);
    assertThat(progress.totalEarnedCredits()).isEqualTo(112);
    assertThat(progress.remainingCredits()).isEqualTo(18);
    assertThat(progress.creditsFulfilled()).isFalse();
    assertThat(progress.recognizedTransferCredits()).isZero();
    assertThat(progress.cumulativeGpa()).isEqualByComparingTo("3.2");
    assertThat(progress.requiredGpa()).isEqualByComparingTo("2.0");
    assertThat(progress.gpaFulfilled()).isTrue();
    assertThat(progress.completedSemesters()).isEqualTo(3);
  }

  @Test
  void appliesHigherGpaRequirementWhenSecondaryMajorExists() {
    Department secondaryMajor = mock(Department.class);
    when(student.getSecondaryMajor()).thenReturn(secondaryMajor);
    when(academicRecord.getTotalEarnedCredits()).thenReturn(130);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("2.3"));

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.remainingCredits()).isZero();
    assertThat(progress.creditsFulfilled()).isTrue();
    assertThat(progress.requiredGpa()).isEqualByComparingTo("2.5");
    assertThat(progress.gpaFulfilled()).isFalse();
  }

  @Test
  void exposesDesignatedCourseStatusesAndRefreshState() {
    StudentDesignatedCourse designated = mock(StudentDesignatedCourse.class);
    StudentCourse completedCourse = mock(StudentCourse.class);
    List<StudentDesignatedCourse> designatedCourses = List.of(designated);
    List<StudentCourse> studentCourses = List.of(completedCourse);
    when(studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(STUDENT_ID))
        .thenReturn(designatedCourses);
    when(studentCourseRepository.findAllWithCourseByStudentId(STUDENT_ID)).thenReturn(studentCourses);
    when(designatedCourseEvaluator.evaluate(designatedCourses, studentCourses))
        .thenReturn(
            new DesignatedCourseEvaluator.Evaluation(
                List.of(
                    new DesignatedCourseProgressDto(
                        "C101", "자료구조", 3, DesignatedCourseCompletionStatus.COMPLETED)),
                65));
    when(student.getDesignatedCoursesSnapshotVersion()).thenReturn((Instant) null);
    when(academicRecord.getTotalEarnedCredits()).thenReturn(112);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("3.2"));

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.recognizedTransferCredits()).isEqualTo(65);
    assertThat(progress.designatedCourses()).extracting(DesignatedCourseProgressDto::status)
        .containsExactly(DesignatedCourseCompletionStatus.COMPLETED);
    assertThat(progress.designatedCoursesNeedsRefresh()).isTrue();
    assertThat(progress.manualReviewRequired()).isTrue();
    assertThat(progress.manualReviewReasons())
        .contains(
            TransferManualReviewReason.REQUIRED_COURSES_NOT_ASSESSABLE,
            TransferManualReviewReason.ELECTIVE_RATIO_NOT_ASSESSABLE);
  }

  @Test
  void treatsReceivedEmptyDesignatedSnapshotAsFresh() {
    when(student.getDesignatedCoursesSnapshotVersion()).thenReturn(Instant.parse("2026-09-02T00:00:00Z"));
    when(academicRecord.getTotalEarnedCredits()).thenReturn(130);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("2.0"));

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.designatedCourses()).isEmpty();
    assertThat(progress.designatedCoursesNeedsRefresh()).isFalse();
  }

  @Test
  void returnsNullFulfillmentAndManualReasonWhenAcademicSummaryIsIncomplete() {
    when(academicRecord.getTotalEarnedCredits()).thenReturn(null);
    when(academicRecord.getCumulativeGpa()).thenReturn(null);

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.totalEarnedCredits()).isNull();
    assertThat(progress.remainingCredits()).isNull();
    assertThat(progress.creditsFulfilled()).isNull();
    assertThat(progress.gpaFulfilled()).isNull();
    assertThat(progress.manualReviewReasons())
        .contains(TransferManualReviewReason.ACADEMIC_SUMMARY_INCOMPLETE);
  }

  @Test
  void preservesLanguageCertificateContract() {
    when(academicRecord.getTotalEarnedCredits()).thenReturn(130);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("2.0"));
    when(studentGraduationProgressService.getLanguageCertFulfilled(STUDENT_ID))
        .thenReturn(Optional.empty());

    GraduationProgressResponse response = service.analyze(student);

    assertThat(response.getLanguageCertFulfilled()).isNull();
    assertThat(response.isLanguageCertNeedsRefresh()).isTrue();
  }
}
