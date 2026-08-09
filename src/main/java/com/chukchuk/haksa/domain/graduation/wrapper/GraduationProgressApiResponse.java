package com.chukchuk.haksa.domain.graduation.wrapper;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;

/** 졸업 progress api 응답 데이터를 전달한다. */
@Schema(name = "GraduationProgressApiResponse", description = "졸업 요건 진행 상황 응답")
public class GraduationProgressApiResponse extends SuccessResponse<GraduationProgressResponse> {

  /** 졸업 progress api 응답 인스턴스를 생성한다. */
  public GraduationProgressApiResponse() {
    super(new GraduationProgressResponse(Collections.emptyList()), "요청 성공");
  }
}
