package com.chukchuk.haksa.global.exception;

public enum ErrorCode {
    TOKEN_INVALID_FORMAT("T01", "ID 토큰 형식이 올바르지 않습니다."),
    TOKEN_NO_MATCHING_KEY("T02", "일치하는 공개키가 없습니다."),
    TOKEN_PARSE_ERROR("T03", "ID 토큰 검증 중 오류가 발생했습니다."),
    TOKEN_EXPIRED("T04", "만료된 토큰입니다."),
    TOKEN_INVALID_ISS("T05", "유효하지 않은 iss 입니다."),
    TOKEN_INVALID_AUD("T06", "유효하지 않은 aud 입니다."),
    TOKEN_INVALID_AUD_FORMAT("T07", "aud 클레임 형식이 올바르지 않습니다."),
    TOKEN_INVALID_NONCE("T08", "유효하지 않은 nonce 입니다."),
    TOKEN_HASH_ERROR("T09", "SHA-256 해싱 중 오류가 발생했습니다."),
    TOKEN_INVALID("T10", "토큰이 유효하지 않습니다."),

    USER_NOT_FOUND("U01", "해당 사용자를 찾을 수 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}