package com.chukchuk.haksa.infrastructure.portal.model;

public record PortalAcademicInfo(
        int gradeLevel,
        int completedSemesters,
        int totalCredits,
        double gpa
) {}