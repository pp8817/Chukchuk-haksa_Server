package com.chukchuk.haksa.domain.auth.repository;

import com.chukchuk.haksa.domain.auth.entity.RefreshToken;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 척척학사의 refresh 토큰 repository 기능의 계약을 정의한다. */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  /**
   * 척척학사의 delete by expiry before 대상을 삭제한다.
   *
   * @param now now 값
   * @return int
   */
  int deleteByExpiryBefore(Date now);

  int deleteByUserId(String userId);
}
