package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.chukchuk.haksa.domain.student.dto.StudentDto.Profile;


@RestController
@RequestMapping("/api/student")
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
        return ResponseEntity.ok(ApiResponse.success("목표 학점 저장 완료"));
    }

    @GetMapping("/profile")
    @Operation(summary = "사용자 프로필 조회", description = "로그인된 사용자의 프로필 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Profile>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        Profile response = studentService.getStudentProfile(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}