package com.chukchuk.haksa.domain.user.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

public class UserDto {
    public record SignInRequest(
            String id_token,
            String nonce
    ) {

    }

    @Builder
    public record SignInResponse(
            HttpStatus status,
            String accessToken,
            String refreshToken
    ) {

    }
}
