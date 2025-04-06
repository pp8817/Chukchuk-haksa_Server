package com.chukchuk.haksa.infrastructure.portal.model;

public record AcademicSummary(
        int appliedCredits,
        int totalCredits,
        double gpa,
        double score
) {}
