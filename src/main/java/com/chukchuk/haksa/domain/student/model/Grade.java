package com.chukchuk.haksa.domain.student.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/** 척척학사의 성적 도메인 상태를 표현한다. */
@Embeddable
public class Grade {

  @Convert(converter = GradeTypeConverter.class)
  @Column(name = "grade")
  private GradeType value;

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  protected Grade() {} // JPA를 위한 기본 생성자

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param value value 값
   */
  public Grade(GradeType value) {
    this.value = value;
  }

  /**
   * 입력 값을 사용해 결과 객체를 생성한다.
   *
   * @return 생성된
   */
  public static Grade createInProgress() {
    return new Grade(GradeType.IP);
  }

  public GradeType getValue() {
    return value;
  }

  public boolean isCompleted() {
    return value != GradeType.IP;
  }

  public boolean isPassingGrade() {
    return value != GradeType.F && value != GradeType.NP && value != GradeType.IP;
  }

  public double getGradePoint() {
    return value.getGradePoint();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Grade g)) {
      return false;
    }
    return value == g.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
