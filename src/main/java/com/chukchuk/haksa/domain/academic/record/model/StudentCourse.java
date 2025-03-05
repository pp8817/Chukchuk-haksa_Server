package com.chukchuk.haksa.domain.academic.record.model;

import com.chukchuk.haksa.domain.student.model.Grade;
import com.chukchuk.haksa.domain.course.model.CourseOffering;
import com.chukchuk.haksa.domain.student.model.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "student_courses")
public class StudentCourse {

    @Id
    private Long id;

    @Column(name = "grade")
    private Grade grade;

    @Column(name = "points")
    private Integer points;

    @Column(name = "is_retake")
    private Boolean isRetake;

    @Column(name = "original_score")
    private Integer originalScore;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private CourseOffering offering;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
