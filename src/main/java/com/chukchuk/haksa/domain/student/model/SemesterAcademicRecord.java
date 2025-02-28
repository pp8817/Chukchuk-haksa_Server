package com.chukchuk.haksa.domain.student.model;

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
public class SemesterAcademicRecord {
    @Id
    private UUID id;

    @Column(name = "semester")
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
    private BigDecimal attemptedCredits;

    @Column(name = "earned_credits")
    private BigDecimal earnedCredits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
