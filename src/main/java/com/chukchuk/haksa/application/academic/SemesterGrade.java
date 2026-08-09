package com.chukchuk.haksa.application.academic;

import lombok.Data;

/** 한 학기의 성적과 수강 과목 정보를 표현한다. */
@Data
public class SemesterGrade {

  private int year;
  private int semester;
  private int attemptedCredits;
  private int earnedCredits;
  private double semesterGpa;
  private double semesterPercentile;
  private Double attemptedCreditsGpa;
  private Integer classRank;
  private Integer totalStudents;

  /**
   * 학기 성적 인스턴스를 생성한다.
   *
   * @param year 연도
   * @param semester 학기 값
   * @param attemptedCredits 신청 학점
   * @param earnedCredits 취득 학점
   * @param semesterGpa 학기 평점
   * @param semesterPercentile 학기 percentile 값
   * @param attemptedCreditsGpa attempted credits gpa 값
   * @param classRank class rank 값
   * @param totalStudents total students 값
   */
  public SemesterGrade(
      int year,
      int semester,
      int attemptedCredits,
      int earnedCredits,
      double semesterGpa,
      double semesterPercentile,
      Double attemptedCreditsGpa,
      Integer classRank,
      Integer totalStudents) {
    this.year = year;
    this.semester = semester;
    this.attemptedCredits = attemptedCredits;
    this.earnedCredits = earnedCredits;
    this.semesterGpa = semesterGpa;
    this.semesterPercentile = semesterPercentile;
    this.attemptedCreditsGpa = attemptedCreditsGpa;
    this.classRank = classRank;
    this.totalStudents = totalStudents;
  }
}
