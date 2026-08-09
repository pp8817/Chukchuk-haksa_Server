package com.chukchuk.haksa.domain.lectureevaluations.wrapper;

import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/** 계층 간 전달할 데이터를 표현한다. */
@Schema(name = "LectureEvaluationSkipApiResponse", description = "강의평가 건너뛰기 응답")
public class LectureEvaluationSkipApiResponse extends SuccessResponse<MessageOnlyResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public LectureEvaluationSkipApiResponse() {
    super(new MessageOnlyResponse("강의평가 건너뛰기 완료"), "요청 성공");
  }
}
