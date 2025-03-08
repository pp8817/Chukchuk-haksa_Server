package com.chukchuk.haksa.application.dashboard.dto;

import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.student.dto.StudentDto;

import java.math.BigDecimal;
import java.util.Objects;

public record DashboardResponse(
        Profile profile,
        Summary summary,
        User user) {

    public static DashboardResponse from(StudentDto.StudentInfoDto studentInfoDto, StudentAcademicRecordDto.AcademicSummaryDto academicSummaryDto, String lastSyncedAt, int currentSemester) {
        return new DashboardResponse(
                Profile.from(studentInfoDto, currentSemester),
                Summary.from(academicSummaryDto),
                new User(lastSyncedAt)
        );
    }

    public record Profile(
            String name,
            String studentCode,
            String departmentName,
            String majorName,
            int gradeLevel,
            int currentSemester,
            String status,
            String lastUpdatedAt
    ) {
        public static Profile from(StudentDto.StudentInfoDto studentInfoDto, int currentSemester) {
            return new Profile(
                    Objects.requireNonNullElse(studentInfoDto.name(), ""),
                    Objects.requireNonNullElse(studentInfoDto.studentCode(), ""),
                    Objects.requireNonNullElse(studentInfoDto.departmentName(), ""),
                    Objects.requireNonNullElse(studentInfoDto.majorName(), ""),
                    studentInfoDto.gradeLevel() != null ? studentInfoDto.gradeLevel() : 0,
                    currentSemester,
                    Objects.requireNonNullElse(studentInfoDto.status().toString(), ""),
                    studentInfoDto.updatedAt().toString()
            );
        }
    }

    public record Summary(
            Integer earnedCredits,
            Integer requiredCredits,
            BigDecimal cumulativeGpa,
            BigDecimal percentile
    ) {
        public static Summary from(StudentAcademicRecordDto.AcademicSummaryDto academicSummaryDto) {
            return new Summary(
                    academicSummaryDto.totalEarnedCredits() != null ? academicSummaryDto.totalEarnedCredits() : 0,
                    130, // 기본 값 (동적으로 설정 가능)
                    academicSummaryDto.cumulativeGpa() != null ? academicSummaryDto.cumulativeGpa() : BigDecimal.ZERO,
                    academicSummaryDto.percentile() != null ? academicSummaryDto.percentile() : BigDecimal.ZERO);
        }

    }

    public record User(
            String lastSyncedAt
    ) {

    }
}