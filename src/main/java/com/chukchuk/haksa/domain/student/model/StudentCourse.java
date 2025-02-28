package com.chukchuk.haksa.domain.student.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "student_courses")
public class StudentCourse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade")
    private String grade;

    @Column(name = "points")
    private Integer points;

    @Column(name = "is_retake")
    private Boolean isRetake;

    @Column(name = "original_score")
    private Integer originalScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private CourseOffering offering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;
}
