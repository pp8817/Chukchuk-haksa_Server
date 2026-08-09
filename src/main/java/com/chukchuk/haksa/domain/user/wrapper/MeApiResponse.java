// 내 사용자 정보 조회 API의 문서용 응답 래퍼

package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 me api 응답 데이터를 전달한다. */
@Schema(name = "MeApiResponse", description = "내 사용자 정보 조회 응답")
public class MeApiResponse extends SuccessResponse<UserDto.MeResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public MeApiResponse() {
    super(new UserDto.MeResponse(true), "요청 성공");
  }
}
