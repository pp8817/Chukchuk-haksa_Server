package com.chukchuk.haksa.domain.student.dto;

import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.model.StudentStatus;

import java.time.Instant;

public class StudentDto {

    public record StudentInfoDto(
            String studentCode,
            String name,
            String departmentName,
            String majorName,
            Integer gradeLevel,
            StudentStatus status,
            Integer completedSemesters,
            Instant updatedAt
    ) {
        public static StudentInfoDto from(Student student) {
            return new StudentInfoDto(
                    student.getStudentCode(),
                    student.getName(),
                    student.getDepartment() != null ? student.getDepartment().getEstablishedDepartmentName() : null,
                    student.getMajor() != null ? student.getMajor().getEstablishedDepartmentName() : null,
                    student.getAcademicInfo().getGradeLevel(),
                    student.getAcademicInfo().getStatus(),
                    student.getAcademicInfo().getCompletedSemesters(),
                    student.getUpdatedAt()
            );
        }
    }
}
