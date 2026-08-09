package com.chukchuk.haksa.domain.course.dto;

/**
 * 계층 간 전달할 데이터를 표현한다.
 *
 * @param courseId 과목 식별자
 * @param year 연도
 * @param semester 학기 값
 * @param classSection 분반
 * @param professorId 교수 식별자
 * @param departmentId 학과 식별자
 * @param scheduleSummary schedule summary 값
 * @param evaluationType evaluation type 값
 * @param isVideoLecture is video lecture 여부
 * @param subjectEstablishmentSemester subject establishment 학기 값
 * @param facultyDivisionName faculty division 이름
 * @param areaCode area code 값
 * @param originalAreaCode original area code 값
 * @param points 학점
 * @param hostDepartment 주관 학과
 */
public record CreateOfferingCommand(
    Long courseId,
    Integer year,
    Integer semester,
    String classSection,
    Long professorId,
    Long departmentId,
    String scheduleSummary,
    String evaluationType, // EvaluationType name (ex: "ABSOLUTE")
    Boolean isVideoLecture,
    Integer subjectEstablishmentSemester,
    String facultyDivisionName,
    Integer areaCode,
    Integer originalAreaCode,
    Integer points,
    String hostDepartment) {}
