// 편입생 졸업요건 부분 진단 서비스의 계산 계약을 검증한다.

package com.chukchuk.haksa.domain.graduation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferGraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.TransferManualReviewReason;
import com.chukchuk.haksa.domain.graduation.policy.DesignatedCourseEvaluator;
import com.chukchuk.haksa.domain.graduation.policy.TransferAreaEvaluator;
import com.chukchuk.haksa.domain.graduation.policy.TransferCourseEvaluator;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.domain.student.model.embeddable.AcademicInfo;
import com.chukchuk.haksa.domain.student.repository.StudentDesignatedCourseRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

  @Mock private TransferCourseEvaluator transferCourseEvaluator;

  @Mock private TransferAreaEvaluator transferAreaEvaluator;

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
    lenient()
        .when(studentAcademicRecordService.findStudentAcademicRecordByStudentId(STUDENT_ID))
        .thenReturn(Optional.of(academicRecord));
    lenient()
        .when(studentCourseRepository.findAllWithCourseByStudentId(STUDENT_ID))
        .thenReturn(List.of());
    lenient()
        .when(studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(STUDENT_ID))
        .thenReturn(List.of());
    TransferCourseEvaluator.Evaluation emptyCourseEvaluation =
        new TransferCourseEvaluator.Evaluation(List.of(), Map.of(), Map.of(), Set.of(), 0);
    lenient().when(transferCourseEvaluator.evaluate(anyList())).thenReturn(emptyCourseEvaluation);
    lenient().when(transferAreaEvaluator.evaluate(any(), any())).thenReturn(List.of());
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
    lenient()
        .when(
            designatedCourseEvaluator.evaluate(
                anyList(), any(TransferCourseEvaluator.Evaluation.class)))
        .thenReturn(null);
    when(studentCourseRepository.findAllWithCourseByStudentId(STUDENT_ID))
        .thenReturn(studentCourses);
    TransferCourseEvaluator.Evaluation courseEvaluation =
        new TransferCourseEvaluator.Evaluation(
            List.of(), Map.of("C101", 3), Map.of("C101", 3), Set.of(), 65);
    when(transferCourseEvaluator.evaluate(studentCourses)).thenReturn(courseEvaluation);
    when(designatedCourseEvaluator.evaluate(designatedCourses, courseEvaluation))
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
    assertThat(progress.designatedCourses())
        .extracting(DesignatedCourseProgressDto::status)
        .containsExactly(DesignatedCourseCompletionStatus.COMPLETED);
    assertThat(progress.designatedCoursesNeedsRefresh()).isTrue();
    assertThat(progress.designatedEarnedCredits()).isNull();
    assertThat(progress.designatedCreditUnavailableReasons())
        .containsExactly("SNAPSHOT_NOT_RECEIVED");
    assertThat(progress.manualReviewRequired()).isTrue();
    assertThat(progress.manualReviewReasons())
        .contains(TransferManualReviewReason.GRADUATION_REVIEW_NOT_AVAILABLE);
  }

  @Test
  void treatsReceivedEmptyDesignatedSnapshotAsFresh() {
    when(student.getDesignatedCoursesSnapshotVersion())
        .thenReturn(Instant.parse("2026-09-02T00:00:00Z"));
    when(academicRecord.getTotalEarnedCredits()).thenReturn(130);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("2.0"));

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.designatedCourses()).isEmpty();
    assertThat(progress.designatedCoursesNeedsRefresh()).isFalse();
    assertThat(progress.designatedEarnedCredits()).isZero();
    assertThat(progress.designatedCreditUnavailableReasons()).isEmpty();
  }

  @Test
  void keepsDesignatedCreditUnknownWhenDesignatedCodeIsMissing() {
    StudentDesignatedCourse designated = mock(StudentDesignatedCourse.class);
    List<StudentDesignatedCourse> designatedCourses = List.of(designated);
    when(studentDesignatedCourseRepository.findAllByStudentIdOrderBySourceOrder(STUDENT_ID))
        .thenReturn(designatedCourses);
    lenient()
        .when(
            designatedCourseEvaluator.evaluate(
                anyList(), any(TransferCourseEvaluator.Evaluation.class)))
        .thenReturn(null);
    when(designatedCourseEvaluator.evaluate(designatedCourses, List.of()))
        .thenReturn(
            new DesignatedCourseEvaluator.Evaluation(
                List.of(
                    new DesignatedCourseProgressDto(
                        null, "자료구조", 3, DesignatedCourseCompletionStatus.UNKNOWN)),
                0));
    when(student.getDesignatedCoursesSnapshotVersion())
        .thenReturn(Instant.parse("2026-09-02T00:00:00Z"));

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.designatedEarnedCredits()).isNull();
    assertThat(progress.designatedCreditUnavailableReasons())
        .containsExactly("COURSE_DATA_INCOMPLETE");
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
  void returnsPartialDiagnosisWhenAcademicSummaryRecordIsMissing() {
    when(studentAcademicRecordService.findStudentAcademicRecordByStudentId(STUDENT_ID))
        .thenReturn(Optional.empty());

    TransferGraduationProgressDto progress = service.analyze(student).getTransferProgress();

    assertThat(progress.totalEarnedCredits()).isNull();
    assertThat(progress.remainingCredits()).isNull();
    assertThat(progress.creditsFulfilled()).isNull();
    assertThat(progress.cumulativeGpa()).isNull();
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

  @Test
  void keepsPartialDiagnosisWithoutFinalEligibility() {
    when(student.getAcademicInfo())
        .thenReturn(
            AcademicInfo.builder()
                .admissionYear(2024)
                .completedSemesters(4)
                .isTransferStudent(true)
                .build());
    when(student.getDesignatedCoursesSnapshotVersion())
        .thenReturn(Instant.parse("2026-09-02T00:00:00Z"));
    when(studentGraduationProgressService.getLanguageCertFulfilled(STUDENT_ID))
        .thenReturn(Optional.of(true));
    when(academicRecord.getTotalEarnedCredits()).thenReturn(130);
    when(academicRecord.getCumulativeGpa()).thenReturn(new BigDecimal("3.2"));
    GraduationProgressResponse response = service.analyze(student);
    TransferGraduationProgressDto progress = response.getTransferProgress();

    assertThat(response.getAnalysisStatus())
        .isEqualTo(
            com.chukchuk.haksa.domain.graduation.dto.GraduationAnalysisStatus
                .MANUAL_REVIEW_REQUIRED);
    assertThat(progress.areas()).isEmpty();
  }
}
