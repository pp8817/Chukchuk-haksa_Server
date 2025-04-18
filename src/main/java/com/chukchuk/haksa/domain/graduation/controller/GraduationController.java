package com.chukchuk.haksa.domain.graduation.controller;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.service.GraduationService;
import com.chukchuk.haksa.global.common.response.ApiResponse;
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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/graduation")
@Tag(name = "Graduation", description = "졸업 요건 및 진행 현황 관련 API")
public class GraduationController {

    private final GraduationService graduationService;

    /**
     * 졸업 요건 진행 상황 조회 API
     */
    @GetMapping("/progress")
    @Operation(summary = "졸업 요건 진행 상황 조회", description = "로그인된 사용자의 졸업 요건 충족 여부를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<GraduationProgressDto>> getGraduationProgress(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        GraduationProgressDto response = graduationService.getGraduationProgress(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
