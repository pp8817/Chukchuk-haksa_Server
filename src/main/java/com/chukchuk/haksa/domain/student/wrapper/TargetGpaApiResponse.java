package com.chukchuk.haksa.domain.student.wrapper;

import com.chukchuk.haksa.global.common.response.ApiResponse;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TargetGpaApiResponse", description = "목표 GPA 설정 응답")
public class TargetGpaApiResponse extends ApiResponse<MessageOnlyResponse> {
    public TargetGpaApiResponse() {
        super(true, new MessageOnlyResponse("목표 학점 저장 완료"), null, null);
    }
}