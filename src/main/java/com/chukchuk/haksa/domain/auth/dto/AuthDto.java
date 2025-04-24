package com.chukchuk.haksa.domain.auth.dto;

import lombok.Builder;

import java.util.Date;

public class AuthDto {
    public record RefreshTokenWithExpiry(
            String token,
            Date expiry
    ) {
    }

    public record RefreshRequest(String refreshToken) {

    }

    public record RefreshResponse(String accessToken, String refreshToken) {

    }

    @Builder
    public record SignInTokenResponse(
            String accessToken,
            String refreshToken,
            boolean isPortalLinked
    ) {

    }
}
