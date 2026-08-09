package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

/**
 * 계층 간 전달할 포털 학사 data 데이터를 표현한다.
 *
 * @param semesters semesters 값
 * @param grades grades 값
 * @param summary summary 값
 */
public record PortalAcademicData(
    List<SemesterCourseInfo> semesters, GradeSummary grades, AcademicSummary summary) {}
