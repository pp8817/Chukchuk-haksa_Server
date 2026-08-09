package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.user.model.SocialAccount;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.global.security.service.OidcProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
  Optional<SocialAccount> findByProviderAndSocialId(OidcProvider provider, String socialId);

  List<SocialAccount> findAllByUserId(UUID userId);

  void deleteByUser(User user);
}
