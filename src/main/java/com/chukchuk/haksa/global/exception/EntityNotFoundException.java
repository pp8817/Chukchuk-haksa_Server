package com.chukchuk.haksa.global.exception;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EntityNotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}