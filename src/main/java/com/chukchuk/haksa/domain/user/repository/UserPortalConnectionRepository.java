package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.StudentInitializationDataType;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class UserPortalConnectionRepository {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void initializePortalConnection(User user, StudentInitializationDataType studentData) {
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

    @Transactional
    public void refreshPortalConnection(User user, StudentInitializationDataType studentData) {

        // 학생 정보 조회
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND));

        // 학과 및 전공 정보
        Department department = studentData.getDepartment();
        Department major = studentData.getMajor();
        Department secondaryMajor = studentData.getSecondaryMajor();

        // 기존 학생 정보 갱신
        student.updateInfo(
                studentData.getName(),
                department,
                major,
                secondaryMajor,
                studentData.getAdmissionYear(),
                studentData.getSemesterEnrolled(),
                studentData.isTransferStudent(),
                studentData.isGraduated(),
                studentData.getStatus(),
                studentData.getGradeLevel(),
                studentData.getCompletedSemesters(),
                studentData.getAdmissionType()
        );

        // 마지막 동기화 시간 갱신
        user.updateLastSyncedAt(Instant.now());

        // 저장
        studentRepository.save(student);
        userRepository.save(user);

    }
}