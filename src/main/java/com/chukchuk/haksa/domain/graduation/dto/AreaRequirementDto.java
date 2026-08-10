package com.chukchuk.haksa.domain.graduation.dto;

/**
 * 계층 간 전달할 데이터를 표현한다.
 *
 * @param areaType 졸업 요건을 구분하는 영역
 * @param requiredCredits required 학점
 * @param requiredElectiveCourses 응답에 포함할 required elective courses
 * @param totalElectiveCourses 응답에 포함할 total elective courses
 */
public record AreaRequirementDto(
    String areaType,
    int requiredCredits,
    Integer requiredElectiveCourses,
    Integer totalElectiveCourses) {}
