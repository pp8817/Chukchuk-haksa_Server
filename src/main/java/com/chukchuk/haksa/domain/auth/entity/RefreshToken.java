package com.chukchuk.haksa.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;

/** 세션별 리프레시 토큰 해시와 만료 시각을 저장한다. */
@Entity
@Getter
@Table(name = "refresh_token")
public class RefreshToken {

  @Id
  @Column(name = "session_id", nullable = false)
  private String sessionId;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column private String token;

  @Column(name = "token_hash")
  private String tokenHash;

  @Column(nullable = false)
  private Date expiry;

  /** JPA에서 사용할 기본 인스턴스를 생성한다. */
  public RefreshToken() {}

  /**
   * 세션·사용자·토큰 값과 만료 시각을 포함한 리프레시 토큰 엔티티를 생성한다.
   *
   * @param sessionId 세션 식별자
   * @param userId 사용자 식별자
   * @param token 저장하거나 해시할 리프레시 토큰 원문
   * @param expiry 만료 시각
   */
  public RefreshToken(String sessionId, String userId, String token, Date expiry) {
    this(sessionId, userId, token, null, expiry);
  }

  /**
   * 세션·사용자·토큰 값과 만료 시각을 포함한 리프레시 토큰 엔티티를 생성한다.
   *
   * @param sessionId 세션 식별자
   * @param userId 사용자 식별자
   * @param token 저장하거나 해시할 리프레시 토큰 원문
   * @param tokenHash 원문 토큰 검증에 사용할 저장된 해시
   * @param expiry 만료 시각
   */
  public RefreshToken(
      String sessionId, String userId, String token, String tokenHash, Date expiry) {
    this.sessionId = sessionId;
    this.userId = userId;
    this.token = token;
    this.tokenHash = tokenHash;
    this.expiry = expiry;
  }

  /**
   * 현재 상태가 조건을 충족하는지 반환한다.
   *
   * @return 조건 충족 여부
   */
  public boolean hasTokenHash() {
    return tokenHash != null && !tokenHash.isBlank();
  }
}
