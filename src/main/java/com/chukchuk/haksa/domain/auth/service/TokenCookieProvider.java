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

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false) // local 테스트용으로 false 설정
                .path("/")
                .maxAge(accessTokenExpiration / 1000)
                .sameSite("None") // 추후 도메인 일치시켜 변경 Strict로 변경 필요
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // local 테스트용으로 false 설정
                .path("/")
                .maxAge(refreshTokenExpiration / 1000)
                .sameSite("None") // 추후 도메인 일치시켜 변경 Strict로 변경 필요
                .build();
    }
}