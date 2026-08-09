// PortalClient를 통해 포털 로그인 검증 요청을 수행하는 verifier 구현체

package com.chukchuk.haksa.infrastructure.portal.client;

import com.chukchuk.haksa.application.portal.PortalLoginVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 척척학사의 포털 클라이언트 로그인 입력 값과 인증 조건을 검증한다. */
@Component
@RequiredArgsConstructor
public class PortalClientLoginVerifier implements PortalLoginVerifier {

  private final PortalClient portalClient;

  @Override
  public void verify(String portalType, String username, String password) {
    portalClient.validateLogin(username, password);
  }
}
