package com.chukchuk.haksa.global.portal.model.embeddable;

public record AcademicInfo(
        int gradeLevel,
        int completedSemesters,
        int totalCredits,
        double gpa
) {}
