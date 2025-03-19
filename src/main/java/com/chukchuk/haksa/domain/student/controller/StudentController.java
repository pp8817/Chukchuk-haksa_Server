package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/target-gpa")
    public ResponseEntity<?> setTargetGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Double targetGpa
    ){
        String email = userDetails.getUsername();
        studentService.setStudentTargetGpa(email, targetGpa);
        return ResponseEntity.ok("목표 학점 저장 완료");
    }
}
