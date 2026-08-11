// 포털 로그인 검증을 외부 구현과 분리하는 인터페이스

package com.chukchuk.haksa.application.portal;

/** 포털 종류별 자격 증명을 사용해 실제 로그인 가능 여부를 검증한다. */
public interface PortalLoginVerifier {

  /**
   * 지정한 포털에 로그인해 계정 자격 증명이 유효한지 확인한다.
   *
   * @param portalType 포털 유형
   * @param username 포털 로그인 아이디
   * @param password 포털 비밀번호
   */
  void verify(String portalType, String username, String password);
}
