package com.chukchuk.haksa.domain.academic.record.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "학기별 성적 및 수강 과목 응답")
public record AcademicRecordResponse(
        @Schema(description = "학기 성적 정보") SemesterAcademicRecordDto.SemesterGradeDto semesterGrade,
        @Schema(description = "수강 과목 목록") Courses courses
//        @Schema(description = "학업 요약 정보") Summary summary
) {

    @Schema(description = "수강 과목 목록")
    public record Courses(
            @Schema(description = "전공 과목 목록") List<StudentCourseDto.CourseDetailDto> major,
            @Schema(description = "교양 과목 목록") List<StudentCourseDto.CourseDetailDto> liberal
    ) {}

//     Summary가 필요한가?
//    @Schema(description = "학업 요약 정보")
//    public record Summary(
//            @Schema(description = "총 취득 학점", example = "92") Integer totalEarnedCredits,
//            @Schema(description = "누적 GPA", example = "3.75") BigDecimal cumulativeGpa,
//            @Schema(description = "전공 GPA", example = "3.85") double majorGpa,
//            @Schema(description = "백분위", example = "88.5") BigDecimal percentile
//    ) {
//        public static Summary from(SemesterAcademicRecordDto.SemesterGradeDto semesterGrade, double majorGpa) {
//            Integer totalEarnedCredits = 0;
//            BigDecimal cumulativeGpa = BigDecimal.ZERO;
//            BigDecimal percentile = BigDecimal.ZERO;
//
//            if (semesterGrade != null) {
//                totalEarnedCredits = semesterGrade.earnedCredits();
//                cumulativeGpa = semesterGrade.semesterGpa();
//                percentile = semesterGrade.percentile();
//            }
//
//            return new Summary(
//                    totalEarnedCredits,
//                    cumulativeGpa,
//                    majorGpa,
//                    percentile
//            );
//        }
//    }
}