package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.student.dto.StudentDto;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.BusinessException;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;

    public Student getStudentById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND));
    }

    public StudentDto.Profile getStudentProfile(UUID userId) {
        StudentDto.StudentInfoDto studentInfo = getStudentInfo(userId);
        int currentSemester = getCurrentSemester(studentInfo.gradeLevel(), studentInfo.completedSemesters());

        User user = userService.getUserById(userId);
        String lastSyncedAt = user.getLastSyncedAt() != null ? user.getLastSyncedAt().toString() : "";

        return StudentDto.Profile.from(studentInfo, currentSemester, lastSyncedAt);
    }

    @Transactional
    public void setStudentTargetGpa(UUID userId, Double targetGpa) {
        Student student = getStudentById(userId);

        //학점 입력 하는데 0 ~ 4.5 이외를 입력하는 경우
        if (targetGpa != null &&
                (targetGpa < 0 || targetGpa > 4.5)) {
            throw new BusinessException(ErrorCode.INVALID_TARGET_GPA);
        }

        student.setTargetGpa(targetGpa);
        studentRepository.save(student);
    }

    private StudentDto.StudentInfoDto getStudentInfo(UUID userId) {
        // 학생 조회
        Student student = getStudentById(userId);

        return StudentDto.StudentInfoDto.from(student);
    }

    private static int getCurrentSemester(Integer gradeLevel, Integer completedSemesters) {

        int safeGradeLevel = (gradeLevel != null) ? gradeLevel : 0;
        int safeCompletedSemesters = (completedSemesters != null) ? completedSemesters : 0;

        // 1️⃣ 해당 학년 이전에 완료해야 하는 최소 학기 수
        int expectedCompleted = (safeGradeLevel - 1) * 2;

        // 2️⃣ 만약 completedSemesters가 예상보다 낮으면 최소값으로 간주 (방학 중 업데이트가 안된 경우 대비)
        int effectiveCompleted = Math.max(safeCompletedSemesters, expectedCompleted);

        // 3️⃣ 현재 학기 계산
        int currentSemester = effectiveCompleted - expectedCompleted + 1;

        // 4️⃣ currentSemester는 1 또는 2만 가능하도록 보정
        if (currentSemester < 1) {
            return 1;
        } else if (currentSemester > 2) {
            return 2;
        }
        return currentSemester;
    }
}
