package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // TODO: Custom Exception으로 변경

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    public UUID getUserId(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }
}
