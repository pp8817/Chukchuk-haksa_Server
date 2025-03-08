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
}
