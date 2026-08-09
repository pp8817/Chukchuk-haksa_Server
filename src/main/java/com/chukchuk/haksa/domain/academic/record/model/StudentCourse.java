package com.chukchuk.haksa.domain.academic.record.model;

import com.chukchuk.haksa.application.academic.enrollment.CourseEnrollment;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.student.model.Grade;
import com.chukchuk.haksa.domain.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** 학생 과목 도메인 상태를 표현한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "student_courses")
public class StudentCourse {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  @Column(name = "grade")
  private Grade grade;

  @Column(name = "points")
  private Integer points;

  @Column(name = "is_retake")
  private Boolean isRetake;

  @Column(name = "original_score")
  private Integer originalScore;

  @CreatedDate
  @Column(name = "created_at")
  private Instant createdAt;

  @Column(nullable = false)
  private boolean isRetakeDeleted = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "offering_id", nullable = false)
  private CourseOffering offering;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  /**
   * 학생 과목 인스턴스를 생성한다.
   *
   * @param student 학생 값
   * @param offering offering 값
   * @param grade 성적 값
   * @param points 학점
   * @param isRetake is retake 여부
   * @param originalScore 원점수
   * @param isRetakeDeleted is retake deleted 여부
   */
  public StudentCourse(
      Student student,
      CourseOffering offering,
      Grade grade,
      Integer points,
      Boolean isRetake,
      Integer originalScore,
      boolean isRetakeDeleted) {
    this.student = student;
    this.offering = offering;
    this.grade = grade;
    this.points = points;
    this.isRetake = isRetake;
    this.originalScore = originalScore;
    this.isRetakeDeleted = isRetakeDeleted;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public void setRetakeDeleted(Boolean retakeDeleted) {
    this.isRetakeDeleted = retakeDeleted;
  }

  /**
   * 현재 상태가 조건을 충족하는지 반환한다.
   *
   * @param pe pe 값
   * @return 조건 충족 여부
   */
  public boolean isDifferentFrom(CourseEnrollment pe) {
    return !Objects.equals(this.grade, pe.getGrade())
        || !Objects.equals(
            this.originalScore,
            pe.getOriginalScore() != null ? pe.getOriginalScore().intValue() : null)
        || !Objects.equals(this.isRetake, pe.isRetake())
        || !Objects.equals(this.points, pe.getPoints())
        || this.isRetakeDeleted != pe.isRetakeDeleted();
  }

  /**
   * 척척학사의 update from 포털 대상을 갱신한다.
   *
   * @param pe pe 값
   */
  public void updateFromPortal(CourseEnrollment pe) {
    this.grade = pe.getGrade();
    this.originalScore = pe.getOriginalScore() != null ? pe.getOriginalScore().intValue() : null;
    this.points = pe.getPoints();
    this.isRetake = pe.isRetake();
    this.isRetakeDeleted = pe.isRetakeDeleted();
  }
}
