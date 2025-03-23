package com.chukchuk.haksa.domain.academic.record.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.student.model.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "semester_academic_records")
public class SemesterAcademicRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "year")
    private Integer year;

    @Column(name = "total_students")
    private Integer totalStudents;

    @Column(name = "class_rank")
    private Integer classRank;

    @Column(name = "attempted_credits_gpa")
    private BigDecimal attemptedCreditsGpa;

    @Column(name = "semester_percentile")
    private BigDecimal semesterPercentile;

    @Column(name = "semester_gpa")
    private BigDecimal semesterGpa;

    @Column(name = "attempted_credits")
    private Integer attemptedCredits;

    @Column(name = "earned_credits")
    private Integer earnedCredits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
