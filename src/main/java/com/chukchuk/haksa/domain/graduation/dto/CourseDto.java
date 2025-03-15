package com.chukchuk.haksa.domain.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseDto {
    private Integer year;  // 이수 연도
    private String courseName;  // 과목명
    private Integer credits;  // 학점
    private String grade;  // 성적
    private Integer semester;  // 이수 학기
}
