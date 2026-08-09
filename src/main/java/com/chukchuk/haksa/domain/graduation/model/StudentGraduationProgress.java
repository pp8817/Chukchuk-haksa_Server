package com.chukchuk.haksa.domain.graduation.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 학생 졸업 progress 도메인 상태를 표현한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "student_graduation_progress")
public class StudentGraduationProgress extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "elective_courses_fulfilled")
  private Boolean electiveCoursesFulfilled;

  @Column(name = "credits_fulfilled")
  private Boolean creditsFulfilled;

  @Column(name = "language_cert_fulfilled")
  private Boolean languageCertFulfilled;

  @Column(name = "gpa_fulfilled")
  private Boolean gpaFulfilled;

  @Column(name = "checked_at")
  private Instant checkedAt;

  @Column(name = "area_requirements_fulfilled")
  private Boolean areaRequirementsFulfilled;

  @Enumerated(EnumType.STRING)
  @Column(name = "graduation_status")
  private GraduationStatus graduationStatus;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id")
  private Student student;

  private StudentGraduationProgress(Student student, Boolean languageCertFulfilled) {
    this.student = student;
    this.languageCertFulfilled = languageCertFulfilled;
  }

  /**
   * 척척학사의 create for language cert 대상을 생성한다.
   *
   * @param student 학생 값
   * @param languageCertFulfilled 어학 인증 충족 여부
   * @return 생성된
   */
  public static StudentGraduationProgress createForLanguageCert(
      Student student, boolean languageCertFulfilled) {
    return new StudentGraduationProgress(student, languageCertFulfilled);
  }

  /**
   * 척척학사의 update language cert 대상을 갱신한다.
   *
   * @param languageCertFulfilled 어학 인증 충족 여부
   */
  public void updateLanguageCert(boolean languageCertFulfilled) {
    this.languageCertFulfilled = languageCertFulfilled;
  }
}
