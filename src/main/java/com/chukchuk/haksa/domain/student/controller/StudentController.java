package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.dto.StudentTargetGpaDto;
import com.chukchuk.haksa.domain.student.service.StudentTargetGpaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentTargetGpaService studentTargetGpaService;

    @PostMapping("target-gpa")
    public ResponseEntity<?> setTargetGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StudentTargetGpaDto.TargetGpa request
    ){
        String email = userDetails.getUsername();
        BigDecimal targetGpa = request.targetGpa();
        studentTargetGpaService.setStudentTargetGpa(email, targetGpa);
        return ResponseEntity.ok().body("목표 학점 저장 완료");
    }
}
