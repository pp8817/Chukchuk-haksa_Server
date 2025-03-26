package com.chukchuk.haksa.application.dashboard.dto;

import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.student.dto.StudentDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(description = "대시보드 응답")
public record DashboardResponse(
        @Schema(description = "학생 프로필 정보") Profile profile,
        @Schema(description = "학업 요약 정보") Summary summary,
        @Schema(description = "사용자 동기화 정보") User user
) {
    public static DashboardResponse from(StudentDto.StudentInfoDto studentInfoDto, StudentAcademicRecordDto.AcademicSummaryDto academicSummaryDto, String lastSyncedAt, int currentSemester) {
        return new DashboardResponse(
                Profile.from(studentInfoDto, currentSemester),
                Summary.from(academicSummaryDto),
                new User(lastSyncedAt)
        );
    }

    @Schema(description = "학생 프로필 정보")
    public record Profile(
            @Schema(description = "이름") String name,
            @Schema(description = "학번") String studentCode,
            @Schema(description = "학과 이름") String departmentName,
            @Schema(description = "전공 이름") String majorName,
            @Schema(description = "학년") int gradeLevel,
            @Schema(description = "현재 학기") int currentSemester,
            @Schema(description = "재학 상태") String status,
            @Schema(description = "마지막 업데이트 일시") String lastUpdatedAt
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

    @Schema(description = "학업 요약 정보")
    public record Summary(
            @Schema(description = "이수 학점") Integer earnedCredits,
            @Schema(description = "졸업 필수 학점") Integer requiredCredits,
            @Schema(description = "누적 GPA") BigDecimal cumulativeGpa,
            @Schema(description = "백분위") BigDecimal percentile
    ) {
        public static Summary from(StudentAcademicRecordDto.AcademicSummaryDto academicSummaryDto) {
            return new Summary(
                    academicSummaryDto.totalEarnedCredits() != null ? academicSummaryDto.totalEarnedCredits() : 0,
                    130, // 기본 값
                    academicSummaryDto.cumulativeGpa() != null ? academicSummaryDto.cumulativeGpa() : BigDecimal.ZERO,
                    academicSummaryDto.percentile() != null ? academicSummaryDto.percentile() : BigDecimal.ZERO
            );
        }
    }

    @Schema(description = "사용자 동기화 정보")
    public record User(
            @Schema(description = "학사 정보 마지막 연동 일시") String lastSyncedAt
    ) {}
}