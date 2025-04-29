package com.chukchuk.haksa.infrastructure.portal.exception;

import com.chukchuk.haksa.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PortalScrapeException extends RuntimeException {
    private final ErrorCode errorCode;

    public PortalScrapeException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public PortalScrapeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }
}