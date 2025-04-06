package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestUserService {

    private final UserRepository userRepository;

    public void createTestUser() {
        userRepository.findByEmail("test@example.com")
                .orElseGet(() -> {
                    User user = User.builder()
                            .email("test@example.com")
                            .profileNickname("테스트 계정")
                            .role(User.Role.TEST)
                            .build();
                    return userRepository.save(user);
                });
    }

    public User getTestUser() {
        return userRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트 계정이 존재하지 않습니다."));
    }
}
