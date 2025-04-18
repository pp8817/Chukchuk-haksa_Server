package com.chukchuk.haksa.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Token 관련
    TOKEN_INVALID_FORMAT("T01", "ID 토큰 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NO_MATCHING_KEY("T02", "일치하는 공개키가 없습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_PARSE_ERROR("T03", "ID 토큰 검증 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("T04", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_ISS("T05", "유효하지 않은 iss 입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_AUD("T06", "유효하지 않은 aud 입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_AUD_FORMAT("T07", "aud 클레임 형식이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_NONCE("T08", "유효하지 않은 nonce 입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_HASH_ERROR("T09", "SHA-256 해싱 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    TOKEN_INVALID("T10", "토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // User 관련
    USER_NOT_FOUND("U01", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public HttpStatus status() {
        return status;
    }
}