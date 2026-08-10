// 외국어 인증 정책 그룹을 조회하고 저장하는 Repository

package com.chukchuk.haksa.domain.graduation.repository;

import com.chukchuk.haksa.domain.graduation.model.LanguageCertPolicyGroup;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** 학과·전공·입학 연도에 적용할 외국어 인증 정책 그룹을 조회한다. */
public interface LanguageCertPolicyGroupRepository
    extends JpaRepository<LanguageCertPolicyGroup, UUID> {
  /**
   * 졸업 요건를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param groupKey group key
   * @return 조건에 일치하는 졸업 요건가 있으면 포함한 선택값
   */
  Optional<LanguageCertPolicyGroup> findByGroupKey(String groupKey);
}
