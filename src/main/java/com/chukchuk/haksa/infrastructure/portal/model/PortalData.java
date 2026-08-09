package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 포털 data 데이터를 표현한다.
 *
 * @param student 학생 값
 * @param academic 학사 값
 * @param curriculum curriculum 값
 */
public record PortalData(
    PortalStudentInfo student, PortalAcademicData academic, PortalCurriculumData curriculum) {}
