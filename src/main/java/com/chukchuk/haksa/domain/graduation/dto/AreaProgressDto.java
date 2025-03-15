package com.chukchuk.haksa.domain.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AreaProgressDto {
    private String areaType;  // 영역 유형
    private Integer requiredCredits;  // 해당 영역에서 필요한 학점
    private Integer earnedCredits;  // 학생이 이수한 학점
    private Integer requiredElectiveCourses;  // 필수 선택 과목 수
    private Integer completedElectiveCourses;  // 학생이 이수한 선택 과목 수
    private Integer totalElectiveCourses;  // 총 선택 과목 수
    private List<CourseDto> courses;  // 학생이 이수한 과목 목록
}
