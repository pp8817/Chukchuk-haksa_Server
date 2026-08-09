package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;

/** 학생 학기 list api 응답 데이터를 전달한다. */
@Schema(name = "StudentSemesterListApiResponse", description = "사용자 학기 목록 응답")
public class StudentSemesterListApiResponse
    extends SuccessResponse<List<StudentSemesterDto.StudentSemesterInfoResponse>> {
  /** 학생 학기 list api 응답 인스턴스를 생성한다. */
  public StudentSemesterListApiResponse() {
    super(Collections.emptyList(), "요청 성공");
  }
}
