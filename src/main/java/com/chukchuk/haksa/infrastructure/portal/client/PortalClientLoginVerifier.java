// PortalClient를 통해 포털 로그인 검증 요청을 수행하는 verifier 구현체

package com.chukchuk.haksa.infrastructure.portal.client;

import com.chukchuk.haksa.application.portal.PortalLoginVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 수원대학교 PortalClient를 사용해 포털 자격 증명의 로그인 가능 여부를 검증한다. */
@Component
@RequiredArgsConstructor
public class PortalClientLoginVerifier implements PortalLoginVerifier {

  private final PortalClient portalClient;

  @Override
  public void verify(String portalType, String username, String password) {
    portalClient.validateLogin(username, password);
  }
}
