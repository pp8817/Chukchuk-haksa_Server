package com.chukchuk.haksa.domain.user.controller;

import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.security.service.KakaoOidcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관련 API")
@Slf4j
public class UserController {

    private final UserService userService;
    private final KakaoOidcService kakaoOidcService;

    @DeleteMapping("/users/delete")
    @Operation(summary = "회원 탈퇴", description = "로그인된 사용자의 계정을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/users/signin")
    @Operation(summary = "회원 가입", description = "사용자 회원가입을 진행합니다.")
    public ResponseEntity<?> signInUser(
            @RequestBody UserDto.SignInUserRequest signInUserRequest
            ) {
        try {
            log.info(signInUserRequest.id_token(), signInUserRequest.nonce());
            userService.signInWithKakao(signInUserRequest.id_token(), signInUserRequest.nonce());

            return ResponseEntity.ok(UserDto.SignInUserResponse.builder()
                    .status("200")
                            .accessToken(signInUserRequest.id_token())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(UserDto.SignInUserResponse.builder().status("400").build());
        }
    }
}
