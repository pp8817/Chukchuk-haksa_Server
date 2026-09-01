// 학생 수강 기록의 일괄 fetch 조회 계약을 검증한다.

package com.chukchuk.haksa.domain.academic.record.repository;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class StudentCourseRepositoryFetchTest {

  @Test
  void exposesStudentScopedCourseAndOfferingFetchQuery() {
    assertThatCode(
            () ->
                StudentCourseRepository.class.getMethod(
                    "findAllWithCourseByStudentId", UUID.class))
        .doesNotThrowAnyException();
  }
}
