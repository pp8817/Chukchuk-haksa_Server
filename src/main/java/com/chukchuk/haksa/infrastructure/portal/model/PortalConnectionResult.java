package com.chukchuk.haksa.infrastructure.portal.model;

public record PortalConnectionResult(
        boolean isSuccess,
        String studentId,
        StudentInfo studentInfo,
        String error
) {
    public static PortalConnectionResult success(String studentId, StudentInfo studentInfo) {
        return new PortalConnectionResult(true, studentId, studentInfo, null);
    }

    public static PortalConnectionResult failure(String error) {
        return new PortalConnectionResult(false, null, null, error);
    }

    public record StudentInfo(
            String name,
            String school,
            String majorName,
            String studentCode,
            int gradeLevel,
            String status,
            int completedSemesterType
    ) {}
}