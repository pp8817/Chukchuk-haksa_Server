package com.chukchuk.haksa.domain.user.controller;

import com.chukchuk.haksa.domain.auth.service.TokenCookieProvider;
import com.chukchuk.haksa.domain.user.dto.TestUserDto;
import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.service.TestUserService;
import com.chukchuk.haksa.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
@Slf4j
public class UserController {

    private final UserService userService;
    private final TestUserService testUserService;
    private final TokenCookieProvider tokenCookieProvider;

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "로그인된 사용자의 계정을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        userService.deleteUserByEmail(userId);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/signin")
    @Operation(
            summary = "회원 가입",
            description = "사용자 회원가입을 진행합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인 성공 시 accessToken / refreshToken 쿠키를 반환합니다.",
                            headers = {
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "accessToken이 담긴 HttpOnly 쿠키",
                                            schema = @Schema(type = "string")
                                    ),
                                    @Header(
                                            name = "Set-Cookie",
                                            description = "refreshToken이 담긴 HttpOnly 쿠키",
                                            schema = @Schema(type = "string")
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "카카오 로그인 실패 또는 유효하지 않은 요청"
                    )
            }
    )
    public ResponseEntity<?> signInUser(
            @RequestBody UserDto.SignInRequest signInRequest
    ) {
        try {
            UserDto.SignInResponse signInResponse = userService.signInWithKakao(signInRequest);

            ResponseCookie accessCookie = tokenCookieProvider.createAccessTokenCookie(signInResponse.accessToken());
            ResponseCookie refreshCookie = tokenCookieProvider.createRefreshTokenCookie(signInResponse.refreshToken());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
            headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(UserDto.SignInResponse.builder()
                            .status(signInResponse.status())
                            .build()); // body에서 토큰은 제거
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(UserDto.SignInResponse.builder().status(HttpStatus.UNAUTHORIZED).build());
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Test 계정 정보 조회")
    public ResponseEntity<TestUserDto> getTestUser() {
        User user = testUserService.getTestUser();
        return ResponseEntity.ok(TestUserDto.from(user));
    }
}