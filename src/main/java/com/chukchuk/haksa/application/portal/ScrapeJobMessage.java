package com.chukchuk.haksa.application.portal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 스크래핑 작업 메시지 데이터를 전달한다.
 *
 * @param jobId 작업 식별자
 * @param userId 사용자 식별자
 * @param portalType 포털 유형
 * @param requestPayload 요청 payload 정보
 * @param requestedAt requested at 정보
 */
public record ScrapeJobMessage(
    @JsonProperty("job_id") String jobId,
    @JsonProperty("user_id") String userId,
    @JsonProperty("portal_type") String portalType,
    @JsonProperty("request_payload") RequestPayload requestPayload,
    @JsonProperty("requested_at") String requestedAt) {

  /**
   * 요청 payload 데이터를 전달한다.
   *
   * @param username user이름
   * @param password 포털 비밀번호
   */
  public record RequestPayload(String username, String password) {}
}
