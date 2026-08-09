package com.chukchuk.haksa.application.maintenance;

/**
 * 유지보수 task 요청 데이터를 전달한다.
 *
 * @param source source 값
 * @param task task 값
 * @param scheduledAt 예약 시각
 */
public record MaintenanceTaskRequest(String source, String task, String scheduledAt) {}
