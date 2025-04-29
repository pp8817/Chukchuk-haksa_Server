package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DeleteUserApiResponse", description = "회원 탈퇴 응답 포맷")
public class DeleteUserApiResponse extends SuccessResponse<MessageOnlyResponse> {

    public DeleteUserApiResponse() {
        super(new MessageOnlyResponse("회원 탈퇴가 완료되었습니다."), "요청 성공");
    }
}