package com.chukchuk.haksa.domain.academic.record.controller;

import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/semester")
@RequiredArgsConstructor
@Tag(name = "Semester Record", description = "학기 별 성적 및 학기 정보 관련 API")
public class SemesterController {

    private final SemesterAcademicRecordService semesterAcademicRecordService;

    @GetMapping
    @Operation(summary = "사용자 학기 목록 조회", description = "사용자의 모든 학기 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<StudentSemesterDto.StudentSemesterInfoDto>> getSemesterRecord(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());

        List<StudentSemesterDto.StudentSemesterInfoDto> response = semesterAcademicRecordService.getSemestersByStudentEmail(userId);

        return ResponseEntity.ok(response);
    }
}
