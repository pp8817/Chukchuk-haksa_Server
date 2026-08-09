package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOperationType;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOutboxStatus;
import java.util.UUID;

/**
 * 스크래핑 작업 아웃박스 publish candidate 데이터를 전달한다.
 *
 * @param outboxId 아웃박스 식별자
 * @param jobId 작업 식별자
 * @param userId 사용자 식별자
 * @param operationType 작업 유형
 * @param payloadJson JSON payload
 * @param attemptCount attempt count 값
 * @param status 상태
 * @param queueMessageId 큐 메시지 식별자
 */
public record ScrapeJobOutboxPublishCandidate(
    String outboxId,
    String jobId,
    UUID userId,
    ScrapeJobOperationType operationType,
    String payloadJson,
    int attemptCount,
    ScrapeJobOutboxStatus status,
    String queueMessageId) {}
