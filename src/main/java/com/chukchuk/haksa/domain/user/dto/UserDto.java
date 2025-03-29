package com.chukchuk.haksa.domain.user.dto;

import lombok.Builder;

public class UserDto {
    public record SignInUserRequest(
            String id_token,
            String nonce
    ) {

    }

    @Builder
    public record SignInUserResponse(
            String status,
            String accessToken
    ) {

    }
}
