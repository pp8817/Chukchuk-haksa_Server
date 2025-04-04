package com.chukchuk.haksa.domain.auth.controller;

import com.chukchuk.haksa.domain.auth.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshRequest;
import static com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다.")
    public RefreshResponse refreshResponse(@RequestBody RefreshRequest request) {
        return refreshTokenService.reissue(request.refreshToken());
    }
}
