package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Student", description = "학생 설정 관련 API")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/target-gpa")
    @Operation(summary = "목표 GPA 설정", description = "로그인된 사용자의 목표 GPA를 저장합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> setTargetGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            @Parameter(description = "목표 GPA", example = "3.8") Double targetGpa
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        studentService.setStudentTargetGpa(userId, targetGpa);
        return ResponseEntity.ok("목표 학점 저장 완료");
    }
}