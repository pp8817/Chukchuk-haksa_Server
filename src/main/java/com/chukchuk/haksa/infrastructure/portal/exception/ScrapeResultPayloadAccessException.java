package com.chukchuk.haksa.infrastructure.portal.exception;

import lombok.Getter;

/** 척척학사의 스크래핑 결과 payload 접근 처리할 수 없는 도메인 또는 요청 상태를 나타낸다. */
@Getter
public class ScrapeResultPayloadAccessException extends RuntimeException {

  private final String errorCode;
  private final boolean retryable;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   * @param message 응답 메시지
   * @param retryable retryable 값
   */
  public ScrapeResultPayloadAccessException(String errorCode, String message, boolean retryable) {
    super(message);
    this.errorCode = errorCode;
    this.retryable = retryable;
  }

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   * @param message 응답 메시지
   * @param retryable retryable 값
   * @param cause 원인 예외
   */
  public ScrapeResultPayloadAccessException(
      String errorCode, String message, boolean retryable, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
    this.retryable = retryable;
  }
}
