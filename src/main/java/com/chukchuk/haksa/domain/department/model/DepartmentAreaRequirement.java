package com.chukchuk.haksa.domain.department.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "department_area_requirements")
public class DepartmentAreaRequirement {
    @Id
    private UUID id;

    @Column(name = "total_elective_courses")
    private Integer totalElectiveCourses;

    @Column(name = "required_elective_courses")
    private Integer requiredElectiveCourses;

    @Column(name = "admission_year")
    private Integer admissionYear;

    @Column(name = "required_credits")
    private Integer requiredCredits;

    @Column(name = "description")
    private String description;

    @Column(name = "area_type")
    private String areaType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
