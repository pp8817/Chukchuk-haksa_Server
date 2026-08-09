package com.chukchuk.haksa.global.security.service;

import static com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshTokenWithExpiry;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 척척학사의 jwt 필요한 값을 조회해 제공한다. */
@Component
@Slf4j
public class JwtProvider {

  @Value("${security.jwt.secret}")
  private String secret;

  @Value("${security.jwt.access-expiration}")
  private long accessTokenExpiration; // ms 단위

  @Value("${security.jwt.refresh-expiration}")
  private long refreshTokenExpiration; // ms 단위

  private Key key;

  /** JWT 서명 키를 초기화하고 유효성을 검사한다. */
  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes()); // base64 아님!
  }

  // AccessToken 토큰 생성
  /**
   * 입력 값을 사용해 결과 객체를 생성한다.
   *
   * @param userId 사용자 식별자
   * @param email 이메일 값
   * @param role role 값
   * @return 생성된
   */
  public String createAccessToken(String userId, String email, String role) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .setSubject(userId)
        .claim("email", email)
        .claim("role", role)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // RefreshToken 생성
  /**
   * 입력 값을 사용해 결과 객체를 생성한다.
   *
   * @param userId 사용자 식별자
   * @return 생성된
   */
  public RefreshTokenWithExpiry createRefreshToken(String userId) {
    return createRefreshToken(userId, UUID.randomUUID().toString());
  }

  // RefreshToken 생성
  /**
   * 입력 값을 사용해 결과 객체를 생성한다.
   *
   * @param userId 사용자 식별자
   * @param sessionId 세션 식별자
   * @return 생성된
   */
  public RefreshTokenWithExpiry createRefreshToken(String userId, String sessionId) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshTokenExpiration);

    String token =
        Jwts.builder()
            .setSubject(userId)
            .claim("sid", sessionId)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

    return new RefreshTokenWithExpiry(token, expiry, sessionId);
  }

  // 토큰 검증
  /**
   * 입력 데이터를 필요한 형식으로 변환한다.
   *
   * @param token 토큰 값
   * @return 변환된
   */
  public Claims parseToken(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .setAllowedClockSkewSeconds(60)
          .build()
          .parseClaimsJws(token) // 서명 유효성 검증 + Base64 디코딩, Claims 추출
          .getBody();
    } catch (ExpiredJwtException e) {
      throw e;
    } catch (Exception e) {
      log.info("JWT parsing error: {}", e.getMessage(), e);
      throw new JwtException("Invalid token", e);
    }
  }
}
