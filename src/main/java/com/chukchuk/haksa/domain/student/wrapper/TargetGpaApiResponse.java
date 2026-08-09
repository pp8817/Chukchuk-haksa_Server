package com.chukchuk.haksa.domain.student.wrapper;

import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 target gpa api 응답 데이터를 전달한다. */
@Schema(name = "TargetGpaApiResponse", description = "목표 GPA 설정 응답")
public class TargetGpaApiResponse extends SuccessResponse<MessageOnlyResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public TargetGpaApiResponse() {
    super(new MessageOnlyResponse("목표 학점 저장 완료"), "요청 성공");
  }
}
