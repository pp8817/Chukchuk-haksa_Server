package com.chukchuk.haksa.global.exception.type;

import com.chukchuk.haksa.global.exception.code.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 척척학사의 base 처리할 수 없는 도메인 또는 요청 상태를 나타낸다. */
@Getter
public abstract class BaseException extends RuntimeException {
  private final ErrorCode errorCode;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   */
  protected BaseException(ErrorCode errorCode) {
    super(errorCode.message());
    this.errorCode = errorCode;
  }

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   * @param cause 원인 예외
   */
  protected BaseException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.message(), cause);
    this.errorCode = errorCode;
  }

  public String getCode() {
    return errorCode.code();
  }

  public HttpStatus getStatus() {
    return errorCode.status();
  }
}
