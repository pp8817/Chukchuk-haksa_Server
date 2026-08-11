// 포털 로그인 검증 후 연동용 검증 token을 발급하는 서비스

package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 포털 자격 증명을 확인하고 후속 연동 요청에 사용할 단기 검증 토큰을 발급한다. */
@Service
@RequiredArgsConstructor
public class PortalLoginService {

  private final PortalLoginVerifier portalLoginVerifier;
  private final PortalLoginVerificationTokenService tokenService;

  /**
   * 지원 포털의 자격 증명을 검증하고 해당 계정에 결합된 연동용 토큰을 발급한다.
   *
   * @param userId 사용자 식별자
   * @param request 포털 유형과 로그인 아이디·비밀번호를 담은 요청
   * @return 후속 포털 연동 요청에 제출할 검증 토큰
   * @throws CommonException 요청이 비어 있거나 지원하지 않는 포털인 경우
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
