package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

/**
 * 계층 간 전달할 성적 summary 데이터를 표현한다.
 *
 * @param semesters semesters 값
 * @param summary summary 값
 */
public record GradeSummary(List<SemesterGrade> semesters, AcademicSummary summary) {}
