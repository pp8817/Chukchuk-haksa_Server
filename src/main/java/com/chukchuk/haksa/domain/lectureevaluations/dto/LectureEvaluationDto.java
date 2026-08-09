package com.chukchuk.haksa.domain.lectureevaluations.dto;

import com.chukchuk.haksa.domain.academic.record.model.LectureEvaluationStatus;
import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import com.chukchuk.haksa.domain.course.model.LiberalArtsAreaCode;
import com.chukchuk.haksa.domain.lectureevaluations.model.LectureEvaluationTag;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** 계층 간 전달할 데이터를 표현한다. */
public class LectureEvaluationDto {

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param evaluationStatus evaluation 상태
   * @param year 연도
   * @param semester 학기 값
   * @param grades grades 값
   */
  public record RequiredResponse(
      @Schema(
              description = "강의평가 상태",
              allowableValues = {"NOT_RELEASED", "PENDING", "SKIPPED", "COMPLETED"},
              nullable = true)
          LectureEvaluationStatus evaluationStatus,
      @Schema(description = "강의평가 대상 연도") Integer year,
      @Schema(description = "강의평가 대상 학기 코드") Integer semester,
      @Schema(description = "성적 카드 목록") List<GradeCard> grades) {
    /**
     * 강의평가 대상 학기의 성적 없는 응답을 생성한다.
     *
     * @param evaluationStatus 강의평가 상태
     * @param year 연도
     * @param semester 학기
     * @return 성적 목록이 비어 있는 강의평가 응답
     */
    public static RequiredResponse withoutGrades(
        LectureEvaluationStatus evaluationStatus, Integer year, Integer semester) {
      return new RequiredResponse(evaluationStatus, year, semester, List.of());
    }
  }

  /**
   * 성적 card 데이터를 전달한다.
   *
   * @param courseName 과목 이름
   * @param courseCode 과목 코드
   * @param courseId 과목 식별자
   * @param areaType area type 값
   * @param credits 학점
   * @param professor 교수
   * @param professorId 교수 식별자
   * @param grade 성적 값
   * @param score 점수
   * @param liberalAreaCode liberal area code 값
   */
  public record GradeCard(
      String courseName,
      String courseCode,
      Long courseId,
      FacultyDivision areaType,
      Integer credits,
      String professor,
      Long professorId,
      String grade,
      Integer score,
      @JsonInclude(JsonInclude.Include.NON_NULL) Integer liberalAreaCode) {
    /**
     * 수강 내역으로 강의평가용 성적 카드를 생성한다.
     *
     * @param studentCourse 수강 내역
     * @return 강의평가용 성적 카드
     */
    public static GradeCard from(StudentCourse studentCourse) {
      return new GradeCard(
          studentCourse.getOffering().getCourse().getCourseName(),
          studentCourse.getOffering().getCourse().getCourseCode(),
          studentCourse.getOffering().getCourse().getId(),
          studentCourse.getOffering().getFacultyDivisionName(),
          studentCourse.getOffering().getPoints(),
          studentCourse.getOffering().getProfessor() != null
              ? studentCourse.getOffering().getProfessor().getProfessorName()
              : "미지정",
          studentCourse.getOffering().getProfessor() != null
              ? studentCourse.getOffering().getProfessor().getId()
              : null,
          studentCourse.getGrade() != null && studentCourse.getGrade().getValue() != null
              ? studentCourse.getGrade().getValue().getValue()
              : null,
          studentCourse.getOriginalScore(),
          missionLiberalAreaCode(studentCourse));
    }

    private static Integer missionLiberalAreaCode(StudentCourse studentCourse) {
      if (studentCourse.getOffering().getFacultyDivisionName() != FacultyDivision.선교) {
        return null;
      }

      LiberalArtsAreaCode areaCode = studentCourse.getOffering().getLiberalArtsAreaCode();
      return areaCode != null ? areaCode.getCode() : null;
    }
  }

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param year 연도
   * @param semester 학기 값
   * @param evaluations evaluations 값
   */
  public record SubmitRequest(
      @NotNull Integer year,
      @NotNull Integer semester,
      @Valid @NotNull @Size(min = 1) List<SubmitEvaluation> evaluations) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param courseId 과목 식별자
   * @param professorId 교수 식별자
   * @param selectedTags selected tags 값
   * @param review review 값
   */
  public record SubmitEvaluation(
      @NotNull Long courseId,
      @NotNull Long professorId,
      @NotNull List<LectureEvaluationTag> selectedTags,
      @Size(max = 2000) String review) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param year 연도
   * @param semester 학기 값
   */
  public record SkipRequest(@NotNull Integer year, @NotNull Integer semester) {}
}
