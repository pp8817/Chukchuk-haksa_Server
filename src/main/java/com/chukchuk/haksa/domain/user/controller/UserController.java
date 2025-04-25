package com.chukchuk.haksa.domain.user.controller;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.auth.service.TokenCookieProvider;
import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    private final TokenCookieProvider tokenCookieProvider;

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴", description = "로그인된 사용자의 계정을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        userService.deleteUserByEmail(userId);

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

    /* 회원가입/로그인 */
    @PostMapping("/signin")
    @Operation(
            summary = "회원 가입",
            description = "사용자 회원가입을 진행합니다.")
    public ResponseEntity<ApiResponse<UserDto.PortalLinkStatusResponse>> signInUser(
            @RequestBody UserDto.SignInRequest signInRequest
            ) {
        AuthDto.SignInTokenResponse tokens = userService.signInWithKakao(signInRequest);

        ResponseCookie accessCookie = tokenCookieProvider.createAccessTokenCookie(tokens.accessToken());
        ResponseCookie refreshCookie = tokenCookieProvider.createRefreshTokenCookie(tokens.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        UserDto.PortalLinkStatusResponse body = new UserDto.PortalLinkStatusResponse(tokens.isPortalLinked());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.success(body));
    }
}
