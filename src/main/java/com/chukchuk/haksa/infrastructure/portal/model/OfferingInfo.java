package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 offering info 데이터를 표현한다.
 *
 * @param courseCode 과목 코드
 * @param year 연도
 * @param semester 학기 값
 * @param classSection 분반
 * @param professorName professor 이름
 * @param scheduleSummary schedule summary 값
 * @param points 학점
 * @param hostDepartment 주관 학과
 * @param facultyDivisionName faculty division 이름
 * @param subjectEstablishmentSemester subject establishment 학기 값
 * @param areaCode area code 값
 * @param originalAreaCode original area code 값
 * @param evaluationType evaluation type 값
 * @param isVideoLecture is video lecture 여부
 */
public record OfferingInfo(
    String courseCode,
    int year,
    int semester,
    String classSection,
    String professorName,
    String scheduleSummary,
    Integer points,
    String hostDepartment,
    String facultyDivisionName,
    Integer subjectEstablishmentSemester,
    Integer areaCode,
    Integer originalAreaCode,
    String evaluationType,
    Boolean isVideoLecture) {}
