// 외국어 인증 기준 학과 매핑의 신뢰 상태를 정의하는 enum

package com.chukchuk.haksa.domain.graduation.model;

/** 척척학사의 language cert match status에서 사용할 값을 정의한다. */
public enum LanguageCertMatchStatus {
  VERIFIED,
  INFERRED,
  UNMAPPED
}
