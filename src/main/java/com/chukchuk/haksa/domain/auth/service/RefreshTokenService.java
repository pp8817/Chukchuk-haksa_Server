package com.chukchuk.haksa.domain.auth.service;

import com.chukchuk.haksa.domain.auth.dto.AuthDto;
import com.chukchuk.haksa.domain.auth.entity.RefreshToken;
import com.chukchuk.haksa.domain.auth.repository.RefreshTokenRepository;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserRepository;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import com.chukchuk.haksa.global.exception.TokenException;
import com.chukchuk.haksa.global.security.service.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
            throw new TokenException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtProvider.createAccessToken(userId, user.getEmail(), "USER");
        AuthDto.RefreshTokenWithExpiry newRefresh = jwtProvider.createRefreshToken(userId);
        save(userId, newRefresh.token(), newRefresh.expiry());

        return new AuthDto.RefreshResponse(newAccessToken, newRefresh.token());
    }

    public RefreshToken findByUserId(String userId) {
        return refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new TokenException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
    }

    /* 일정 시간마다 유효기간이 지난 RefreshToken 정보 삭제 */
    @Transactional
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul") // 정각 실행
    public void deletedExpiredTokens() {
        try {
            Date now = new Date();
            int deleted = refreshTokenRepository.deleteByExpiryBefore(now);
            log.info("[RefreshToken 정리] {}개 삭제", deleted);
        } catch (Exception e) {
            log.error("[RefreshToken 정리 실패]", e);
        }
    }
}
