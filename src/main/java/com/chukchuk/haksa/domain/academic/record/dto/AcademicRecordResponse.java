package com.chukchuk.haksa.domain.academic.record.dto;

import java.math.BigDecimal;
import java.util.List;

public record AcademicRecordResponse(
        List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades,
        Courses courses,
        Summary summary
) {
    // TODO: 타입 수정
    public record Courses(
            List<StudentCourseDto.CourseDetailDto> major,
            List<StudentCourseDto.CourseDetailDto> liberal
    ) {}

    public record Summary(
            Integer totalEarnedCredits,  // 총 취득학점
            BigDecimal cumulativeGpa,        // 전체 평점 평균
            double majorGpa,             // 전공 평점 평균
            BigDecimal percentile           // 백분위
    ) {
        public static Summary from(List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades, double majorGpa) {
            Integer totalEarnedCredits = 0;
            BigDecimal cumulativeGpa = BigDecimal.ZERO;
            BigDecimal percentile = BigDecimal.ZERO;

            if (!semesterGrades.isEmpty()) {
                SemesterAcademicRecordDto.SemesterGradeDto firstGrade = semesterGrades.get(0);
                totalEarnedCredits = firstGrade.earnedCredits();
                cumulativeGpa = firstGrade.semesterGpa();
                percentile = firstGrade.percentile();
            }

            return new Summary(
                    totalEarnedCredits,
                    cumulativeGpa,
                    majorGpa,
                    percentile
            );
        }
    }
}