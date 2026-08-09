package com.chukchuk.haksa.domain.auth.repository;

import com.chukchuk.haksa.domain.auth.entity.RefreshToken;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  int deleteByExpiryBefore(Date now);

  int deleteByUserId(String userId);
}
