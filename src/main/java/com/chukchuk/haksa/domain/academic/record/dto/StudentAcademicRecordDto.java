package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;

import java.math.BigDecimal;

public class StudentAcademicRecordDto {

    public record AcademicSummaryDto(
            Integer totalEarnedCredits,
            BigDecimal cumulativeGpa,
            BigDecimal percentile
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
