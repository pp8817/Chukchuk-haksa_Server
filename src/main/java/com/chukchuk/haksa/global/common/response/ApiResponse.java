package com.chukchuk.haksa.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

// 공통 상위 인터페이스
@Schema(description = "API 응답 공통 인터페이스")
public interface ApiResponse {
    boolean isSuccess();
}