package com.chukchuk.haksa.domain.course.model;

import com.chukchuk.haksa.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 척척학사의 liberal arts area code 도메인 상태를 표현한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liberal_arts_area_codes")
public class LiberalArtsAreaCode extends BaseEntity {

  @Id private Integer code;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "area_name", nullable = false)
  private String areaName;

  @Column(name = "description")
  private String description;
}
