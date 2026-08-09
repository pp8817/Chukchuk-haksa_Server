package com.chukchuk.haksa.infrastructure.portal.model;

import org.springframework.lang.Nullable;

/** 척척학사의 merged offering 학사 도메인 상태를 표현한다. */
public class MergedOfferingAcademic {
  private final PortalOfferingCreationData offering;
  private final @Nullable PortalCourseInfo academic;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param offering offering 값
   * @param academic 학사 값
   */
  public MergedOfferingAcademic(PortalOfferingCreationData offering, PortalCourseInfo academic) {
    this.offering = offering;
    this.academic = academic;
  }

  public PortalOfferingCreationData getOffering() {
    return offering;
  }

  public PortalCourseInfo getAcademic() {
    return academic;
  }
}
