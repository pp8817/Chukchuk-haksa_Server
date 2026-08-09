// 포털 링크 job 상태 조회 성공 응답의 Swagger 문서용 래퍼

package com.chukchuk.haksa.domain.portal.wrapper;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** 척척학사의 포털 link 작업 status api 응답 데이터를 전달한다. */
@Schema(name = "PortalLinkJobStatusApiResponse", description = "포털 링크 job 상태 조회 응답")
public class PortalLinkJobStatusApiResponse
    extends SuccessResponse<PortalLinkDto.JobStatusResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public PortalLinkJobStatusApiResponse() {
    super(
        new PortalLinkDto.JobStatusResponse(
            "job-123",
            "suwon",
            "queued",
            null,
            null,
            null,
            Instant.parse("2026-05-29T08:00:00Z"),
            Instant.parse("2026-05-29T08:00:00Z"),
            null),
        "요청 성공");
  }
}
