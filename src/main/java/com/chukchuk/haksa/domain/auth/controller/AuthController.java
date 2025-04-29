package com.chukchuk.haksa.domain.auth.controller;

import com.chukchuk.haksa.domain.auth.service.RefreshTokenService;
import com.chukchuk.haksa.domain.auth.wrapper.RefreshTokenApiResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshRequest;
import static com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰을 사용해 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "토큰 재발급 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RefreshTokenApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (ErrorCode: T04, T10, T12, T11)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자 정보 없음 (ErrorCode: U01)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponseWrapper.class)))
            }
    )
    public ResponseEntity<ApiResponse<RefreshResponse>> refreshResponse(@RequestBody RefreshRequest request) {
        RefreshResponse response = refreshTokenService.reissue(request.refreshToken());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
