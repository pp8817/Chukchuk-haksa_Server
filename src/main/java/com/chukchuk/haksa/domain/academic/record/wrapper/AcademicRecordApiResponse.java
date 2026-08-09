package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 학사 record api 응답 데이터를 전달한다. */
@Schema(name = "AcademicRecordApiResponse", description = "학기별 성적 및 수강 과목 정보 응답")
public class AcademicRecordApiResponse extends SuccessResponse<AcademicRecordResponse> {

  /** 학사 record api 응답 인스턴스를 생성한다. */
  public AcademicRecordApiResponse() {
    super(
        new AcademicRecordResponse(
            null, new AcademicRecordResponse.Courses(List.of(), List.of(), List.of())),
        "요청 성공");
  }
}
