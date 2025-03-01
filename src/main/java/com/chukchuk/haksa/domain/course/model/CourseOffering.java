package com.chukchuk.haksa.domain.course.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.professor.model.Professor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course_offerings")
public class CourseOffering extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "subject_establishment_semester")
    private Integer subjectEstablishmentSemester;

    @Column(name = "is_video_lecture")
    private Boolean isVideoLecture;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "semester", nullable = false)
    private Integer semester;

    @Column(name = "host_department")
    private String hostDepartment;

    @Column(name = "class_section")
    private String classSection;

    @Column(name = "schedule_summary")
    private String scheduleSummary;

    @Column(name = "original_area_code")
    private Integer originalAreaCode;

    @Column(name = "area_code")
    private Integer areaCode;

    @Column(name = "points")
    private BigDecimal points;

    @Column(name = "deleted_at")
    private Instant deletedAt; // Soft delete 적용

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type_code")
    private EvaluationType evaluationTypeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "faculty_division_name")
    private  FacultyDivision facultyDivisionName;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;
}
