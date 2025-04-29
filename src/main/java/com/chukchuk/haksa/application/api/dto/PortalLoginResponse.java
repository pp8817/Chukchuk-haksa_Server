package com.chukchuk.haksa.application.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PortalLoginResponse", description = "포털 로그인 응답")
public class PortalLoginResponse {
    @Schema(description = "결과 메시지", example = "로그인 성공")
    String message;

    public PortalLoginResponse(String message) {
        this.message = message;
    }
}
