// 포털 로그인 검증 API의 Swagger 문서 계약

package com.chukchuk.haksa.domain.portal.controller.docs;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.domain.portal.wrapper.PortalLoginApiResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

/** 포털 자격 증명을 검증하고 연동용 일회성 토큰을 발급하는 API를 정의한다. */
@Tag(name = "Portal Link", description = "비동기 포털 연동 job 생성 및 폴링 안내")
public interface PortalLoginControllerDocs {

  /**
   * 포털 계정 자격 증명을 검증하고 연동 작업에 사용할 일회용 토큰을 발급한다.
   *
   * @param userDetails 사용자 상세 정보
   * @param request 요청 정보
   * @return 포털 자격 증명 검증 후 발급한 일회용 검증 토큰 응답
   */
  @Operation(
      summary = "포털 로그인 검증",
      description = "포털 ID/PW를 검증하고 포털 연동 job 생성에 사용할 verification token을 반환합니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "로그인 검증 성공",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PortalLoginApiResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 입력 (INVALID_ARGUMENT)",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseWrapper.class))),
        @ApiResponse(
            responseCode = "401",
            description = "자격 증명 오류 (PORTAL_LOGIN_FAILED)",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseWrapper.class))),
        @ApiResponse(
            responseCode = "423",
            description = "포털 계정 잠금 (PORTAL_ACCOUNT_LOCKED)",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseWrapper.class)))
      })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<PortalLinkDto.LoginResponse>> verifyPortalLogin(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody PortalLinkDto.LoginRequest request);
}
