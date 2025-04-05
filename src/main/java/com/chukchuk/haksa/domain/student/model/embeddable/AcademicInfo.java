package com.chukchuk.haksa.domain.student.model.embeddable;

import com.chukchuk.haksa.domain.student.model.StudentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AcademicInfo {

    @Column(name = "admission_year", nullable = false)
    private Integer admissionYear;

    @Column(name = "semester_enrolled")
    private Integer semesterEnrolled;

    @Column(name = "is_transfer_student")
    private Boolean isTransferStudent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StudentStatus status;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "completed_semesters")
    private Integer completedSemesters;

    public static AcademicInfo create(
            int admissionYear,
            int semesterEnrolled,
            boolean isTransferStudent,
            StudentStatus status,
            int gradeLevel,
            int completedSemesters
    ) {
        AcademicInfo info = new AcademicInfo();
        info.admissionYear = admissionYear;
        info.semesterEnrolled = semesterEnrolled;
        info.isTransferStudent = isTransferStudent;
        info.status = status;
        info.gradeLevel = gradeLevel;
        info.completedSemesters = completedSemesters;
        return info;
    }
}