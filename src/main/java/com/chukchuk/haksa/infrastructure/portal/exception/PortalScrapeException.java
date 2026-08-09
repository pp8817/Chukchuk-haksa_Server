package com.chukchuk.haksa.infrastructure.portal.exception;

import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.BaseException;

/** 척척학사의 포털 스크래핑 처리할 수 없는 도메인 또는 요청 상태를 나타낸다. */
public class PortalScrapeException extends BaseException {

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   */
  public PortalScrapeException(ErrorCode errorCode) {
    super(errorCode);
  }

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   * @param cause 원인 예외
   */
  public PortalScrapeException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
