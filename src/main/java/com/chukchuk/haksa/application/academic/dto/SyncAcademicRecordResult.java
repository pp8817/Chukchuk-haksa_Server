package com.chukchuk.haksa.application.academic.dto;

public class SyncAcademicRecordResult {
    private final boolean isSuccess;
    private final String error;

    public SyncAcademicRecordResult(boolean isSuccess, String error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }

    public static SyncAcademicRecordResult success() {
        return new SyncAcademicRecordResult(true, null);
    }

    public static SyncAcademicRecordResult failure(String error) {
        return new SyncAcademicRecordResult(false, error);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getError() {
        return error;
    }
}