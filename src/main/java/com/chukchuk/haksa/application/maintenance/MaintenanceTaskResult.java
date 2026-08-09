package com.chukchuk.haksa.application.maintenance;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 유지보수 task 결과 데이터를 전달한다.
 *
 * @param success success 값
 * @param task task 값
 * @param affectedCount affected count 값
 * @param scheduledAt 예약 시각
 */
public record MaintenanceTaskResult(
    boolean success,
    String task,
    @JsonProperty("affected_count") int affectedCount,
    @JsonProperty("scheduled_at") String scheduledAt) {

  /**
   * 성공 결과를 생성한다.
   *
   * @param task task 값
   * @param affectedCount affected count 값
   * @param scheduledAt 예약 시각
   * @return 유지보수 task 결과
   */
  public static MaintenanceTaskResult success(String task, int affectedCount, String scheduledAt) {
    return new MaintenanceTaskResult(true, task, affectedCount, scheduledAt);
  }
}
