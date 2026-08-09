package com.chukchuk.haksa.global.exception.type;

import com.chukchuk.haksa.global.exception.code.ErrorCode;

/** 척척학사의 entity not found 처리할 수 없는 도메인 또는 요청 상태를 나타낸다. */
public class EntityNotFoundException extends BaseException {
  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   */
  public EntityNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param errorCode 오류 코드
   * @param cause 원인 예외
   */
  public EntityNotFoundException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
