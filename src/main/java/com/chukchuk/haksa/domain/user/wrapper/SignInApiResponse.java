package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 sign in api 응답 데이터를 전달한다. */
@Schema(name = "SignInApiResponse", description = "회원가입 및 로그인 응답")
public class SignInApiResponse extends SuccessResponse<UserDto.SignInResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public SignInApiResponse() {
    super(
        new UserDto.SignInResponse(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            true),
        "요청 성공");
  }
}
