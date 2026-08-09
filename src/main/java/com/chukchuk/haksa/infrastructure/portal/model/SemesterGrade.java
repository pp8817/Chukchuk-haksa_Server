package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 학기 성적 데이터를 표현한다.
 *
 * @param year 연도
 * @param semester 학기 값
 * @param earnedCredits 취득 학점
 * @param appliedCredits applied 학점
 * @param semesterGpa 학기 평점
 * @param score 점수
 */
public record SemesterGrade(
    int year,
    int semester,
    String earnedCredits,
    String appliedCredits,
    String semesterGpa,
    double score,
    Ranking ranking // null 가능
    ) {}
