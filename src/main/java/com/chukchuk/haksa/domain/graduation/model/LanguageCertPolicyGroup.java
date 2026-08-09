// 외국어 인증 기준을 공유하는 정책 그룹 엔티티

package com.chukchuk.haksa.domain.graduation.model;

import static jakarta.persistence.GenerationType.UUID;

import com.chukchuk.haksa.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 척척학사의 language cert policy group 도메인 상태를 표현한다. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "language_cert_policy_groups",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_language_cert_policy_groups_group_key",
            columnNames = "group_key"))
public class LanguageCertPolicyGroup extends BaseEntity {

  @Id
  @GeneratedValue(strategy = UUID)
  private UUID id;

  @Column(name = "group_key", nullable = false)
  private String groupKey;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  private LanguageCertPolicyGroup(String groupKey, String name, String description) {
    this.groupKey = groupKey;
    this.name = name;
    this.description = description;
  }

  /**
   * 척척학사의 create 대상을 생성한다.
   *
   * @param groupKey group key 값
   * @param name 이름
   * @param description description 값
   * @return 생성된
   */
  public static LanguageCertPolicyGroup create(String groupKey, String name, String description) {
    return new LanguageCertPolicyGroup(groupKey, name, description);
  }
}
