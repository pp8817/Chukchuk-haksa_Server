package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/** 척척학사의 success 응답 데이터를 전달한다. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "성공 응답 포맷")
public class SuccessResponse<T> {

  @Schema(description = "성공 여부", example = "true", required = true)
  private final boolean success = true;

  @Schema(description = "응답 데이터", required = true)
  private final T data;

  @Schema(description = "메시지", example = "요청 성공")
  private final String message;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param data 응답 데이터
   * @param message 응답 메시지
   */
  public SuccessResponse(T data, String message) {
    this.data = data;
    this.message = message;
  }

  /**
   * 전달된 데이터로 API 응답을 생성한다.
   *
   * @param data 응답 데이터
   * @param message 응답 메시지
   * @return success 응답 결과
   */
  public static <T> SuccessResponse<T> of(T data, String message) {
    return new SuccessResponse<>(data, message);
  }

  /**
   * 전달된 데이터로 API 응답을 생성한다.
   *
   * @param data 응답 데이터
   * @return success 응답 결과
   */
  public static <T> SuccessResponse<T> of(T data) {
    return new SuccessResponse<>(data, "요청 성공");
  }
}
