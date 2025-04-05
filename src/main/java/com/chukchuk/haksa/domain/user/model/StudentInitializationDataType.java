package com.chukchuk.haksa.domain.user.model;


import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.student.model.StudentStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StudentInitializationDataType {
    private String studentCode;
    private String name;
    private Department department;    // 학과 객체
    private Department major;         // 전공 객체 (nullable)
    private Department secondaryMajor; // 복수 전공 객체 (nullable)
    private int admissionYear;
    private int semesterEnrolled;
    private boolean isTransferStudent;
    private boolean isGraduated;
    private StudentStatus status;
    private int gradeLevel;
    private int completedSemesters;
    private String admissionType;
}
