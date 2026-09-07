// 편입생 영역별 취득학점과 기준 비교 규칙을 검증한다.

package com.chukchuk.haksa.domain.graduation.policy;

import static org.assertj.core.api.Assertions.assertThat;

import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.graduation.dto.CourseInternalDto;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.TransferAreaEvaluationType;
import com.chukchuk.haksa.domain.graduation.dto.TransferAreaProgressDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TransferAreaEvaluatorTest {

  private final TransferAreaEvaluator evaluator = new TransferAreaEvaluator();

  @Test
  void keepsEarnedOnlyAreaWithoutInventingRequiredCredits() {
    TransferCourseEvaluator.Evaluation courses = evaluation(course("일선", "E101", 6));

    List<TransferAreaProgressDto> result =
        evaluator.evaluate(courses, TransferAreaEvaluator.Requirements.unavailable());

    TransferAreaProgressDto area =
        result.stream()
            .filter(value -> value.areaType() == FacultyDivision.일선)
            .findFirst()
            .orElseThrow();
    assertThat(area.evaluationType()).isEqualTo(TransferAreaEvaluationType.EARNED_ONLY);
    assertThat(area.earnedCredits()).isEqualTo(6);
    assertThat(area.requiredCredits()).isNull();
    assertThat(area.fulfilled()).isNull();
  }

  @Test
  void marksRequiredCoreCourseMissingEvenWhenOtherCoreCreditsAreEnough() {
    TransferCourseEvaluator.Evaluation courses =
        evaluation(course("전핵", "C301", 3), course("전핵", "C999", 3));
    TransferAreaEvaluator.Requirements requirements =
        new TransferAreaEvaluator.Requirements(
            List.of(
                new TransferAreaEvaluator.RequiredCourse("C301", "자료구조", 3),
                new TransferAreaEvaluator.RequiredCourse("C401", "운영체제", 3)),
            BigDecimal.valueOf(48),
            List.of(),
            List.of());

    TransferAreaProgressDto core =
        evaluator.evaluate(courses, requirements).stream()
            .filter(value -> value.areaType() == FacultyDivision.전핵)
            .findFirst()
            .orElseThrow();

    assertThat(core.evaluationType()).isEqualTo(TransferAreaEvaluationType.COMPARISON);
    assertThat(core.earnedCredits()).isEqualTo(6);
    assertThat(core.countedCredits()).isEqualTo(3);
    assertThat(core.requiredCredits()).isEqualByComparingTo("6");
    assertThat(core.fulfilled()).isFalse();
    assertThat(core.requiredCourses())
        .extracting(value -> value.status())
        .containsExactly(
            DesignatedCourseCompletionStatus.COMPLETED,
            DesignatedCourseCompletionStatus.NOT_COMPLETED);
  }

  @Test
  void reportsUnavailableComparisonWithoutDroppingKnownEarnedCredits() {
    TransferCourseEvaluator.Evaluation courses = evaluation(course("전선", "E101", 45));
    TransferAreaEvaluator.Requirements requirements =
        new TransferAreaEvaluator.Requirements(
            List.of(),
            null,
            List.of("CORE_CURRICULUM_UNAVAILABLE"),
            List.of("ELECTIVE_REQUIREMENT_UNAVAILABLE"));

    TransferAreaProgressDto elective =
        evaluator.evaluate(courses, requirements).stream()
            .filter(value -> value.areaType() == FacultyDivision.전선)
            .findFirst()
            .orElseThrow();

    assertThat(elective.evaluationType()).isEqualTo(TransferAreaEvaluationType.UNAVAILABLE);
    assertThat(elective.earnedCredits()).isEqualTo(45);
    assertThat(elective.requiredCredits()).isNull();
    assertThat(elective.fulfilled()).isNull();
    assertThat(elective.unavailableReasons()).containsExactly("ELECTIVE_REQUIREMENT_UNAVAILABLE");
  }

  private TransferCourseEvaluator.Evaluation evaluation(CourseInternalDto... courses) {
    Map<String, Integer> earned =
        java.util.Arrays.stream(courses)
            .filter(value -> value.getCredits() != null)
            .collect(
                java.util.stream.Collectors.toMap(
                    CourseInternalDto::getCourseCode, CourseInternalDto::getCredits));
    return new TransferCourseEvaluator.Evaluation(List.of(courses), earned, earned, Set.of(), 0);
  }

  private CourseInternalDto course(String area, String code, int credits) {
    return new CourseInternalDto(1L, area, credits, "A0", code, 1, 2025, code, 90, null);
  }
}
