package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.TargetGpaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

//targetGpa는 setter로 DB값 수정해야하는데 @Transactional(readOnly = true)없는 service가 없어서 새로 만들었습니다

@Service
@RequiredArgsConstructor
public class StudentTargetGpaService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final UserService userService;

    public void setStudentTargetGpa(String email, BigDecimal targetGpa) {
        UUID studentId = userService.getUserId(email);
        Student student = studentService.getStudentById(studentId);

        //학점 입력 하는데 0 ~ 4.5 이외를 입력하는 경우, 문자 입력하는 것도 고려해야하나?
        if (targetGpa != null &&
                (targetGpa.compareTo(BigDecimal.ZERO) < 0 || targetGpa.compareTo(new BigDecimal("4.5")) > 0)) {
            throw new TargetGpaException("유효하지 않은 목표 학점입니다");
        }

        student.setTargetGpa(targetGpa);
        studentRepository.save(student);
    }
}
