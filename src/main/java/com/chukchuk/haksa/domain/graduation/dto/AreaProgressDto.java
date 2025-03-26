package com.chukchuk.haksa.domain.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "졸업 요건 영역별 이수 현황")
public class AreaProgressDto {
    @Schema(description = "영역 유형 (예: 전핵, 전선, 선교 등)", example = "전공")
    private String areaType;
    @Schema(description = "해당 영역에서 필요한 학점", example = "60")
    private Integer requiredCredits;
    @Schema(description = "학생이 이수한 학점", example = "45")
    private Integer earnedCredits;
    @Schema(description = "필수 선택 과목 수", example = "2")
    private Integer requiredElectiveCourses;
    @Schema(description = "학생이 이수한 선택 과목 수", example = "1")
    private Integer completedElectiveCourses;
    @Schema(description = "총 선택 과목 수", example = "3")
    private Integer totalElectiveCourses;
    @Schema(description = "학생이 이수한 과목 목록")
    private List<CourseDto> courses;
}
