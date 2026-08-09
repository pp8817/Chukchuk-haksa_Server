// 포털 링크 job 요약 조회 성공 응답의 Swagger 문서용 래퍼

package com.chukchuk.haksa.domain.portal.wrapper;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/** 척척학사의 포털 link 작업 summary api 응답 데이터를 전달한다. */
@Schema(name = "PortalLinkJobSummaryApiResponse", description = "포털 링크 job 요약 조회 응답")
public class PortalLinkJobSummaryApiResponse
    extends SuccessResponse<PortalLinkDto.JobSummaryResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public PortalLinkJobSummaryApiResponse() {
    super(
        new PortalLinkDto.JobSummaryResponse(
            "job-123",
            "succeeded",
            new PortalLinkDto.StudentInfoSummary("홍길동", "수원대학교", "소프트웨어학과", "17019013", 2, "재학", 1),
            Instant.parse("2026-05-29T08:10:00Z")),
        "요청 성공");
  }
}
