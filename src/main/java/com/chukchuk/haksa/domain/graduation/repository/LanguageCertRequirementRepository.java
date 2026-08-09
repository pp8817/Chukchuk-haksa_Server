// 외국어 인증 정책 그룹별 시험 기준을 조회하는 Repository

package com.chukchuk.haksa.domain.graduation.repository;

import com.chukchuk.haksa.domain.graduation.model.LanguageCertPolicyGroup;
import com.chukchuk.haksa.domain.graduation.model.LanguageCertRequirement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** 척척학사의 language cert 요건 repository 기능의 계약을 정의한다. */
public interface LanguageCertRequirementRepository
    extends JpaRepository<LanguageCertRequirement, UUID> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param policyGroup 정책 그룹
   * @return 조회
   */
  List<LanguageCertRequirement> findAllByPolicyGroupOrderBySortOrderAsc(
      LanguageCertPolicyGroup policyGroup);
}
