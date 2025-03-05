package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;

import java.math.BigDecimal;

public class SemesterAcademicRecordDto {

    public record SemesterGradeDto(
            Integer year, // 이수년도
            String semester, // 학기 (1, 2, 여름, 겨울)
            Integer earnedCredits, // 취득 학점
            Integer attemptedCredits, // 신청 학점
            BigDecimal semesterGpa, // 평점 평균
            Integer classRank, // 석차 (있는 경우), null 가능
            Integer totalStudents, // 전체 학생 수(있는 경우), null 가능
            BigDecimal percentile // 백분율
    ) {
        public static SemesterGradeDto from(SemesterAcademicRecord record) {
            return new SemesterGradeDto(
                    record.getYear(),
                    String.valueOf(record.getSemester()),
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
