package com.chukchuk.haksa.domain.auth.dto;

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
}
