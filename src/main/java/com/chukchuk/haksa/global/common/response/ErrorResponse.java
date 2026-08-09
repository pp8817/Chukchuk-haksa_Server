package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/** 척척학사의 오류 응답 데이터를 전달한다. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "API 실패 응답 포맷")
public class ErrorResponse {

  @Schema(description = "성공 여부", example = "false", required = true)
  private final boolean success = false;

  @Schema(description = "에러 상세")
  private final ErrorDetail error;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param error 오류 값
   */
  protected ErrorResponse(ErrorDetail error) {
    this.error = error;
  }

  /**
   * 전달된 데이터로 API 응답을 생성한다.
   *
   * @param code code 값
   * @param message 응답 메시지
   * @return 오류 응답 결과
   */
  public static ErrorResponse of(String code, String message) {
    return new ErrorResponse(new ErrorDetail(code, message, null));
  }

  /**
   * 전달된 데이터로 API 응답을 생성한다.
   *
   * @param code code 값
   * @param message 응답 메시지
   * @param details 상세 정보
   * @return 오류 응답 결과
   */
  public static ErrorResponse of(String code, String message, Object details) {
    return new ErrorResponse(new ErrorDetail(code, message, details));
  }
}
