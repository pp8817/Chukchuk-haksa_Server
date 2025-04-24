package com.chukchuk.haksa.domain.user.dto;

public class UserDto {
    public record SignInRequest(
            String id_token,
            String nonce
    ) {

    }

    public record PortalLinkStatusResponse(boolean isPortalLinked) {}
}
