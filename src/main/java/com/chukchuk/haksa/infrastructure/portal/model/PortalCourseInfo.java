package com.chukchuk.haksa.infrastructure.portal.model;

import lombok.Data;

/** 척척학사의 포털 과목 info 도메인 상태를 표현한다. */
@Data
public class PortalCourseInfo {
  private String code; // courseCode
  private String name;
  private int credits;
  private String professor;
  private String schedule;
  private int establishmentSemester;
  private boolean isRetake;
  private Double originalScore;
  private String grade;
  private boolean isRetakeDeleted;
  // 생성자, getter 생략 가능
}
