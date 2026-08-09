// 포털 로그인 검증 후 연동용 검증 token을 발급하는 서비스

package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 포털 로그인 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
public class PortalLoginService {

  private final PortalLoginVerifier portalLoginVerifier;
  private final PortalLoginVerificationTokenService tokenService;

  /**
   * 로그인 처리를 수행한다.
   *
   * @param userId 사용자 식별자
   * @param request 요청 정보
   * @return 포털 link dto 로그인 응답 결과
   */
  public PortalLinkDto.LoginResponse login(UUID userId, PortalLinkDto.LoginRequest request) {
    validateRequest(request);
    portalLoginVerifier.verify(request.portalType(), request.username(), request.password());
    String token =
        tokenService.issue(userId, request.portalType(), request.username(), request.password());
    return new PortalLinkDto.LoginResponse(token);
  }

  private void validateRequest(PortalLinkDto.LoginRequest request) {
    if (request.username() == null
        || request.username().isBlank()
        || request.password() == null
        || request.password().isBlank()) {
      throw new CommonException(ErrorCode.INVALID_ARGUMENT);
    }
    if (!"suwon".equals(normalize(request.portalType()))) {
      throw new CommonException(ErrorCode.UNSUPPORTED_PORTAL_TYPE);
    }
  }

  private String normalize(String portalType) {
    return portalType == null ? "" : portalType.trim().toLowerCase();
  }
}
