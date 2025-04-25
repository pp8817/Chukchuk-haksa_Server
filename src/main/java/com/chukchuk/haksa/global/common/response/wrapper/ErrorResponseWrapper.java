package com.chukchuk.haksa.global.common.response.wrapper;

import com.chukchuk.haksa.global.common.response.ApiResponse;
import com.chukchuk.haksa.global.common.response.ErrorDetail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponseWrapper", description = "API 에러 응답 포맷")
public class ErrorResponseWrapper extends ApiResponse<ErrorResponse> {

    protected ErrorResponseWrapper(boolean success, ErrorResponse data, String message, ErrorDetail error) {
        super(success, data, message, error);
    }
}