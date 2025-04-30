package com.chukchuk.haksa.global.exception;


public class CommonException extends BaseException {
    public CommonException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CommonException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}