package com.chukchuk.haksa.application.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포털 재연동 및 학업 동기화 응답")
public record RefreshScrapingResponse(
        @Schema(description = "작업 ID", example = "123e4567-e89b-12d3-a456-426614174000")
        String taskId,

        @Schema(description = "결과 메시지", example = "포털 재연동 및 학업 이력 동기화 완료")
        String message
) {}