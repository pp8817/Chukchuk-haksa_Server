package com.chukchuk.haksa.domain.academic.record.repository;

import com.chukchuk.haksa.application.academic.enrollment.CourseEnrollment;
import com.chukchuk.haksa.domain.student.model.GradeType;
import java.util.UUID;

/**
 * 학생 과목 bulk row 데이터를 전달한다.
 *
 * @param studentId 학생 식별자
 * @param offeringId 과목 개설 식별자
 * @param gradeType 응답에 포함할 성적 type
 * @param points 학점
 * @param isRetake is retake 여부
 * @param originalScore 원점수
 * @param isRetakeDeleted is retake deleted 여부
 */
public record StudentCourseBulkRow(
    UUID studentId,
    Long offeringId,
    GradeType gradeType,
    Integer points,
    boolean isRetake,
    Integer originalScore,
    boolean isRetakeDeleted) {

  /**
   * 수강 정보를 JDBC 배치 저장 행으로 변환한다.
   *
   * @param enrollment 응답에 포함할 수강
   * @return 학생 과목 bulk row 결과
   */
  public static StudentCourseBulkRow from(CourseEnrollment enrollment) {
    Integer normalizedScore =
        enrollment.getOriginalScore() != null ? enrollment.getOriginalScore().intValue() : null;
    return new StudentCourseBulkRow(
        enrollment.getStudentId(),
        enrollment.getOfferingId(),
        enrollment.getGradeType(),
        enrollment.getPoints(),
        enrollment.isRetake(),
        normalizedScore,
        enrollment.isRetakeDeleted());
  }
}
