package com.chukchuk.haksa.domain.student.controller;

import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.domain.student.wrapper.StudentProfileApiResponse;
import com.chukchuk.haksa.domain.student.wrapper.TargetGpaApiResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.chukchuk.haksa.domain.student.dto.StudentDto.StudentProfileResponse;


@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Tag(name = "Student", description = "학생 설정 관련 API")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/target-gpa")
    @Operation(
            summary = "목표 GPA 설정",
            description = "로그인된 사용자의 목표 GPA를 저장합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "목표 GPA 설정 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TargetGpaApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "잘못된 GPA 입력 (ErrorCode: S02, INVALID_TARGET_GPA)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "학생 정보 없음 (ErrorCode: S01, STUDENT_NOT_FOUND)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<MessageOnlyResponse>> setTargetGpa(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false)
            @Parameter(description = "목표 GPA", example = "3.8") Double targetGpa
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        studentService.setStudentTargetGpa(userId, targetGpa);
        return ResponseEntity.ok(ApiResponse.success(new MessageOnlyResponse("목표 학점 저장 완료")));
    }

    @GetMapping("/profile")
    @Operation(
            summary = "사용자 프로필 조회",
            description = "로그인된 사용자의 프로필 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "사용자 프로필 정보 조회 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "학생 정보 없음 (ErrorCode: S01, STUDENT_NOT_FOUND)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        StudentProfileResponse response = studentService.getStudentProfile(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}