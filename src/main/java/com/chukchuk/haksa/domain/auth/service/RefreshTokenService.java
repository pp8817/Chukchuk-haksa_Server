package com.chukchuk.haksa.domain.auth.service;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.auth.entity.RefreshToken;
import com.chukchuk.haksa.domain.auth.repository.RefreshTokenRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import com.chukchuk.haksa.global.exception.InvalidTokenException;
import com.chukchuk.haksa.global.security.service.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /* Refresh Token 저장 */
    @Transactional
    public void save(String userId, String refreshToken, Date expiry) {
        RefreshToken token = new RefreshToken(userId, refreshToken, expiry);

        refreshTokenRepository.save(token);
    }

    /* 토큰 재발급 */
    @Transactional
    public AuthDto.RefreshResponse reissue(String refreshToken) {
        Claims claims = jwtProvider.parseToken(refreshToken);
        String userId = claims.getSubject();

        RefreshToken saved = findByUserId(userId);
        if (!saved.getToken().equals(refreshToken)) {
            throw new InvalidTokenException("RefreshToken 불일치");
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String newAccessToken = jwtProvider.createAccessToken(userId, user.getEmail(), "USER");
        AuthDto.RefreshTokenWithExpiry newRefresh = jwtProvider.createRefreshToken(userId);
        save(userId, newRefresh.token(), newRefresh.expiry());

        return new AuthDto.RefreshResponse(newAccessToken, newRefresh.token());
    }

    public RefreshToken findByUserId(String userId) {
        return refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("RefreshToken이 존재하지 않음"));
    }
}
