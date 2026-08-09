package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 과목 info 데이터를 표현한다.
 *
 * @param code code 값
 * @param name 이름
 * @param professor 교수
 * @param department 학과 값
 * @param credits 학점
 * @param grade 성적 값
 * @param isRetake is retake 여부
 * @param schedule schedule 값
 * @param areaType area type 값
 * @param areaCode area code 값
 * @param originalAreaCode original area code 값
 * @param establishmentSemester establishment 학기 값
 * @param originalScore 원점수
 * @param isRetakeDeleted is retake deleted 여부
 */
public record CourseInfo(
    String code,
    String name,
    String professor,
    String department,
    Integer credits,
    String grade,
    boolean isRetake,
    String schedule,
    String areaType,
    Integer areaCode,
    Integer originalAreaCode,
    Integer establishmentSemester,
    Double originalScore,
    boolean isRetakeDeleted) {}
