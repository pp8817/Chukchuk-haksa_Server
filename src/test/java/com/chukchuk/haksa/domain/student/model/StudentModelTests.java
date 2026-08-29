// 학생 도메인의 편입생 판별 규칙을 검증한다.

package com.chukchuk.haksa.domain.student.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentModelTests {

  @Test
  @DisplayName("저장된 편입 여부가 false이면 학번 패턴과 관계없이 일반 학생으로 판별한다")
  void usesStoredFalseTransferFlag() {
    Student student =
        Student.builder()
            .studentCode("20221234")
            .admissionYear(2022)
            .isTransferStudent(false)
            .build();

    assertThat(student.isTransferStudent()).isFalse();
  }

  @Test
  @DisplayName("저장된 편입 여부가 true이면 학번 패턴과 관계없이 편입생으로 판별한다")
  void usesStoredTrueTransferFlag() {
    Student student =
        Student.builder()
            .studentCode("22221234")
            .admissionYear(2022)
            .isTransferStudent(true)
            .build();

    assertThat(student.isTransferStudent()).isTrue();
  }
}
