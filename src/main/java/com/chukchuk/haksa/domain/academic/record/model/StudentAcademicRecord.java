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
@Table(name = "student_academic_records")
public class StudentAcademicRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "attempted_credits_gpa")
    private BigDecimal attemptedCreditsGpa;

    @Column(name = "percentile")
    private BigDecimal percentile;

    @Column(name = "cumulative_gpa")
    private BigDecimal cumulativeGpa;

    @Column(name = "total_attempted_credits")
    private BigDecimal totalAttemptedCredits;

    @Column(name = "total_earned_credits")
    private BigDecimal totalEarnedCredits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", unique = true)
    private Student student;
}