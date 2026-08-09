package com.chukchuk.haksa.domain.user.dto;

import com.chukchuk.haksa.global.security.service.OidcProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/** 척척학사의 사용자 계층 간 데이터를 전달한다. */
public class UserDto {

  /**
   * 계층 간 전달할 sign in 요청 데이터를 표현한다.
   *
   * @param provider provider 값
   * @param idToken ID token
   * @param nonce nonce 값
   */
  @Schema(description = "소셜 로그인 요청 정보")
  public record SignInRequest(
      @Schema(description = "OIDC Provider", example = "KAKAO", required = true)
          OidcProvider provider,
      @JsonProperty("id_token") @Schema(description = "OIDC Provider에서 발급받은 ID 토큰", required = true)
          String idToken,
      @Schema(description = "로그인 시 사용한 nonce 값", example = "random_nonce_value", required = true)
          String nonce) {}

  /**
   * 계층 간 전달할 sign in 응답 데이터를 표현한다.
   *
   * @param accessToken 접근 토큰 값
   * @param refreshToken refresh token 원문
   * @param isPortalLinked is 포털 linked 여부
   */
  @Schema(description = "회원가입 및 로그인 응답")
  public record SignInResponse(
      @Schema(
              description = "Access Token",
              example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              required = true)
          String accessToken,
      @Schema(
              description = "Refresh Token",
              example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              required = true)
          String refreshToken,
      @Schema(description = "포털 연동 여부", example = "true", required = true)
          boolean isPortalLinked) {}

  /**
   * 계층 간 전달할 analytics id 응답 데이터를 표현한다.
   *
   * @param analyticsId analytics id 식별자
   */
  @Schema(description = "사용자 분석 식별자 응답")
  public record AnalyticsIdResponse(
      @Schema(
              description = "Amplitude 사용자 식별자",
              example = "550e8400-e29b-41d4-a716-446655440000",
              required = true)
          String analyticsId) {}

  /**
   * 계층 간 전달할 me 응답 데이터를 표현한다.
   *
   * @param isPortalLinked is 포털 linked 여부
   */
  @Schema(description = "내 사용자 정보 응답")
  public record MeResponse(
      @Schema(description = "포털 연동 여부", example = "true", required = true)
          boolean isPortalLinked) {}
}
