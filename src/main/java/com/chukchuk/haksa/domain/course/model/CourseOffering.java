package com.chukchuk.haksa.domain.course.model;

import com.chukchuk.haksa.domain.BaseEntity;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.professor.model.Professor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "course_offerings")
public class CourseOffering extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Integer points;

    @Column(name = "deleted_at")
    private Instant deletedAt; // Soft delete 적용

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type_code")
    private EvaluationType evaluationTypeCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "faculty_division_name")
    private  FacultyDivision facultyDivisionName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    public CourseOffering(
            Integer subjectEstablishmentSemester,
            Boolean isVideoLecture,
            Integer year,
            Integer semester,
            String hostDepartment,
            String classSection,
            String scheduleSummary,
            Integer originalAreaCode,
            Integer areaCode,
            Integer points,
            EvaluationType evaluationTypeCode,
            FacultyDivision facultyDivisionName,
            Course course,
            Professor professor,
            Department department
    ) {
        this.subjectEstablishmentSemester = subjectEstablishmentSemester;
        this.isVideoLecture = isVideoLecture;
        this.year = year;
        this.semester = semester;
        this.hostDepartment = hostDepartment;
        this.classSection = classSection;
        this.scheduleSummary = scheduleSummary;
        this.originalAreaCode = originalAreaCode;
        this.areaCode = areaCode;
        this.points = points;
        this.evaluationTypeCode = evaluationTypeCode;
        this.facultyDivisionName = facultyDivisionName;
        this.course = course;
        this.professor = professor;
        this.department = department;
    }
}
