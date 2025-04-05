package com.chukchuk.haksa.application.academic.enrollment;

public record CourseEnrollmentRecord(
        String studentId,
        Long offeringId,
        String grade,
        Boolean isRetake,
        Double originalScore,
        Double points
) {}
