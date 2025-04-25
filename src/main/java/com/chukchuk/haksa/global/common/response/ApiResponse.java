package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "API 응답 공통 포맷")
public class ApiResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "메시지", example = "요청 성공")
    private String message;

    @Schema(description = "에러 정보")
    private ErrorDetail error;

    protected ApiResponse(boolean success, T data, String message, ErrorDetail error) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> successMessage(String message) {
        return new ApiResponse<>(true, null, message, null);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null, new ErrorDetail(code, message, null));
    }

    public static <T> ApiResponse<T> error(String code, String message, Object details) {
        return new ApiResponse<>(false, null, null, new ErrorDetail(code, message, details));
    }


}