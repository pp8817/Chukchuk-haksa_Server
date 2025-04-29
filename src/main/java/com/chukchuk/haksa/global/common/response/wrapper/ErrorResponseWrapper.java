package com.chukchuk.haksa.global.common.response.wrapper;

import com.chukchuk.haksa.global.common.response.ErrorDetail;
import com.chukchuk.haksa.global.common.response.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponseWrapper", description = "API 에러 응답 포맷")
public class ErrorResponseWrapper extends ErrorResponse {

    public ErrorResponseWrapper() {
        super(new ErrorDetail("ERROR_CODE", "에러 메시지", null));
    }
}