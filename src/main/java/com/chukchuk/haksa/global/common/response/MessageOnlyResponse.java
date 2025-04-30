package com.chukchuk.haksa.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "메시지 응답 DTO")
public record MessageOnlyResponse(
        @Schema(description = "결과 메시지", example = "목표 학점 저장 완료")
        String message
) {}