package com.chukchuk.haksa.domain.academic.record.controller;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.service.AcademicRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@RestController
@RequestMapping("/api/academic")
@RequiredArgsConstructor
@Tag(name = "Academic Record", description = "학업 성적 관련 API")
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;

    /* 학기별 성적 및 수강 과목 정보 조회 API */
    @GetMapping("/record") // Restful 방식으로 변경 제안: /api/academic-record
    @Operation(summary = "학기별 성적 및 수강 과목 정보 조회", description = "지정한 학기(year, semester)에 해당하는 성적 및 수강 과목 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AcademicRecordResponse> getAcademicRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @Parameter(description = "연도", example = "2024") Integer year,
            @RequestParam @Parameter(description = "학기", example = "10, 15, 20 ...") Integer semester) {

        UUID userId = UUID.fromString(userDetails.getUsername());

        AcademicRecordResponse response = academicRecordService.getAcademicRecord(userId, year, semester);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "사용자 학업 요약 정보 조회", description = "사용자의 학업 요약 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<StudentAcademicRecordDto.AcademicSummaryDto> getAcademicSummary(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        StudentAcademicRecordDto.AcademicSummaryDto academicSummary = academicRecordService.getAcademicSummary(userId);

        return ResponseEntity.ok(academicSummary);
    }

}
