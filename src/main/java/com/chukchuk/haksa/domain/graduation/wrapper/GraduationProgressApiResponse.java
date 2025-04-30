package com.chukchuk.haksa.domain.graduation.wrapper;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;

@Schema(name = "GraduationProgressApiResponse", description = "졸업 요건 진행 상황 응답")
public class GraduationProgressApiResponse extends SuccessResponse<GraduationProgressResponse> {

    public GraduationProgressApiResponse() {
        super(new GraduationProgressResponse(Collections.emptyList()), "요청 성공");
    }
}