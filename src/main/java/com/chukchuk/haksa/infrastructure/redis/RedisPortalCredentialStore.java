package com.chukchuk.haksa.infrastructure.redis;

import com.chukchuk.haksa.global.crypto.AesEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisPortalCredentialStore {

    private final RedisTemplate<String, String> redisTemplate;
    private final AesEncryptor aesEncryptor;

    private static final Duration TTL = Duration.ofMinutes(10);

    public void save(String userId, String username, String password) {
        String prefix = "portal:" + userId + ":";

        try {
            redisTemplate.opsForValue().set(prefix + "username", aesEncryptor.encrypt(username), TTL);
            redisTemplate.opsForValue().set(prefix + "password", aesEncryptor.encrypt(password), TTL);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    public String getUsername(String userId) {
        try {
            String encrypted = redisTemplate.opsForValue().get("portal:" + userId + ":username");
            return encrypted != null ? aesEncryptor.decrypt(encrypted) : null;
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }

    public String getPassword(String userId) {
        try {
            String encrypted = redisTemplate.opsForValue().get("portal:" + userId + ":password");
            return encrypted != null ? aesEncryptor.decrypt(encrypted) : null;
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }

    public void clear(String userId) {
        redisTemplate.delete("portal:" + userId + ":username");
        redisTemplate.delete("portal:" + userId + ":password");
    }
}