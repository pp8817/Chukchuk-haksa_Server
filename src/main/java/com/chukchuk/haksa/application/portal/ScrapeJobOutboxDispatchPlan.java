package com.chukchuk.haksa.application.portal;

import java.util.List;

/**
 * 스크래핑 작업 아웃박스 dispatch plan 데이터를 전달한다.
 *
 * @param candidates candidates 여부
 * @param dispatchedCount dispatched count 값
 */
public record ScrapeJobOutboxDispatchPlan(
    List<ScrapeJobOutboxPublishCandidate> candidates, int dispatchedCount) {

  /**
   * 발행 대상이 없는 빈 계획을 생성한다.
   *
   * @return 스크래핑 작업 아웃박스 dispatch plan 결과
   */
  public static ScrapeJobOutboxDispatchPlan empty() {
    return new ScrapeJobOutboxDispatchPlan(List.of(), 0);
  }
}
