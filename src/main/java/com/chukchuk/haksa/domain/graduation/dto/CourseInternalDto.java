package com.chukchuk.haksa.domain.graduation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 과목 internal 계층 간 데이터를 전달한다. */
@Getter
@AllArgsConstructor
public class CourseInternalDto {
  private Long offeringId;
  private String areaType;
  private Integer credits;
  private String grade;
  private String courseName;
  private Integer semester;
  private Integer year;
  private String courseCode;
  private Integer originalScore;
  private Integer liberalAreaCode;
}
