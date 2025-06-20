package com.chukchuk.haksa.domain.graduation.controller;

import com.chukchuk.haksa.domain.graduation.controller.docs.GraduationControllerDocs;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.service.GraduationService;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/graduation")
public class GraduationController implements GraduationControllerDocs {

    private final GraduationService graduationService;

    @GetMapping("/progress")
    public ResponseEntity<SuccessResponse<GraduationProgressResponse>> getGraduationProgress(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID studentId = userDetails.getStudentId();
        GraduationProgressResponse response = graduationService.getGraduationProgress(studentId);
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}