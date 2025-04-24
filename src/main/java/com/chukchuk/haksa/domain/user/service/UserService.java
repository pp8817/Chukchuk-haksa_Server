package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.auth.service.RefreshTokenService;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import com.chukchuk.haksa.global.security.service.JwtProvider;
import com.chukchuk.haksa.global.security.service.KakaoOidcService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final KakaoOidcService kakaoOidcService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public AuthDto.SignInTokenResponse signInWithKakao(UserDto.SignInRequest signInRequest) {
        Claims claims = verifyKakaoToken(signInRequest);
        String email = extractEmail(claims);

        User user = findOrCreateUser(email);
        return generateSignInResponse(user);
    }

    @Transactional
    public void deleteUserByEmail(UUID userId){
        userRepository.deleteById(userId);
        // User - Student 연관관계를 엮어 자동 삭제 로직을 만들어야 하지만, Student 저장 로직이 별도로 존재하지 않아, 연관관계를 형성하지 못함. 수정하기 전까지 임의 삭제 방식으로 진행
        studentRepository.deleteById(userId);
    }

    /* private method */
    private Claims verifyKakaoToken(UserDto.SignInRequest request) {
        return kakaoOidcService.verifyIdToken(request.id_token(), request.nonce());
    }

    private String extractEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    private User findOrCreateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .profileNickname("Unknown User")
                                .build()
                ));
    }

    private AuthDto.SignInTokenResponse generateSignInResponse(User user) {
        String userId = user.getId().toString();
        String accessToken = jwtProvider.createAccessToken(userId, user.getEmail(), "USER");
        AuthDto.RefreshTokenWithExpiry refresh = jwtProvider.createRefreshToken(userId);
        refreshTokenService.save(userId, refresh.token(), refresh.expiry());

        return new AuthDto.SignInTokenResponse(accessToken, refresh.token(), user.getPortalConnected());
    }
}
