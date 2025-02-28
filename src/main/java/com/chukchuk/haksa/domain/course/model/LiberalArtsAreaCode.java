package com.chukchuk.haksa.domain.course.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "liberal_arts_area_codes")
public class LiberalArtsAreaCode {
    @Id
    private Integer code;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "area_name")
    private String areaName;

    @Column(name = "description")
    private String description;
}