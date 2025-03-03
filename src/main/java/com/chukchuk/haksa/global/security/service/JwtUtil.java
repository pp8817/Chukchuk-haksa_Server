package com.chukchuk.haksa.global.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret}")
    private String SUPABASE_JWT_SECRET;

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SUPABASE_JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // UID(UUID) 추출
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject(); // 'sub' -> UID (UUID)
    }

    // Email 추출
    public String getEmailFromToken(String token) {
        return parseToken(token).get("email", String.class);
    }
}
