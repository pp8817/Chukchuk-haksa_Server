package com.chukchuk.haksa.application.academic.wrapper;

import com.chukchuk.haksa.application.dto.ScrapingResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 스크래핑 api 응답 데이터를 전달한다. */
@Schema(name = "ScrapingApiResponse", description = "포털 데이터 크롤링 응답")
public class ScrapingApiResponse extends SuccessResponse<ScrapingResponse> {

  /** 스크래핑 api 응답 인스턴스를 생성한다. */
  public ScrapingApiResponse() {
    super(ScrapingResponse.success("dummy-task-id", null), "요청 성공");
  }
}
