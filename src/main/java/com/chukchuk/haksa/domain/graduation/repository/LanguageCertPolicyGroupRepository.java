// 외국어 인증 정책 그룹을 조회하고 저장하는 Repository

package com.chukchuk.haksa.domain.graduation.repository;

import com.chukchuk.haksa.domain.graduation.model.LanguageCertPolicyGroup;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** 척척학사의 language cert policy group repository 기능의 계약을 정의한다. */
public interface LanguageCertPolicyGroupRepository
    extends JpaRepository<LanguageCertPolicyGroup, UUID> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param groupKey group key 값
   * @return 조회
   */
  Optional<LanguageCertPolicyGroup> findByGroupKey(String groupKey);
}
