package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
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
    public void signInWithKakao(String idToken, String nonce) throws Exception {
        // Kakao 토큰 검증
        Claims claims = kakaoOidcService.verifyIdToken(idToken, nonce);

        // 클레임에서 필요한 정보 추출
        String email = claims.get("email", String.class);

        // 회원 가입, 로그인 처리
        // User 조회
        userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .profileNickname("Unknown User")
                        .build()
                ));

        log.info("회원가입 성공");
    }
    @Transactional
    public void deleteUserByEmail(String email){
        UUID userId = getUserId(email);
        userRepository.deleteById(userId);
        // User - Student 연관관계를 엮어 자동 삭제 로직을 만들어야 하지만, Student 저장 로직이 별도로 존재하지 않아, 연관관계를 형성하지 못함. 수정하기 전까지 임의 삭제 방식으로 진행
        studentRepository.deleteById(userId);
    }
}
