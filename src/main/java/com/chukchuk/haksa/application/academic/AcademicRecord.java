package com.chukchuk.haksa.application.academic;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** 포털에서 조회한 학생의 전체 학사 기록을 표현한다. */
public class AcademicRecord {
  private UUID studentId;
  private List<SemesterGrade> semesters;
  private AcademicSummary summary;

  /**
   * 학사 record 인스턴스를 생성한다.
   *
   * @param studentId 학생 식별자
   * @param semesters semesters 값
   * @param summary summary 값
   */
  public AcademicRecord(UUID studentId, List<SemesterGrade> semesters, AcademicSummary summary) {
    this.studentId = studentId;
    this.semesters = semesters;
    this.summary = summary;
  }

  public UUID getStudentId() {
    return studentId;
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @return 조회
   */
  public List<SemesterGrade> getSemesters() {
    if (semesters == null) {
      return Collections.emptyList();
    }
    Collections.sort(
        semesters,
        (a, b) -> {
          if (b.getYear() != a.getYear()) {
            return Integer.compare(b.getYear(), a.getYear());
          }
          return Integer.compare(b.getSemester(), a.getSemester());
        });
    return semesters;
  }

  public AcademicSummary getSummary() {
    return summary;
  }
}
