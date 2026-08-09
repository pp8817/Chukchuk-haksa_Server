package com.chukchuk.haksa.domain.auth.wrapper;

import com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 계층 간 전달할 데이터를 표현한다. */
@Schema(name = "RefreshTokenApiResponse", description = "토큰 재발급 응답 포맷")
public class RefreshTokenApiResponse extends SuccessResponse<RefreshResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public RefreshTokenApiResponse() {
    super(new RefreshResponse("new-access-token", "new-refresh-token"), "요청 성공");
  }
}
