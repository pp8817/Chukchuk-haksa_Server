package com.chukchuk.haksa.global.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String code;

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public String getCode() {
        return code;
    }
}
