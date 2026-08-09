package com.chukchuk.haksa.domain.user.service;

import io.jsonwebtoken.Claims;

/** 구현체가 제공해야 할 oidc service 기능의 계약을 정의한다. */
public interface OidcService {
  /**
   * 입력 값과 업무 처리 조건을 검증한다.
   *
   * @param idToken ID token
   * @param nonce nonce 값
   * @return claims
   */
  Claims verifyIdToken(String idToken, String nonce);
}
