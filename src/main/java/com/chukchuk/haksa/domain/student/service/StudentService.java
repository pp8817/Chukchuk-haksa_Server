package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.student.dto.StudentDto;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
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

    public StudentDto.StudentInfoDto getStudentInfo(UUID userId) {

        // 학생 조회
        Student student = getStudentById(userId);

        return StudentDto.StudentInfoDto.from(student);
    }

    public Student getStudentById(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new DataNotFoundException("해당 학생이 존재하지 않습니다."));
    }

    @Transactional
    public void setStudentTargetGpa(String email, Double targetGpa) {
        UUID studentId = userService.getUserId(email);
        Student student = getStudentById(studentId);

        //학점 입력 하는데 0 ~ 4.5 이외를 입력하는 경우
        if (targetGpa != null &&
                (targetGpa < 0 || targetGpa>4.5)) {
            throw new IllegalArgumentException("유효하지 않은 목표 학점입니다");
        }

        student.updateTargetGpa(targetGpa);
        studentRepository.save(student);
    }
}
