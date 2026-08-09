package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 학사 summary 데이터를 표현한다.
 *
 * @param appliedCredits applied 학점
 * @param totalCredits total 학점
 * @param gpa gpa 값
 * @param score 점수
 */
public record AcademicSummary(int appliedCredits, int totalCredits, double gpa, double score) {}
