package com.chukchuk.haksa.domain.user.service;

import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

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

    @Transactional
    public void deleteUserByEmail(String email){
        UUID userId = getUserId(email);
        userRepository.deleteById(userId);
        // User - Student 연관관계를 엮어 자동 삭제 로직을 만들어야 하지만, Student 저장 로직이 별도로 존재하지 않아, 연관관계를 형성하지 못함. 수정하기 전까지 임의 삭제 방식으로 진행
        studentRepository.deleteById(userId);
    }
}
