package com.chukchuk.haksa.infrastructure.portal.model;

public record InitializePortalConnectionResult(
        boolean isSuccess,
        String studentId,
        StudentInfo studentInfo,
        String error
) {
    public static InitializePortalConnectionResult success(String studentId, StudentInfo studentInfo) {
        return new InitializePortalConnectionResult(true, studentId, studentInfo, null);
    }

    public static InitializePortalConnectionResult failure(String error) {
        return new InitializePortalConnectionResult(false, null, null, error);
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