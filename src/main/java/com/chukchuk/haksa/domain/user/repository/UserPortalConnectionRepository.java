package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.StudentInitializationDataType;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPortalConnectionRepository {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void initializePortalConnection(UUID userId, StudentInitializationDataType studentData) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // 포털 연동 상태 업데이트
        user.setPortalConnected(true);

        // 학과 및 전공 정보는 StudentInitializationDataType에서 이미 전달됨
        Department department = studentData.getDepartment(); // 학과 객체
        Department major = studentData.getMajor();          // 전공 객체
        Department secondaryMajor = studentData.getSecondaryMajor(); // 복수 전공 객체

        // 학생 정보 설정
        Student student = Student.builder()
                .studentCode(studentData.getStudentCode())
                .name(studentData.getName())
                .department(department)
                .major(major)
                .secondaryMajor(secondaryMajor)
                .admissionYear(studentData.getAdmissionYear())
                .semesterEnrolled(studentData.getSemesterEnrolled())
                .isTransferStudent(studentData.isTransferStudent())
                .isGraduated(studentData.isGraduated())
                .status(studentData.getStatus())
                .gradeLevel(studentData.getGradeLevel())
                .completedSemesters(studentData.getCompletedSemesters())
                .admissionType(studentData.getAdmissionType())
                .user(user) // User와 연관 관계 설정
                .build();

        // User와 Student 연관 관계 설정
        user.setStudent(student);

        // DB에 저장
        userRepository.save(user);
        studentRepository.save(student);
    }
}