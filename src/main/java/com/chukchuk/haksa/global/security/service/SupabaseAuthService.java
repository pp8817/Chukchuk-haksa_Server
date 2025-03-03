package com.chukchuk.haksa.global.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chukchuk.haksa.global.security.SupabaseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupabaseAuthService {

    @Value("${security.jwt.secret}")
    private String SUPABASE_JWT_SECRET;

    public SupabaseUser verifyToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(SUPABASE_JWT_SECRET);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String userId = jwt.getClaim("sub").asString();
            String email = jwt.getClaim("email").asString();

            return new SupabaseUser(userId, email);
        } catch (JWTVerificationException e) {
            return null; // 검증 실패
        }
    }
}
