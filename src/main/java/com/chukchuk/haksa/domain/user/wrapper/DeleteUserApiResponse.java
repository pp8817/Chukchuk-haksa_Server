package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.global.common.response.ApiResponse;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DeleteUserApiResponse", description = "회원 탈퇴 응답 포맷")
public class DeleteUserApiResponse extends ApiResponse<MessageOnlyResponse> {
    public DeleteUserApiResponse() {
        super(true, new MessageOnlyResponse("회원 탈퇴가 완료되었습니다."), null, null);
    }
}