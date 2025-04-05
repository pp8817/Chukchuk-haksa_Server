package com.chukchuk.haksa.global.portal.model;

public record PortalAcademicInfo(
        int gradeLevel,
        int completedSemesters,
        int totalCredits,
        double gpa
) {}