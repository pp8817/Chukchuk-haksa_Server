package com.chukchuk.haksa.domain.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이수 과목 정보")
public class CourseDto {
    @Schema(description = "이수 연도", example = "2023", required = true)
    private Integer year;
    @Schema(description = "과목명", example = "자료구조", required = true)
    private String courseName;
    @Schema(description = "학점", example = "3", required = true)
    private Integer credits;
    @Schema(description = "성적", example = "A+", required = true)
    private String grade;
    @Schema(description = "이수 학기", example = "10", required = true)
    private Integer semester;
}
