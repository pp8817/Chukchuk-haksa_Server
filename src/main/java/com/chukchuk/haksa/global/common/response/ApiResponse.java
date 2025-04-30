package com.chukchuk.haksa.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 모든 응답의 부모 인터페이스
 */
@Schema(description = "API 응답 최상위 인터페이스")
public interface ApiResponse {
    boolean isSuccess();
}