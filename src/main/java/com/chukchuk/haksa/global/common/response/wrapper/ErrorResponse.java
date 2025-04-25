package com.chukchuk.haksa.global.common.response.wrapper;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "에러 상세 정보")
public record ErrorResponse(
        @Schema(description = "에러 코드", example = "A01, A02 ...")
        String code,

        @Schema(description = "에러 메시지", example = "에러메시지")
        String message
) {}
