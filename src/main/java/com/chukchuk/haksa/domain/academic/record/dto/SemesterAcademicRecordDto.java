package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class SemesterAcademicRecordDto {

    @Schema(description = "학기 성적 요약 정보")
    public record SemesterGradeResponse(
            @Schema(description = "이수 연도", example = "2024", required = true) Integer year,
            @Schema(description = "학기 코드 (10: 1학기, 15: 여름학기, 20: 2학기, 25: 겨울학기)", example = "10", required = true) Integer semester,
            @Schema(description = "취득 학점", example = "15", required = true) Integer earnedCredits,
            @Schema(description = "신청 학점", example = "18", required = true) Integer attemptedCredits,
            @Schema(description = "학기 GPA (평점 평균)", example = "3.85", required = true) BigDecimal semesterGpa,
            @Schema(description = "석차", example = "5", nullable = true, required = true) Integer classRank,
            @Schema(description = "전체 학생 수", example = "150", nullable = true, required = true) Integer totalStudents,
            @Schema(description = "백분율", example = "92.4", required = true) BigDecimal percentile
    ) {
        public static SemesterGradeResponse from(SemesterAcademicRecord record) {
            return new SemesterGradeResponse(
                    record.getYear(),
                    record.getSemester(),
                    record.getEarnedCredits(),
                    record.getAttemptedCredits(),
                    record.getSemesterGpa(),
                    record.getClassRank(),
                    record.getTotalStudents(),
                    record.getSemesterPercentile()
            );
        }
    }
}
