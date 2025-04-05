package com.chukchuk.haksa.global.portal.model;

public record SemesterGrade(
        int year,
        int semester,
        String earnedCredits,
        String appliedCredits,
        String semesterGpa,
        double score,
        Ranking ranking // null 가능
) {}
