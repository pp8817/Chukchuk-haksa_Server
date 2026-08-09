package com.chukchuk.haksa.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/** API 오류의 필드별 상세 정보를 표현한다. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Schema(description = "에러 상세 정보")
public class ErrorDetail {

  @Schema(description = "에러 코드", example = "U01", required = true)
  private String code;

  @Schema(description = "에러 메시지", example = "해당 사용자를 찾을 수 없습니다.", required = true)
  private String message;

  @Schema(description = "에러 추가 정보", nullable = true)
  private Object details;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param code code 값
   * @param message 응답 메시지
   * @param details 상세 정보
   */
  public ErrorDetail(String code, String message, Object details) {
    this.code = code;
    this.message = message;
    this.details = details;
  }
}
