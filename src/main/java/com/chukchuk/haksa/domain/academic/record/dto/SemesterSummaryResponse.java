package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * 학기 summary 응답 데이터를 전달한다.
 *
 * @param year 연도
 * @param semester 학기 값
 * @param earnedCredits 취득 학점
 * @param attemptedCredits 신청 학점
 * @param semesterGpa 학기 평점
 * @param classRank class rank 값
 * @param totalStudents total students 값
 * @param percentile percentile 값
 */
@Schema(description = "학기 요약 정보 (성적 포함)")
public record SemesterSummaryResponse(
    @Schema(description = "이수 연도", example = "2023", required = true) int year,
    @Schema(
            description = "학기 코드 (10: 1학기, 15: 여름학기, 20: 2학기, 25: 겨울학기)",
            example = "10",
            required = true)
        int semester,

    // 아래 필드는 getAllSemesterGrades에서만 필요하므로 nullable
    @Schema(description = "취득 학점", example = "15", nullable = true) Integer earnedCredits,
    @Schema(description = "신청 학점", example = "18", nullable = true) Integer attemptedCredits,
    @Schema(description = "학기 GPA (평점 평균)", example = "3.85", nullable = true)
        BigDecimal semesterGpa,
    @Schema(description = "석차", example = "5", nullable = true) Integer classRank,
    @Schema(description = "전체 학생 수", example = "150", nullable = true) Integer totalStudents,
    @Schema(description = "백분율", example = "92.4", nullable = true) BigDecimal percentile) {
  /**
   * 학기 학사 기록을 성적 요약 응답으로 변환한다.
   *
   * @param record 학기 학사 기록
   * @return 학기 성적 요약 응답
   */
  public static SemesterSummaryResponse from(SemesterAcademicRecord record) {
    return new SemesterSummaryResponse(
        record.getYear(),
        record.getSemester(),
        record.getEarnedCredits(),
        record.getAttemptedCredits(),
        record.getSemesterGpa(),
        record.getClassRank(),
        record.getTotalStudents(),
        record.getSemesterPercentile());
  }
}
