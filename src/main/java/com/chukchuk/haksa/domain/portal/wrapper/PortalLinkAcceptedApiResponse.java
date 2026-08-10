// 포털 링크 job 생성 성공 응답의 Swagger 문서용 래퍼

package com.chukchuk.haksa.domain.portal.wrapper;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 포털 연동 작업 생성 성공 응답의 OpenAPI 스키마 예시를 제공한다. */
@Schema(name = "PortalLinkAcceptedApiResponse", description = "포털 링크 job 생성 수락 응답")
public class PortalLinkAcceptedApiResponse extends SuccessResponse<PortalLinkDto.AcceptedResponse> {

  /** 포털 link accepted api 응답 인스턴스를 생성한다. */
  public PortalLinkAcceptedApiResponse() {
    super(
        new PortalLinkDto.AcceptedResponse("job-123", "accepted", "/portal/link/jobs/job-123"),
        "요청 성공");
  }
}
