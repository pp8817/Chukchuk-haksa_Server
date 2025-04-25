package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter

@Schema(description = "에러 상세 정보")
public class ErrorDetail {

    @Schema(description = "에러 코드", example = "USER_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "해당 사용자를 찾을 수 없습니다.")
    private String message;

    @Schema(description = "에러 추가 정보")
    private Object details;

    public ErrorDetail(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

}
