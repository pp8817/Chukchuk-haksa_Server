package com.chukchuk.haksa.domain.lectureevaluations.wrapper;

import com.chukchuk.haksa.domain.academic.record.model.LectureEvaluationStatus;
import com.chukchuk.haksa.domain.lectureevaluations.dto.LectureEvaluationDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 계층 간 전달할 데이터를 표현한다. */
@Schema(name = "LectureEvaluationRequiredApiResponse", description = "강의평가 상태 응답")
public class LectureEvaluationRequiredApiResponse
    extends SuccessResponse<LectureEvaluationDto.RequiredResponse> {

  /** 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다. */
  public LectureEvaluationRequiredApiResponse() {
    super(
        new LectureEvaluationDto.RequiredResponse(
            LectureEvaluationStatus.PENDING, 2026, 10, List.of()),
        "요청 성공");
  }
}
