package com.chukchuk.haksa.domain.user.dto;

import com.chukchuk.haksa.domain.user.model.User;
import lombok.Builder;

import java.util.UUID;
@Builder
public record TestUserDto(
        UUID id,
        String email,
        String profileNickname,
        String role
) {
    public static TestUserDto from(User user) {
        return TestUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .profileNickname(user.getProfileNickname())
                .role(user.getRole().name())
                .build();
    }
}