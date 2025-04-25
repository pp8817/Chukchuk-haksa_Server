package com.chukchuk.haksa.domain.academic.record.controller;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.service.AcademicRecordService;
import com.chukchuk.haksa.domain.academic.record.wrapper.AcademicRecordApiResponse;
import com.chukchuk.haksa.domain.academic.record.wrapper.AcademicSummaryApiResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto.AcademicSummaryResponse;

@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
@Tag(name = "Academic Record", description = "학업 성적 관련 API")
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;

    /* 학기별 성적 및 수강 과목 정보 조회 API */
    @GetMapping("/record") // Restful 방식으로 변경 제안: /api/academic-record
    @Operation(
            summary = "학기별 성적 및 수강 과목 정보 조회",
            description = "지정한 학기(year, semester)에 해당하는 성적 및 수강 과목 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "지정 학기 성적 및 수강 과목 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AcademicRecordApiResponse.class)))
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AcademicRecordResponse>> getAcademicRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @Parameter(description = "연도", example = "2024") Integer year,
            @RequestParam @Parameter(description = "학기", example = "10, 15, 20 ...") Integer semester) {

        UUID userId = UUID.fromString(userDetails.getUsername());

        AcademicRecordResponse response = academicRecordService.getAcademicRecord(userId, year, semester);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    @Operation(
            summary = "사용자 학업 요약 정보 조회",
            description = "사용자의 학업 요약 정보를 조회합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "사용자 학업 요약 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AcademicSummaryApiResponse.class)))
            })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AcademicSummaryResponse>> getAcademicSummary(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        AcademicSummaryResponse response = academicRecordService.getAcademicSummary(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
