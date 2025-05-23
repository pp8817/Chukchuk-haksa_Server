package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

@Schema(name = "StudentSemesterListApiResponse", description = "사용자 학기 목록 응답")
public class StudentSemesterListApiResponse extends SuccessResponse<List<StudentSemesterDto.StudentSemesterInfoResponse>> {
    public StudentSemesterListApiResponse() {
        super(Collections.emptyList(), "요청 성공");
    }
}
