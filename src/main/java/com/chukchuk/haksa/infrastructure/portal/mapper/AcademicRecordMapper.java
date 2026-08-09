package com.chukchuk.haksa.infrastructure.portal.mapper;

import com.chukchuk.haksa.application.academic.AcademicSummary;
import com.chukchuk.haksa.application.academic.SemesterGrade;
import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.student.model.Student;
import java.math.BigDecimal;

/** 척척학사의 학사 record 도메인 데이터 간 변환을 담당한다. */
public class AcademicRecordMapper {

  /**
   * 입력 데이터를 영속 엔티티로 변환한다.
   *
   * @param student 학생 값
   * @param summary summary 값
   * @return 학생 학사 record 결과
   */
  public static StudentAcademicRecord toEntity(Student student, AcademicSummary summary) {

    if (summary == null) {
      throw new IllegalArgumentException("AcademicSummary cannot be null");
    }

    // null 체크를 통한 안전한 변환
    BigDecimal cumulativeGpa =
        summary.getCumulativeGpa() != null
            ? BigDecimal.valueOf(summary.getCumulativeGpa())
            : BigDecimal.ZERO;

    BigDecimal percentile =
        summary.getPercentile() != null
            ? BigDecimal.valueOf(summary.getPercentile())
            : BigDecimal.ZERO;

    return new StudentAcademicRecord(
        student,
        summary.getTotalAttemptedCredits(),
        summary.getTotalEarnedCredits(),
        cumulativeGpa,
        percentile);
  }

  /**
   * 입력 데이터를 영속 엔티티로 변환한다.
   *
   * @param student 학생 값
   * @param grade 성적 값
   * @return 학기 학사 record 결과
   */
  public static SemesterAcademicRecord toEntity(Student student, SemesterGrade grade) {
    if (grade == null) {
      throw new IllegalArgumentException("SemesterGrade cannot be null");
    }

    BigDecimal attemptedCreditsGpa =
        grade.getAttemptedCreditsGpa() != null
            ? BigDecimal.valueOf(grade.getAttemptedCreditsGpa())
            : BigDecimal.ZERO;

    return new SemesterAcademicRecord(
        student,
        grade.getYear(),
        grade.getSemester(),
        grade.getAttemptedCredits(),
        grade.getEarnedCredits(),
        BigDecimal.valueOf(grade.getSemesterGpa()),
        BigDecimal.valueOf(grade.getSemesterPercentile()),
        attemptedCreditsGpa,
        grade.getClassRank(),
        grade.getTotalStudents());
  }
}
