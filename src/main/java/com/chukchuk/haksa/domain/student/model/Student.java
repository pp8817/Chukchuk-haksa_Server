package com.chukchuk.haksa.domain.student.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.department.model.Department;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "students")
public class Student extends BaseEntity {
    @Id
    private UUID studentId;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "name")
    private String name;

    @Column(name = "completed_semesters")
    private Integer completedSemesters;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "semester_enrolled")
    private Integer semesterEnrolled;

    @Column(name = "admission_year")
    private Integer admissionYear;

    @Column(name = "is_graduated")
    private Boolean isGraduated;

    @Column(name = "is_transfer_student")
    private Boolean isTransferStudent;

    @Column(name = "admission_type")
    private String admissionType;

    @Column(name = "target_gpa")
    private BigDecimal targetGpa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Department major;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondary_major_id")
    private Department secondaryMajor;
}
