package com.chukchuk.haksa.domain.course.dto;

/**
 * 계층 간 전달할 데이터를 표현한다.
 *
 * @param courseId 과목 식별자
 * @param year 연도
 * @param semester 대상 학기
 * @param classSection 분반
 * @param professorId 교수 식별자
 * @param departmentId 학과 식별자
 * @param scheduleSummary 응답에 포함할 schedule summary
 * @param evaluationType 응답에 포함할 evaluation type
 * @param isVideoLecture is video lecture 여부
 * @param subjectEstablishmentSemester 응답에 포함할 subject establishment 학기
 * @param facultyDivisionName faculty division 이름
 * @param areaCode 응답에 포함할 area code
 * @param originalAreaCode 응답에 포함할 original area code
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
