// 편입생의 영역별 취득학점을 기준과 비교해 응답으로 변환한다.

package com.chukchuk.haksa.domain.graduation.policy;

import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.graduation.dto.CourseDto;
import com.chukchuk.haksa.domain.graduation.dto.CourseInternalDto;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.TransferAreaEvaluationType;
import com.chukchuk.haksa.domain.graduation.dto.TransferAreaProgressDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/** 편입생 영역의 비교 방식과 취득학점을 계산하는 정책 평가기다. */
@Component
public class TransferAreaEvaluator {

  private static final String CORE_AREA = FacultyDivision.전핵.name();
  private static final String ELECTIVE_AREA = FacultyDivision.전선.name();
  private static final Set<String> TRANSFER_CREDIT_CODES =
      Set.of("07045", "07046", "00111", "07050");

  /**
   * 실제 이수 영역과 편입 전용 기준을 응답 영역으로 변환한다.
   *
   * @param evaluation 정규화한 이수 과목
   * @param requirements 검증된 전핵·전선 기준
   * @return 편입생 영역별 응답
   */
  public List<TransferAreaProgressDto> evaluate(
      TransferCourseEvaluator.Evaluation evaluation, Requirements requirements) {
    Map<String, List<CourseInternalDto>> coursesByArea = groupByArea(evaluation.courses());
    Set<String> areaNames = new LinkedHashSet<>(coursesByArea.keySet());
    areaNames.add(CORE_AREA);
    areaNames.add(ELECTIVE_AREA);

    List<TransferAreaProgressDto> result = new ArrayList<>();
    for (String areaName : orderedAreaNames(areaNames)) {
      List<CourseInternalDto> courses = coursesByArea.getOrDefault(areaName, List.of());
      if (CORE_AREA.equals(areaName)) {
        result.add(evaluateCore(courses, evaluation, requirements));
      } else if (ELECTIVE_AREA.equals(areaName)) {
        result.add(evaluateElective(courses, evaluation, requirements));
      } else {
        result.add(earnedOnly(areaName, courses, evaluation));
      }
    }
    return List.copyOf(result);
  }

  private TransferAreaProgressDto evaluateCore(
      List<CourseInternalDto> courses,
      TransferCourseEvaluator.Evaluation evaluation,
      Requirements requirements) {
    if (!requirements.coreUnavailableReasons().isEmpty()) {
      return unavailable(CORE_AREA, courses, evaluation, requirements.coreUnavailableReasons());
    }

    Map<String, CourseInternalDto> completedByCode = byCourseCode(courses);
    List<DesignatedCourseProgressDto> requiredCourses =
        requirements.coreCourses().stream()
            .map(
                required ->
                    new DesignatedCourseProgressDto(
                        required.courseCode(),
                        required.courseName(),
                        required.credits(),
                        completedByCode.containsKey(normalize(required.courseCode()))
                            ? DesignatedCourseCompletionStatus.COMPLETED
                            : DesignatedCourseCompletionStatus.NOT_COMPLETED))
            .toList();
    BigDecimal requiredCredits =
        requirements.coreCourses().stream()
            .map(RequiredCourse::credits)
            .map(BigDecimal::valueOf)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    Integer countedCredits = sumCreditsForCodes(requirements.coreCourses(), evaluation);
    Boolean fulfilled =
        countedCredits == null
            ? null
            : requiredCourses.stream()
                .allMatch(value -> value.status() == DesignatedCourseCompletionStatus.COMPLETED);

    return new TransferAreaProgressDto(
        FacultyDivision.전핵,
        TransferAreaEvaluationType.COMPARISON,
        sumCredits(courses, evaluation),
        countedCredits,
        requiredCredits,
        fulfilled,
        toCourseDtos(courses),
        requiredCourses,
        countedCredits == null ? List.of("COURSE_DATA_INCOMPLETE") : List.of());
  }

  private TransferAreaProgressDto evaluateElective(
      List<CourseInternalDto> courses,
      TransferCourseEvaluator.Evaluation evaluation,
      Requirements requirements) {
    if (!requirements.electiveUnavailableReasons().isEmpty()
        || requirements.electiveRequiredCredits() == null) {
      List<String> reasons =
          requirements.electiveUnavailableReasons().isEmpty()
              ? List.of("ELECTIVE_REQUIREMENT_UNAVAILABLE")
              : requirements.electiveUnavailableReasons();
      return unavailable(ELECTIVE_AREA, courses, evaluation, reasons);
    }

    Integer earnedCredits = sumCredits(courses, evaluation);
    Boolean fulfilled =
        earnedCredits == null
            ? null
            : BigDecimal.valueOf(earnedCredits).compareTo(requirements.electiveRequiredCredits())
                >= 0;
    return new TransferAreaProgressDto(
        FacultyDivision.전선,
        TransferAreaEvaluationType.COMPARISON,
        earnedCredits,
        earnedCredits,
        requirements.electiveRequiredCredits(),
        fulfilled,
        toCourseDtos(courses),
        List.of(),
        earnedCredits == null ? List.of("COURSE_DATA_INCOMPLETE") : List.of());
  }

  private TransferAreaProgressDto earnedOnly(
      String areaName,
      List<CourseInternalDto> courses,
      TransferCourseEvaluator.Evaluation evaluation) {
    Integer earnedCredits = sumCredits(courses, evaluation);
    return new TransferAreaProgressDto(
        parseArea(areaName),
        TransferAreaEvaluationType.EARNED_ONLY,
        earnedCredits,
        null,
        null,
        null,
        toCourseDtos(courses),
        List.of(),
        earnedCredits == null ? List.of("COURSE_DATA_INCOMPLETE") : List.of());
  }

