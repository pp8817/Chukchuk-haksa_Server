package com.chukchuk.haksa.domain.user.model;

import com.chukchuk.haksa.global.security.service.OidcProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 척척학사의 social 계정 도메인 상태를 표현한다. */
@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_provider_social_id",
          columnNames = {"provider", "social_id"})
    })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "provider", nullable = false)
  @Enumerated(EnumType.STRING)
  private OidcProvider provider;

  @Column(name = "social_id", nullable = false, length = 255)
  private String socialId;

  @Column(name = "email")
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param provider provider 값
   * @param socialId social id 식별자
   * @param email 이메일 값
   * @param user 사용자 값
   */
  @Builder
  public SocialAccount(OidcProvider provider, String socialId, String email, User user) {
    this.provider = provider;
    this.socialId = socialId;
    this.email = email;
    this.user = user;
  }

  /**
   * 현재 상태를 요청 내용에 맞게 갱신한다.
   *
   * @param user 사용자 값
   */
  public void updateUser(User user) {
    this.user = user;
  }
}
