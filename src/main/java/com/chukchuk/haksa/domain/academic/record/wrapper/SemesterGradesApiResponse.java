package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterSummaryResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;

/** 학기 grades api 응답 데이터를 전달한다. */
@Schema(name = "SemesterGradesApiResponse", description = "학기별 성적 목록 응답")
public class SemesterGradesApiResponse extends SuccessResponse<List<SemesterSummaryResponse>> {
  /** 학기 grades api 응답 인스턴스를 생성한다. */
  public SemesterGradesApiResponse() {
    super(Collections.emptyList(), "요청 성공");
  }
}
