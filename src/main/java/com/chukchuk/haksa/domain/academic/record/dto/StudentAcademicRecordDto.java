package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public class StudentAcademicRecordDto {

    @Schema(description = "학업 성적 요약 정보")
    public record AcademicSummaryDto(
            @Schema(description = "총 취득 학점", example = "120") Integer totalEarnedCredits,
            @Schema(description = "누적 GPA", example = "3.76") BigDecimal cumulativeGpa,
            @Schema(description = "전체 백분위", example = "87.5") BigDecimal percentile
//            @Schema(description = "필요 졸업 학점", example = "130") Integer
//             requiredCredits
    ) {
        public static AcademicSummaryDto from(StudentAcademicRecord studentAcademicRecord) {
            return new AcademicSummaryDto(
                    studentAcademicRecord.getTotalEarnedCredits(),
                    studentAcademicRecord.getCumulativeGpa(),
                    studentAcademicRecord.getPercentile()
            );
        }
    }
}
