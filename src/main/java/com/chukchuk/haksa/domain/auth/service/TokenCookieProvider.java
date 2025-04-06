package com.chukchuk.haksa.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCookieProvider {

    @Value("${security.jwt.access-expiration}")
    private long accessTokenExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Value("${security.cookie.dev-mode:false}")
    private boolean devMode; // 로컬 프론트와 배포 백엔드 조합에서 true로 설정

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(httpOnly())
                .secure(true)
                .path("/")
                .maxAge(accessTokenExpiration / 1000)
                .sameSite("None")
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(httpOnly())
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("None")
                .build();
    }

    private boolean httpOnly() {
        return !devMode;
    }
}