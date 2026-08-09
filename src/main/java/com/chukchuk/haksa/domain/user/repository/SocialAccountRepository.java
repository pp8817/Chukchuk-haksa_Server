package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.user.model.SocialAccount;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.global.security.service.OidcProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 구현체가 제공해야 할 social 계정 repository 기능의 계약을 정의한다. */
@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param provider provider 값
   * @param socialId social id 식별자
   * @return 조회
   */
  Optional<SocialAccount> findByProviderAndSocialId(OidcProvider provider, String socialId);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  List<SocialAccount> findAllByUserId(UUID userId);

  /**
   * 지정된 데이터를 삭제한다.
   *
   * @param user 사용자 값
   */
  void deleteByUser(User user);
}
