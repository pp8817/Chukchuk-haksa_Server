package com.chukchuk.haksa.domain.auth.controller;

import com.chukchuk.haksa.domain.auth.controller.docs.AuthControllerDocs;
import com.chukchuk.haksa.domain.auth.service.RefreshTokenService;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
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
public class AuthController implements AuthControllerDocs {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<RefreshResponse>> refreshResponse(@RequestBody RefreshRequest request) {
        RefreshResponse response = refreshTokenService.reissue(request.refreshToken());
        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}