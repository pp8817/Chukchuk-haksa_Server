package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {
    private String code;
    private String message;
    private Object details;

    public ErrorDetail(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    // Getter 생략 가능 (또는 @Getter 사용)
}
