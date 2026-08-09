package com.chukchuk.haksa.domain.student.dto;

import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Objects;

/** 척척학사의 학생 계층 간 데이터를 전달한다. */
public class StudentDto {

  /**
   * 계층 간 전달할 학생 info dto 데이터를 표현한다.
   *
   * @param studentCode 학번
   * @param name 이름
   * @param departmentName 학과 이름
   * @param majorName 전공 이름
   * @param dualMajorName dual 전공 이름
   * @param gradeLevel 학년
   * @param status 상태
   * @param completedSemesters 이수 학기 수
   * @param updatedAt 수정 시각
   * @param reconnectionRequired reconnection required 값
   */
  public record StudentInfoDto(
      String studentCode,
      String name,
      String departmentName,
      String majorName,
      String dualMajorName,
      Integer gradeLevel,
      StudentStatus status,
      Integer completedSemesters,
      Instant updatedAt,
      boolean reconnectionRequired) {
    /**
     * 학생 엔티티를 학생 정보 응답으로 변환한다.
     *
     * @param student 학생 값
     * @return 학생 info dto 결과
     */
    public static StudentInfoDto from(Student student) {
      return new StudentInfoDto(
          student.getStudentCode(),
          student.getName(),
          student.getDepartment() != null
              ? student.getDepartment().getEstablishedDepartmentName()
              : null,
          student.getMajor() != null ? student.getMajor().getEstablishedDepartmentName() : null,
          student.getSecondaryMajor() != null
              ? student.getSecondaryMajor().getEstablishedDepartmentName()
              : null,
          student.getAcademicInfo().getGradeLevel(),
          student.getAcademicInfo().getStatus(),
          student.getAcademicInfo().getCompletedSemesters(),
          student.getUpdatedAt(),
          student.isReconnectionRequired());
    }
  }

  /**
   * 계층 간 전달할 학생 프로필 응답 데이터를 표현한다.
   *
   * @param name 이름
   * @param studentCode 학번
   * @param departmentName 학과 이름
   * @param majorName 전공 이름
   * @param dualMajorName dual 전공 이름
   * @param gradeLevel 학년
   * @param currentSemester current 학기 값
   * @param status 상태
   * @param lastUpdatedAt last 수정 시각
   * @param lastSyncedAt last synced at 값
   * @param reconnectionRequired reconnection required 값
   */
  @Schema(description = "학생 프로필 정보")
  public record StudentProfileResponse(
      @Schema(description = "이름", required = true) String name,
      @Schema(description = "학번", required = true) String studentCode,
      @Schema(description = "학과 이름", required = true) String departmentName,
      @Schema(description = "전공 이름", required = true) String majorName,
      @Schema(description = "복수전공 이름", required = false) String dualMajorName,
      @Schema(description = "학년", required = true) int gradeLevel,
      @Schema(description = "현재 학기", required = true) int currentSemester,
      @Schema(description = "재학 상태", required = true, implementation = StudentStatus.class)
          StudentStatus status,
      @Schema(description = "마지막 업데이트 일시", required = true) String lastUpdatedAt,
      @Schema(description = "학사 정보 마지막 연동 일시", required = true) String lastSyncedAt,
      @Schema(description = "재연동 필요 여부", required = true) boolean reconnectionRequired) {
    /**
     * 학생 정보와 동기화 시각으로 학생 프로필 응답을 생성한다.
     *
     * @param studentInfoDto 학생 info dto 값
     * @param currentSemester current 학기 값
     * @param lastSyncedAt last synced at 값
     * @return 학생 프로필 응답 결과
     */
    public static StudentProfileResponse from(
        StudentDto.StudentInfoDto studentInfoDto, int currentSemester, String lastSyncedAt) {
      return new StudentProfileResponse(
          Objects.requireNonNullElse(studentInfoDto.name(), ""),
          Objects.requireNonNullElse(studentInfoDto.studentCode(), ""),
          Objects.requireNonNullElse(studentInfoDto.departmentName(), ""),
          Objects.requireNonNullElse(studentInfoDto.majorName(), ""),
          Objects.requireNonNullElse(studentInfoDto.dualMajorName(), ""),
          studentInfoDto.gradeLevel() != null ? studentInfoDto.gradeLevel() : 0,
          currentSemester,
          studentInfoDto.status(),
          studentInfoDto.updatedAt() != null ? studentInfoDto.updatedAt().toString() : "",
          lastSyncedAt,
          studentInfoDto.reconnectionRequired());
    }
  }
}
