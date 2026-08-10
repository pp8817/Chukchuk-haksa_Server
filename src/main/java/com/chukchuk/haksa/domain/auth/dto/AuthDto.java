package com.chukchuk.haksa.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Builder;

/** 계층 간 전달할 데이터를 표현한다. */
public class AuthDto {

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param token 응답에 포함할 토큰
   * @param expiry 만료 시각
   * @param sessionId 세션 식별자
   */
  @Schema(description = "만료 시간을 포함한 리프레시 토큰 정보")
  public record RefreshTokenWithExpiry(
      @Schema(description = "Refresh Token", required = true) String token,
      @Schema(description = "만료 시각", required = true) Date expiry,
      @Schema(description = "로그인 세션 식별자", required = true) String sessionId) {
    /**
     * 세션 식별자 없이 리프레시 토큰 정보를 생성한다.
     *
     * @param token 리프레시 토큰
     * @param expiry 토큰 만료 시각
     */
    public RefreshTokenWithExpiry(String token, Date expiry) {
      this(token, expiry, null);
    }
  }

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param refreshToken refresh token 원문
   */
  @Schema(description = "리프레시 토큰 요청 DTO")
  public record RefreshRequest(
      @Schema(description = "Refresh Token", required = true) String refreshToken) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param accessToken 응답에 포함할 접근 토큰
   * @param refreshToken refresh token 원문
   */
  @Schema(description = "Refresh Response DTO")
  public record RefreshResponse(
      @Schema(description = "액세스 토큰", required = true) String accessToken,
      @Schema(description = "리프레시 토큰", required = true) String refreshToken) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param accessToken 응답에 포함할 접근 토큰
   * @param refreshToken refresh token 원문
   * @param isPortalLinked is 포털 linked 여부
   */
  @Schema(description = "카카오 로그인 성공 시 반환되는 토큰 정보")
  @Builder
  public record SignInTokenResponse(
      @Schema(description = "액세스 토큰", required = true) String accessToken,
      @Schema(description = "리프레시 토큰", required = true) String refreshToken,
      @Schema(description = "포털 연동 여부", example = "true", required = true)
          boolean isPortalLinked) {}
}
