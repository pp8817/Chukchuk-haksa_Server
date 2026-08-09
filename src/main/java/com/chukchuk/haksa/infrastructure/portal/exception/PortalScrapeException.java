package com.chukchuk.haksa.infrastructure.portal.exception;

import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.BaseException;

public class PortalScrapeException extends BaseException {

  public PortalScrapeException(ErrorCode errorCode) {
    super(errorCode);
  }

  public PortalScrapeException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