  private TransferAreaProgressDto unavailable(
      String areaName,
      List<CourseInternalDto> courses,
      TransferCourseEvaluator.Evaluation evaluation,
      List<String> reasons) {
    return new TransferAreaProgressDto(
        parseArea(areaName),
        TransferAreaEvaluationType.UNAVAILABLE,
        sumCredits(courses, evaluation),
        null,
        null,
        null,
        toCourseDtos(courses),
        List.of(),
        List.copyOf(reasons));
  }

  private Map<String, List<CourseInternalDto>> groupByArea(List<CourseInternalDto> courses) {
    Map<String, List<CourseInternalDto>> grouped = new HashMap<>();
    for (CourseInternalDto course : courses) {
      if (course.getCourseCode() != null
          && TRANSFER_CREDIT_CODES.contains(course.getCourseCode())) {
        continue;
      }
      String area =
          course.getAreaType() == null || course.getAreaType().isBlank()
              ? FacultyDivision.기타.name()
              : course.getAreaType().trim();
      grouped.computeIfAbsent(area, key -> new ArrayList<>()).add(course);
    }
    return grouped;
  }

  private Map<String, CourseInternalDto> byCourseCode(List<CourseInternalDto> courses) {
    Map<String, CourseInternalDto> result = new HashMap<>();
    for (CourseInternalDto course : courses) {
      if (course.getCourseCode() != null) {
        result.put(normalize(course.getCourseCode()), course);
      }
    }
    return result;
  }

  private Integer sumCredits(
      List<CourseInternalDto> courses, TransferCourseEvaluator.Evaluation evaluation) {
    int total = 0;
    for (CourseInternalDto course : courses) {
      if (course.getCourseCode() == null
          || course.getCredits() == null
          || evaluation.unknownCreditCourseCodes().contains(course.getCourseCode())) {
        return null;
      }
      total += course.getCredits();
    }
    return total;
  }

  private Integer sumCreditsForCodes(
      List<RequiredCourse> requiredCourses, TransferCourseEvaluator.Evaluation evaluation) {
    Set<String> requiredCodes =
        requiredCourses.stream()
            .map(RequiredCourse::courseCode)
            .map(this::normalize)
            .collect(java.util.stream.Collectors.toSet());
    int total = 0;
    for (String requiredCode : requiredCodes) {
      if (evaluation.unknownCreditCourseCodes().contains(requiredCode)) {
        return null;
      }
      Integer credits = evaluation.creditsByCourseCode().get(requiredCode);
      if (credits != null) {
        total += credits;
      }
    }
    return total;
  }

  private List<CourseDto> toCourseDtos(List<CourseInternalDto> courses) {
    return courses.stream()
        .map(
            course ->
                new CourseDto(
                    course.getYear(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getGrade(),
                    course.getSemester(),
                    course.getLiberalAreaCode()))
        .toList();
  }

  private FacultyDivision parseArea(String areaName) {
    try {
      return FacultyDivision.valueOf(areaName);
    } catch (IllegalArgumentException exception) {
      return FacultyDivision.기타;
    }
  }

  private List<String> orderedAreaNames(Set<String> areaNames) {
    return areaNames.stream().sorted(Comparator.comparingInt(this::areaOrder)).toList();
  }

  private int areaOrder(String areaName) {
    try {
      return FacultyDivision.valueOf(areaName).ordinal();
    } catch (IllegalArgumentException exception) {
      return FacultyDivision.values().length;
    }
  }

  private String normalize(String value) {
    return value == null ? "" : value.trim().toUpperCase();
  }

  /**
   * 검증된 편입생 기준을 보관한다.
   *
   * @param coreCourses 검증된 전핵 대상 과목
   * @param electiveRequiredCredits 검증된 전선 필요학점
   * @param coreUnavailableReasons 전핵 기준 미확인 사유
   * @param electiveUnavailableReasons 전선 기준 미확인 사유
   */
  public record Requirements(
      List<RequiredCourse> coreCourses,
      BigDecimal electiveRequiredCredits,
      List<String> coreUnavailableReasons,
      List<String> electiveUnavailableReasons) {

    /**
     * 기준을 아직 확인하지 못한 상태를 생성한다.
     *
     * @return 미확인 전핵·전선 기준
     */
    public static Requirements unavailable() {
      return new Requirements(
          List.of(),
          null,
          List.of("CORE_CURRICULUM_UNAVAILABLE"),
          List.of("ELECTIVE_REQUIREMENT_UNAVAILABLE"));
    }

    /**
     * 목록 필드를 방어적으로 보관한다.
     *
     * @param coreCourses 검증된 전핵 대상 과목
     * @param electiveRequiredCredits 검증된 전선 필요학점
     * @param coreUnavailableReasons 전핵 기준 미확인 사유
     * @param electiveUnavailableReasons 전선 기준 미확인 사유
     */
    public Requirements {
      coreCourses = List.copyOf(coreCourses);
      coreUnavailableReasons = List.copyOf(coreUnavailableReasons);
      electiveUnavailableReasons = List.copyOf(electiveUnavailableReasons);
    }
  }

  /** 전핵 대상 한 과목의 기준 정보다. */
  public record RequiredCourse(String courseCode, String courseName, int credits) {}
}
