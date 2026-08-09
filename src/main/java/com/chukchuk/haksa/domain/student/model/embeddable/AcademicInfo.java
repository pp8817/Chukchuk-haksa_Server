package com.chukchuk.haksa.domain.student.model.embeddable;

import com.chukchuk.haksa.domain.student.model.StudentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 척척학사의 학사 info 도메인 상태를 표현한다. */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AcademicInfo {

  @Column(name = "admission_year", nullable = false)
  private Integer admissionYear;

  @Column(name = "semester_enrolled")
  private Integer semesterEnrolled;

  @Column(name = "is_transfer_student")
  private Boolean isTransferStudent;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private StudentStatus status;

  @Column(name = "grade_level")
  private Integer gradeLevel;

  @Column(name = "completed_semesters")
  private Integer completedSemesters;

  // Builder 패턴 추가
  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param admissionYear admission 연도
   * @param semesterEnrolled 학기 enrolled 값
   * @param isTransferStudent is transfer 학생 여부
   * @param status 상태
   * @param gradeLevel 학년
   * @param completedSemesters 이수 학기 수
   */
  @Builder
  public AcademicInfo(
      Integer admissionYear,
      Integer semesterEnrolled,
      Boolean isTransferStudent,
      StudentStatus status,
      Integer gradeLevel,
      Integer completedSemesters) {
    this.admissionYear = admissionYear;
    this.semesterEnrolled = semesterEnrolled;
    this.isTransferStudent = isTransferStudent;
    this.status = status;
    this.gradeLevel = gradeLevel;
    this.completedSemesters = completedSemesters;
  }
}
