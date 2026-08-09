package com.chukchuk.haksa.application.academic.enrollment;

import com.chukchuk.haksa.domain.student.model.Grade;
import com.chukchuk.haksa.domain.student.model.GradeType;
import java.util.UUID;
import lombok.Getter;

/** 학사 영역에서 과목 수강 책임을 수행한다. */
@Getter
public class CourseEnrollment {
  private UUID studentId;
  private Long offeringId;
  private Grade grade;
  private Integer points;
  private boolean isRetake;
  private Double originalScore;
  private boolean isRetakeDeleted;

  /**
   * 과목 수강 인스턴스를 생성한다.
   *
   * @param studentId 학생 식별자
   * @param offeringId 과목 개설 식별자
   * @param grade 성적 값
   * @param points 학점
   * @param isRetake is retake 여부
   * @param originalScore 원점수
   * @param isRetakeDeleted is retake deleted 여부
   */
  public CourseEnrollment(
      UUID studentId,
      Long offeringId,
      Grade grade,
      Integer points,
      boolean isRetake,
      Double originalScore,
      boolean isRetakeDeleted) {
    this.studentId = studentId;
    this.offeringId = offeringId;
    this.grade = grade;
    this.points = points;
    this.isRetake = isRetake;
    this.originalScore = originalScore;
    this.isRetakeDeleted = isRetakeDeleted;
  }

  public GradeType getGradeType() {
    return grade.getValue();
  }
}
