// 지정과목 이수 여부와 편입 인정학점을 수강 기록에서 계산한다.

package com.chukchuk.haksa.domain.graduation.policy;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseCompletionStatus;
import com.chukchuk.haksa.domain.graduation.dto.DesignatedCourseProgressDto;
import com.chukchuk.haksa.domain.student.model.GradeType;
import com.chukchuk.haksa.domain.student.model.StudentDesignatedCourse;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/** 지정과목과 실제 수강 기록을 비교하는 저장소 비의존 평가기다. */
@Component
public class DesignatedCourseEvaluator {

  private static final Set<String> TRANSFER_CREDIT_CODES =
      Set.of("07045", "07046", "00111", "07050");
  private static final Set<GradeType> NON_PASSING_GRADES =
      EnumSet.of(GradeType.F, GradeType.R, GradeType.NP, GradeType.IP);

  /** 지정과목 이수 상태와 편입 인정학점을 함께 계산한다.
   *
   * @param designatedCourses 학생에게 저장된 지정과목 원본 목록
   * @param studentCourses 학생의 전체 수강 기록
   * @return 지정과목 상태 목록과 편입 인정학점
   */
  public Evaluation evaluate(
      List<StudentDesignatedCourse> designatedCourses, List<StudentCourse> studentCourses) {
    Map<String, Integer> completedCourseCredits = new HashMap<>();
    for (StudentCourse studentCourse : studentCourses) {
      if (!isValidCompletedCourse(studentCourse)) {
        continue;
      }

      String courseCode = normalizeCode(studentCourse.getOffering().getCourse().getCourseCode());
      if (courseCode == null) {
        continue;
      }

      Integer credits = studentCourse.getPoints();
      if (credits == null) {
        credits = 0;
      }
      completedCourseCredits.merge(courseCode, credits, Math::max);
    }

    List<DesignatedCourseProgressDto> progress =
        designatedCourses.stream()
            .map(course -> toProgress(course, completedCourseCredits))
            .toList();

    int recognizedTransferCredits =
        TRANSFER_CREDIT_CODES.stream()
            .mapToInt(code -> completedCourseCredits.getOrDefault(code, 0))
            .sum();
    return new Evaluation(progress, recognizedTransferCredits);
  }

  private DesignatedCourseProgressDto toProgress(
      StudentDesignatedCourse designatedCourse, Map<String, Integer> completedCourseCredits) {
    String normalizedCode = normalizeCode(designatedCourse.getSubjtCd());
    DesignatedCourseCompletionStatus status;
    if (normalizedCode == null) {
      status = DesignatedCourseCompletionStatus.UNKNOWN;
    } else if (completedCourseCredits.containsKey(normalizedCode)) {
      status = DesignatedCourseCompletionStatus.COMPLETED;
    } else {
      status = DesignatedCourseCompletionStatus.NOT_COMPLETED;
    }

    return new DesignatedCourseProgressDto(
        normalizedCode, designatedCourse.getSubjtNm(), designatedCourse.getPoint(), status);
  }

  private boolean isValidCompletedCourse(StudentCourse studentCourse) {
    return studentCourse.getGrade() != null
        && !NON_PASSING_GRADES.contains(studentCourse.getGrade().getValue())
        && !studentCourse.isRetakeDeleted();
  }

  private String normalizeCode(String code) {
    if (code == null || code.isBlank()) {
      return null;
    }
    return code.trim().toUpperCase(Locale.ROOT);
  }

  /** 지정과목 평가 결과를 보관한다.
   *
   * @param designatedCourses 지정과목별 이수 상태
   * @param recognizedTransferCredits 편입 인정학점 합계
   */
  public record Evaluation(
      List<DesignatedCourseProgressDto> designatedCourses, int recognizedTransferCredits) {
    public Evaluation {
      designatedCourses = List.copyOf(designatedCourses);
    }
  }
}
