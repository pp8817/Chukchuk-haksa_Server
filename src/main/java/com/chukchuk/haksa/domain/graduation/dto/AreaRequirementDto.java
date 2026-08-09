package com.chukchuk.haksa.domain.graduation.dto;

/**
 * 계층 간 전달할 데이터를 표현한다.
 *
 * @param areaType area type 값
 * @param requiredCredits required 학점
 * @param requiredElectiveCourses required elective courses 값
 * @param totalElectiveCourses total elective courses 값
 */
public record AreaRequirementDto(
    String areaType,
    int requiredCredits,
    Integer requiredElectiveCourses,
    Integer totalElectiveCourses) {}
