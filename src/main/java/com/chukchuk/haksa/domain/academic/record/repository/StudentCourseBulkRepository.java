package com.chukchuk.haksa.domain.academic.record.repository;

import java.util.List;

/** 학생 과목 bulk repository 기능의 계약을 정의한다. */
public interface StudentCourseBulkRepository {

  /**
   * 학생 수강 과목 행을 배치로 저장한다.
   *
   * @param rows rows 값
   */
  void insertAll(List<StudentCourseBulkRow> rows);
}
