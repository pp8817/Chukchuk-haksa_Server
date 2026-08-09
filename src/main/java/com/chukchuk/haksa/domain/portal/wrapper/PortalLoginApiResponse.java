// 포털 로그인 검증 성공 응답의 Swagger 문서용 래퍼

package com.chukchuk.haksa.domain.portal.wrapper;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 포털 로그인 api 응답 데이터를 전달한다. */
@Schema(name = "PortalLoginApiResponse", description = "포털 로그인 검증 응답")
public class PortalLoginApiResponse extends SuccessResponse<PortalLinkDto.LoginResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public PortalLoginApiResponse() {
    super(new PortalLinkDto.LoginResponse("portal-verification-token"), "요청 성공");
  }
}
