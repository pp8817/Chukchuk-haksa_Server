package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.auth.service.RefreshTokenService;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.dto.UserDto;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import com.chukchuk.haksa.global.security.service.JwtProvider;
import com.chukchuk.haksa.global.security.service.KakaoOidcService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    // TODO: Custom Exception으로 변경
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    public UUID getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Transactional
    public UserDto.SignInResponse signInWithKakao(UserDto.SignInRequest signInRequest) throws Exception {
        // Kakaoㅌ 토큰 검증
        String idToken = signInRequest.id_token();
        String nonce = signInRequest.nonce();
        Claims claims = kakaoOidcService.verifyIdToken(idToken, nonce);

        // 클레임에서 필요한 정보 추출
        String email = claims.get("email", String.class);

        // User 조회 or 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .profileNickname("Unknown User")
                        .build()
                ));

        // token 발급
        String userId = user.getId().toString();
        String accessToken = jwtProvider.createAccessToken(userId, user.getEmail(), "USER");
        AuthDto.RefreshTokenWithExpiry refresh = jwtProvider.createRefreshToken(userId);
        refreshTokenService.save(userId, refresh.token(), refresh.expiry());

        return new UserDto.SignInResponse(HttpStatus.OK, accessToken, refresh.token());
    }
    @Transactional
    public void deleteUserByEmail(String email){
        UUID userId = getUserId(email);
        userRepository.deleteById(userId);
        // User - Student 연관관계를 엮어 자동 삭제 로직을 만들어야 하지만, Student 저장 로직이 별도로 존재하지 않아, 연관관계를 형성하지 못함. 수정하기 전까지 임의 삭제 방식으로 진행
        studentRepository.deleteById(userId);
    }
}
