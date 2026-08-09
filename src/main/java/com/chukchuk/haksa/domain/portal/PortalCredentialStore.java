package com.chukchuk.haksa.domain.portal;

/** 포털 credential store 기능의 계약을 정의한다. */
public interface PortalCredentialStore {

  /**
   * 척척학사의 save 대상을 저장한다.
   *
   * @param userId 사용자 식별자
   * @param username user이름
   * @param password 포털 비밀번호
   */
  void save(String userId, String username, String password);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  String getUsername(String userId);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  String getPassword(String userId);

  /**
   * 척척학사의 clear 대상을 삭제한다.
   *
   * @param userId 사용자 식별자
   */
  void clear(String userId);
}
