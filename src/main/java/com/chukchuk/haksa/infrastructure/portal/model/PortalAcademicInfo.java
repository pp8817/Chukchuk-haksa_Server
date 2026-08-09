package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 포털 학사 info 데이터를 표현한다.
 *
 * @param gradeLevel 학년
 * @param completedSemesters 이수 학기 수
 * @param totalCredits total 학점
 * @param gpa gpa 값
 */
public record PortalAcademicInfo(
    int gradeLevel, int completedSemesters, int totalCredits, double gpa) {}
