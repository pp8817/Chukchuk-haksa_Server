// 편입생 전용 유효 수강 과목 정규화 규칙을 검증한다.

package com.chukchuk.haksa.domain.graduation.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.course.model.Course;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.graduation.dto.CourseInternalDto;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.student.model.Grade;
import com.chukchuk.haksa.domain.student.model.GradeType;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import com.chukchuk.haksa.infrastructure.portal.model.DesignatedCourseData;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

class TransferCourseEvaluatorTest {

  private final TransferCourseEvaluator evaluator = new TransferCourseEvaluator();

  @Test
  @ResourceLock(Resources.LOCALE)
  void matchesDesignatedCourseCodesUnderTurkishLocale() {
    Locale original = Locale.getDefault();
    try {
      Locale.setDefault(Locale.forLanguageTag("tr-TR"));
      StudentCourse course = course(" i101 ", FacultyDivision.전핵, 2025, 1, 3, GradeType.P);
      StudentDesignatedCourse designated =
          new StudentDesignatedCourse(
              null, new DesignatedCourseData(null, "i101", "자료구조", 3, null, null, null, null, 0));

      TransferCourseEvaluator.Evaluation result = evaluator.evaluate(List.of(course));
      DesignatedCourseEvaluator.Evaluation designatedResult =
          new DesignatedCourseEvaluator().evaluate(List.of(designated), result);

      assertThat(result.creditsByCourseCode()).containsEntry("I101", 3);
      assertThat(designatedResult.designatedCourses().get(0).status())
          .isEqualTo(DesignatedCourseCompletionStatus.COMPLETED);
    } finally {
      Locale.setDefault(original);
    }
  }

  @Test
  void keepsLatestPassingCourseOnceAndExcludesInvalidGrades() {
    StudentCourse oldCourse = course(" C101 ", FacultyDivision.전핵, 2024, 2, 3, GradeType.B0);
    StudentCourse latestCourse = course("C101", FacultyDivision.전핵, 2025, 1, 4, GradeType.A0);
    StudentCourse failedCourse = course("C102", FacultyDivision.전핵, 2025, 1, 3, GradeType.NP);
    StudentCourse deletedCourse = course("C103", FacultyDivision.전선, 2025, 1, 3, GradeType.A0);
    when(deletedCourse.isRetakeDeleted()).thenReturn(true);

    TransferCourseEvaluator.Evaluation result =
        evaluator.evaluate(List.of(oldCourse, latestCourse, failedCourse, deletedCourse));

    assertThat(result.courses()).hasSize(1);
    CourseInternalDto selected = result.courses().get(0);
    assertThat(selected.getCourseCode()).isEqualTo("C101");
    assertThat(selected.getCredits()).isEqualTo(4);
    assertThat(result.unknownCreditCourseCodes()).isEmpty();
  }

  @Test
  void preservesMissingPersonalCreditsInsteadOfUsingOfferingCredits() {
    StudentCourse course = course("C101", FacultyDivision.전선, 2025, 1, null, GradeType.P);
    when(course.getOffering().getPoints()).thenReturn(3);

    TransferCourseEvaluator.Evaluation result = evaluator.evaluate(List.of(course));

    assertThat(result.courses().get(0).getCredits()).isNull();
    assertThat(result.unknownCreditCourseCodes()).containsExactly("C101");
    assertThat(result.earnedCreditsByCourseCode()).doesNotContainKey("C101");
  }

  @Test
  void calculatesRecognizedTransferCreditsByCodeUsingLargestValidCredit() {
    StudentCourse smaller = course("07045", FacultyDivision.일선, 2024, 1, 15, GradeType.P);
    StudentCourse larger = course("07045", FacultyDivision.일선, 2024, 2, 18, GradeType.P);
    StudentCourse other = course("07050", FacultyDivision.일선, 2024, 1, 17, GradeType.P);

    TransferCourseEvaluator.Evaluation result = evaluator.evaluate(List.of(smaller, larger, other));

    assertThat(result.recognizedTransferCredits()).isEqualTo(35);
    assertThat(result.creditsByCourseCode()).containsEntry("07045", 18).containsEntry("07050", 17);
    assertThat(result.earnedCreditsByCourseCode()).doesNotContainKey("07045");
    assertThat(result.earnedCreditsByCourseCode()).doesNotContainKey("07050");
  }

  @Test
  void keepsUncodedCourseVisibleWithoutUsingItForCodeBasedAggregation() {
    StudentCourse uncoded = course(" ", FacultyDivision.기타, 2025, 1, null, GradeType.P);

    TransferCourseEvaluator.Evaluation result = evaluator.evaluate(List.of(uncoded));

    assertThat(result.courses())
        .singleElement()
        .satisfies(
            course -> {
              assertThat(course.getCourseCode()).isNull();
              assertThat(course.getCredits()).isNull();
            });
    assertThat(result.creditsByCourseCode()).isEmpty();
  }

  @Test
  void marksSameSemesterConflictingResultsAsUnknown() {
    StudentCourse first = course("C101", FacultyDivision.전핵, 2025, 1, 3, GradeType.A0);
    StudentCourse second = course("C101", FacultyDivision.전핵, 2025, 1, 4, GradeType.A0);

    TransferCourseEvaluator.Evaluation result = evaluator.evaluate(List.of(first, second));

    assertThat(result.unknownCreditCourseCodes()).containsExactly("C101");
  }

  private StudentCourse course(
      String code,
      FacultyDivision area,
      int year,
      int semester,
      Integer credits,
      GradeType gradeType) {
    Course course = mock(Course.class);
    when(course.getCourseCode()).thenReturn(code);
    when(course.getCourseName()).thenReturn(code + " 과목");

    CourseOffering offering = mock(CourseOffering.class);
    when(offering.getCourse()).thenReturn(course);
    when(offering.getFacultyDivisionName()).thenReturn(area);
    when(offering.getYear()).thenReturn(year);
    when(offering.getSemester()).thenReturn(semester);
    when(offering.getPoints()).thenReturn(credits);

    StudentCourse studentCourse = mock(StudentCourse.class);
    when(studentCourse.getOffering()).thenReturn(offering);
    when(studentCourse.getGrade()).thenReturn(new Grade(gradeType));
    when(studentCourse.getPoints()).thenReturn(credits);
    when(studentCourse.isRetakeDeleted()).thenReturn(false);
    return studentCourse;
  }
}
