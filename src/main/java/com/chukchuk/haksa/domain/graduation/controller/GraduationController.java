package com.chukchuk.haksa.domain.graduation.controller;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.service.GraduationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Graduation", description = "졸업 요건 및 진행 현황 관련 API")
public class GraduationController {

    private final GraduationService graduationService;

    /**
     * 졸업 요건, 학사 성적 정보 조회 API
     */
    @GetMapping("/graduation-progress")
    @Operation(summary = "졸업 요건 및 학사 성적 정보 조회", description = "로그인된 사용자의 졸업 요건 충족 여부 및 학사 성적 정보를 조회합니다.")
    public ResponseEntity<GraduationProgressDto> getGraduationProgress(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        GraduationProgressDto graduationProgress = graduationService.getGraduationProgress(email);
        return ResponseEntity.ok(graduationProgress);
    }
}
