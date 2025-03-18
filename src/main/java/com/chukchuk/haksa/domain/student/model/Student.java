package com.chukchuk.haksa.domain.student.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.student.model.embeddable.AcademicInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "student_id")
    private UUID id;

    @Column(name = "student_code", nullable = false, unique = true)
    private String studentCode;

    @Column(name = "name")
    private String name;

    @Column(name = "is_graduated")
    private Boolean isGraduated;

    @Column(name = "admission_type")
    private String admissionType;

    @Column(name = "target_gpa")
    private Double targetGpa;

    private void setTargetGpa(Double targetGpa) {
        this.targetGpa = targetGpa;
    }
    public void updateTargetGpa(Double targetGpa) {
        setTargetGpa(targetGpa);
    }

    @Embedded
    private AcademicInfo academicInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Department major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_major_id")
    private Department secondaryMajor;
}
