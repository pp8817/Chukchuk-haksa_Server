package com.chukchuk.haksa.infrastructure.portal.model.embeddable;

public record AcademicInfo(
        int gradeLevel,
        int completedSemesters,
        int totalCredits,
        double gpa
) {}
