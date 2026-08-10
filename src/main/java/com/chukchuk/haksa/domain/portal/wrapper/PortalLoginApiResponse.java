// 포털 로그인 검증 성공 응답의 Swagger 문서용 래퍼

package com.chukchuk.haksa.domain.portal.wrapper;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 포털 자격 증명 검증 성공 응답의 OpenAPI 스키마 예시를 제공한다. */
@Schema(name = "PortalLoginApiResponse", description = "포털 로그인 검증 응답")
public class PortalLoginApiResponse extends SuccessResponse<PortalLinkDto.LoginResponse> {

  /** 포털 자격 증명 검증 성공 응답의 예시 값과 메시지를 구성한다. */
  public PortalLoginApiResponse() {
    super(new PortalLinkDto.LoginResponse("portal-verification-token"), "요청 성공");
  }
}
