package com.chukchuk.haksa.application.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PortalLoginMessageResponse", description = "포털 로그인 메시지 응답")
public class PortalLoginMessageResponse {
    @Schema(description = "결과 메시지", example = "로그인 성공")
    String message;

    public PortalLoginMessageResponse(String message) {
        this.message = message;
    }
}
