package com.chukchuk.haksa.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 계층 간 전달할 메시지 only 응답 데이터를 표현한다.
 *
 * @param message 응답 메시지
 */
@Schema(description = "메시지 응답 DTO")
public record MessageOnlyResponse(
    @Schema(description = "결과 메시지", example = "목표 학점 저장 완료") String message) {}
