package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SignInApiResponse", description = "회원가입 응답 포맷")
public class SignInApiResponse extends ApiResponse<UserDto.SignInResponse> {
    public SignInApiResponse() {
        super(true, new UserDto.SignInResponse(true), null, null);
    }
}