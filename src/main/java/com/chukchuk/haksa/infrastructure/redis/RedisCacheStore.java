package com.chukchuk.haksa.infrastructure.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisCacheStore {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper ob;

    // 기본 저장
    public <T> void set(String key, T value, Duration ttl) {
        try {
            String json = ob.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 캐싱 직렬화 실패", e);
        }
    }

    // 조회
    public <T> T get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return ob.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    // 단건 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // prefix로 여러 키 삭제
    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
        }
    }

    // userId 기반 전체 캐시 삭제
    public void deleteAllByUserId(String userId) {
        String prefix = "user:" + userId+":";
        deleteByPrefix(prefix);
    }
}
