package com.chukchuk.haksa.global.exception;

public class TokenException extends RuntimeException {
    private final String code;

    public TokenException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public String getCode() {
        return code;
    }
}
