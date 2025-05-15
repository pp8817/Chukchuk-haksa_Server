package com.chukchuk.haksa.domain.user.controller;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.domain.user.wrapper.DeleteUserApiResponse;
import com.chukchuk.haksa.domain.user.wrapper.SignInApiResponse;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
@Slf4j
public class UserController {

    private final UserService userService;

    @DeleteMapping("/delete")
    @Operation(
            summary = "회원 탈퇴",
            description = "로그인된 사용자의 계정을 삭제합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteUserApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "사용자 정보 없음 (ErrorCode: U01, USER_NOT_FOUND)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))
                    )
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<MessageOnlyResponse>> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        userService.deleteUserById(userId);

        return ResponseEntity.ok(SuccessResponse.of(new MessageOnlyResponse("회원 탈퇴가 완료되었습니다.")));
    }

    /* 회원가입/로그인 */
    @PostMapping("/signin")
    @Operation(
            summary = "회원 가입 및 로그인",
            description = "사용자가 카카오 소셜 로그인으로 회원가입 및 로그인을 진행합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "회원가입/로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SignInApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "토큰 유효성 오류 (ErrorCode: T10, TOKEN_INVALID)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "만료된 토큰 오류 (ErrorCode: T04, TOKEN_EXPIRED)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류 (ErrorCode: INTERNAL_ERROR)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class)))
            }
    )
    public ResponseEntity<SuccessResponse<UserDto.SignInResponse>> signInUser(
            @RequestBody UserDto.SignInRequest signInRequest
            ) {
        AuthDto.SignInTokenResponse tokens = userService.signInWithKakao(signInRequest);

        UserDto.SignInResponse response = new UserDto.SignInResponse(tokens.accessToken(), tokens.refreshToken(), tokens.isPortalLinked());

        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}