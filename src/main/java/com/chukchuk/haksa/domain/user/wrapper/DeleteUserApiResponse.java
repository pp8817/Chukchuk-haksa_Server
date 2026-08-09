package com.chukchuk.haksa.domain.user.wrapper;

import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 delete 사용자 api 응답 데이터를 전달한다. */
@Schema(name = "DeleteUserApiResponse", description = "회원 탈퇴 응답 포맷")
public class DeleteUserApiResponse extends SuccessResponse<MessageOnlyResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public DeleteUserApiResponse() {
    super(new MessageOnlyResponse("회원 탈퇴가 완료되었습니다."), "요청 성공");
  }
}
