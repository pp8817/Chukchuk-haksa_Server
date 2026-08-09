package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

/**
 * 계층 간 전달할 학기 과목 info 데이터를 표현한다.
 *
 * @param year 연도
 * @param semester 학기 값
 * @param courses courses 값
 */
public record SemesterCourseInfo(int year, int semester, List<CourseInfo> courses) {}
