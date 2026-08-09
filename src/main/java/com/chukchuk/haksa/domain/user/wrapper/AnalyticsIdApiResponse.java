// 사용자 분석 식별자 조회 API의 문서용 응답 래퍼

package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 analytics id api 응답 데이터를 전달한다. */
@Schema(name = "AnalyticsIdApiResponse", description = "사용자 분석 식별자 조회 응답")
public class AnalyticsIdApiResponse extends SuccessResponse<UserDto.AnalyticsIdResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public AnalyticsIdApiResponse() {
    super(new UserDto.AnalyticsIdResponse("550e8400-e29b-41d4-a716-446655440000"), "요청 성공");
  }
}
