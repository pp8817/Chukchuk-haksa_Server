package com.chukchuk.haksa.domain.academic.record.controller;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto.SemesterGradeResponse;
import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import com.chukchuk.haksa.domain.academic.record.wrapper.SemesterGradesApiResponse;
import com.chukchuk.haksa.domain.academic.record.wrapper.StudentSemesterListApiResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static com.chukchuk.haksa.domain.student.dto.StudentSemesterDto.StudentSemesterInfoResponse;

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
@Tag(name = "Semester Record", description = "학기 별 성적 및 학기 정보 관련 API")
public class SemesterController {

    private final SemesterAcademicRecordService semesterAcademicRecordService;

    @GetMapping
    @Operation(
            summary = "사용자 학기 목록 조회",
            description = "사용자의 모든 학기 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "사용자의 모든 학기 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StudentSemesterListApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "학기 데이터 없음 (ErrorCode: A03, FRESHMAN_NO_SEMESTER)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류 (ErrorCode: INTERNAL_ERROR)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class)))
            })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<StudentSemesterInfoResponse>>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());

        List<StudentSemesterInfoResponse> response = semesterAcademicRecordService.getSemestersByStudentEmail(userId);

        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @GetMapping("/grades")
    @Operation(
            summary = "사용자 학기 별 성적 조회",
            description = "사용자의 학기 별 성적 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "사용자의 학기 별 성적 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SemesterGradesApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "학기 성적 데이터 없음 (ErrorCode: A02, SEMESTER_RECORD_EMPTY)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류 (ErrorCode: INTERNAL_ERROR)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class)))
            })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<SemesterGradeResponse>>> getSemesterGrades(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());

        List<SemesterGradeResponse> response = semesterAcademicRecordService.getAllSemesterGrades(userId);

        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}
