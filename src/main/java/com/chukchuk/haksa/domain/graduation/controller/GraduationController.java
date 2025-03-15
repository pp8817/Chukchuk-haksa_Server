package com.chukchuk.haksa.domain.graduation.controller;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.service.GraduationService;
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
public class GraduationController {

    private final GraduationService graduationService;

    /**
     * 졸업 요건, 학사 성적 정보 조회 API
     */
    @GetMapping("/graduation-progress")
    public ResponseEntity<GraduationProgressDto> getGraduationProgress(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        GraduationProgressDto graduationProgress = graduationService.getGraduationProgress(email);

        return ResponseEntity.ok(graduationProgress);
    }
}
