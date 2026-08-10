package com.chukchuk.haksa.domain.academic.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 한 학기의 성적과 전공·교양·기타 영역별 수강 과목을 묶는다.
 *
 * @param semesterGrade 응답에 포함할 학기 성적
 * @param summary 저장하거나 비교할 학사 요약
 */
@Schema(description = "학기별 성적 및 수강 과목 응답")
public record AcademicRecordResponse(
    @Schema(description = "학기 성적 정보", required = true)
        SemesterAcademicRecordDto.SemesterGradeResponse semesterGrade,
    @Schema(description = "수강 과목 목록", required = true) Courses courses
    //        @Schema(description = "학업 요약 정보") Summary summary
    ) {

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param major 주전공
   * @param liberal 응답에 포함할 liberal
   * @param etc 응답에 포함할 etc
   */
  @Schema(description = "수강 과목 목록")
  public record Courses(
      @Schema(description = "전공 과목 목록", required = true)
          List<StudentCourseDto.CourseDetailDto> major,
      @Schema(description = "교양 과목 목록", required = true)
          List<StudentCourseDto.CourseDetailDto> liberal,
      @Schema(description = "기타 과목 목록", required = true)
          List<StudentCourseDto.CourseDetailDto> etc) {}
}
