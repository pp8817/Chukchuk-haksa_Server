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

    /**
     * Parses the provided JWT token and returns its claims.
     *
     * <p>This method validates the token using the signing key from {@link #getSigningKey()} and extracts
     * the claims for further processing.
     *
     * @param token the JWT token to parse
     * @return the claims extracted from the token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Decodes the Base64-encoded JWT secret and generates a signing key for HMAC SHA-based signature verification.
     *
     * <p>This key is used to validate JWT tokens during parsing by providing the necessary cryptographic
     * material derived from the SUPABASE_JWT_SECRET.</p>
     *
     * @return the signing key generated from the decoded JWT secret
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SUPABASE_JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the user ID (UUID) from the provided JWT token.
     *
     * This method parses the token to retrieve its subject, which is assumed to represent the user's unique identifier.
     *
     * @param token the JWT token to be parsed
     * @return the user ID extracted from the token's subject claim
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject(); // 'sub' -> UID (UUID)
    }

    /**
     * Extracts the email address from the provided JWT token.
     *
     * <p>This method parses the token's claims and retrieves the value associated with the "email" key.
     *
     * @param token the JWT token containing the email claim
     * @return the email address extracted from the token claims, or {@code null} if the claim is absent
     */
    public String getEmailFromToken(String token) {
        return parseToken(token).get("email", String.class);
    }
}
