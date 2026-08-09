package com.chukchuk.haksa.domain.academic.record.model;

import com.chukchuk.haksa.application.academic.AcademicSummary;
import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.student.model.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 학생 학사 record 도메인 상태를 표현한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "student_academic_records")
public class StudentAcademicRecord extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "attempted_credits_gpa")
  private BigDecimal attemptedCreditsGpa;

  @Column(name = "percentile")
  private BigDecimal percentile;

  @Column(name = "cumulative_gpa")
  private BigDecimal cumulativeGpa;

  @Column(name = "total_attempted_credits")
  private Integer totalAttemptedCredits;

  @Column(name = "total_earned_credits")
  private Integer totalEarnedCredits;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_id", unique = true, nullable = false)
  private Student student;

  /**
   * 학생 학사 record 인스턴스를 생성한다.
   *
   * @param student 학생 값
   * @param totalAttemptedCredits total 신청 학점
   * @param totalEarnedCredits total 취득 학점
   * @param cumulativeGpa 누적 평점
   * @param percentile percentile 값
   */
  public StudentAcademicRecord(
      Student student,
      Integer totalAttemptedCredits,
      Integer totalEarnedCredits,
      BigDecimal cumulativeGpa,
      BigDecimal percentile) {
    this.student = student;
    this.totalAttemptedCredits = totalAttemptedCredits;
    this.totalEarnedCredits = totalEarnedCredits;
    this.cumulativeGpa = cumulativeGpa;
    this.percentile = percentile;
  }

  /**
   * 척척학사의 update with 대상을 갱신한다.
   *
   * @param summary summary 값
   */
  public void updateWith(AcademicSummary summary) {
    this.totalAttemptedCredits = summary.getTotalAttemptedCredits();
    this.totalEarnedCredits = summary.getTotalEarnedCredits();
    this.cumulativeGpa = BigDecimal.valueOf(summary.getCumulativeGpa());
    this.percentile = BigDecimal.valueOf(summary.getPercentile());
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  /**
   * 현재 상태가 조건을 충족하는지 반환한다.
   *
   * @param summary summary 값
   * @return 조건 충족 여부
   */
  public boolean isSameAs(AcademicSummary summary) {
    return this.totalAttemptedCredits != null
        && this.totalAttemptedCredits.equals(summary.getTotalAttemptedCredits())
        && this.totalEarnedCredits != null
        && this.totalEarnedCredits.equals(summary.getTotalEarnedCredits())
        && this.cumulativeGpa != null
        && this.cumulativeGpa.compareTo(BigDecimal.valueOf(summary.getCumulativeGpa())) == 0
        && this.percentile != null
        && this.percentile.compareTo(BigDecimal.valueOf(summary.getPercentile())) == 0;
  }
}
