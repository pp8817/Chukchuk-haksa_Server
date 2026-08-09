package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJob;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOperationType;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOutbox;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOutboxStatus;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobStatus;
import com.chukchuk.haksa.domain.scrapejob.repository.ScrapeJobOutboxRepository;
import com.chukchuk.haksa.domain.scrapejob.repository.ScrapeJobRepository;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 포털 link 작업 tx 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
public class PortalLinkJobTxService {

  private final ScrapeJobRepository scrapeJobRepository;
  private final ScrapeJobOutboxRepository scrapeJobOutboxRepository;
  private final ObjectMapper objectMapper;

  /**
   * 척척학사의 create or load 작업 대상을 생성한다.
   *
   * @param userId 사용자 식별자
   * @param idempotencyKey 멱등성 키
   * @param portalType 포털 유형
   * @param operationType 작업 유형
   * @param requestFingerprint 요청 fingerprint 정보
   * @param requestPayloadJson 요청 payload json 정보
   * @param username user이름
   * @param password 포털 비밀번호
   * @param requestedAt requested at 정보
   * @return 생성된
   */
  @Transactional
  public PreparedJob createOrLoadJob(
      UUID userId,
      String idempotencyKey,
      String portalType,
      ScrapeJobOperationType operationType,
      String requestFingerprint,
      String requestPayloadJson,
      String username,
      String password,
      Instant requestedAt) {
    return scrapeJobRepository
        .findByUserIdAndIdempotencyKey(userId, idempotencyKey)
        .map(existingJob -> toPreparedJob(existingJob, requestFingerprint))
        .orElseGet(
            () ->
                createNewJob(
                    userId,
                    idempotencyKey,
                    portalType,
                    operationType,
                    requestFingerprint,
                    requestPayloadJson,
                    username,
                    password,
                    requestedAt));
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @param idempotencyKey 멱등성 키
   * @param requestFingerprint 요청 fingerprint 정보
   * @return 조회
   */
  @Transactional(readOnly = true)
  public PreparedJob loadExistingJob(
      UUID userId, String idempotencyKey, String requestFingerprint) {
    ScrapeJob existingJob =
        scrapeJobRepository
            .findByUserIdAndIdempotencyKey(userId, idempotencyKey)
            .orElseThrow(() -> new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED));
    return toPreparedJob(existingJob, requestFingerprint);
  }

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param outboxId 아웃박스 식별자
   * @return 조회
   */
  @Transactional(readOnly = true)
  public DispatchSnapshot loadDispatchSnapshot(String outboxId) {
    ScrapeJobOutbox outbox =
        scrapeJobOutboxRepository
            .findById(outboxId)
            .orElseThrow(() -> new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED));
    ScrapeJob job =
        scrapeJobRepository
            .findById(outbox.getJobId())
            .orElseThrow(() -> new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED));
    return new DispatchSnapshot(
        job.getJobId(),
        outbox.getOutboxId(),
        job.getStatus(),
        outbox.getStatus(),
        outbox.getQueueMessageId(),
        outbox.getLastError());
  }

  private PreparedJob createNewJob(
      UUID userId,
      String idempotencyKey,
      String portalType,
      ScrapeJobOperationType operationType,
      String requestFingerprint,
      String requestPayloadJson,
      String username,
      String password,
      Instant requestedAt) {
    ScrapeJob savedJob =
        scrapeJobRepository.save(
            ScrapeJob.createQueued(
                userId,
                portalType,
                operationType,
                idempotencyKey,
                requestFingerprint,
                requestPayloadJson,
                requestedAt));
    ScrapeJobOutbox savedOutbox =
        scrapeJobOutboxRepository.save(
            ScrapeJobOutbox.createPending(
                savedJob.getJobId(),
                buildOutboxPayload(
                    savedJob.getJobId(), userId, portalType, username, password, requestedAt),
                requestedAt));
    return PreparedJob.created(savedJob, savedOutbox);
  }

  private String buildOutboxPayload(
      String jobId,
      UUID userId,
      String portalType,
      String username,
      String password,
      Instant requestedAt) {
    try {
      return objectMapper.writeValueAsString(
          new ScrapeJobMessage(
              jobId,
              userId.toString(),
              portalType,
              new ScrapeJobMessage.RequestPayload(username, password),
              requestedAt.toString()));
    } catch (JsonProcessingException exception) {
      throw new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED, exception);
    }
  }

  private PreparedJob toPreparedJob(ScrapeJob existingJob, String requestFingerprint) {
    if (!existingJob.hasSameFingerprint(requestFingerprint)) {
      throw new CommonException(ErrorCode.IDEMPOTENCY_KEY_CONFLICT);
    }

    ScrapeJobOutbox outbox =
        scrapeJobOutboxRepository
            .findByJobId(existingJob.getJobId())
            .orElseThrow(() -> new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED));
    if (existingJob.getStatus() == ScrapeJobStatus.QUEUED
        && outbox.getStatus() == ScrapeJobOutboxStatus.DEAD) {
      throw new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED);
    }
    return PreparedJob.loaded(existingJob, outbox, requiresDispatch(existingJob, outbox));
  }

  private boolean requiresDispatch(ScrapeJob job, ScrapeJobOutbox outbox) {
    return job.getStatus() == ScrapeJobStatus.QUEUED
        && (outbox.getStatus() == ScrapeJobOutboxStatus.PENDING
            || outbox.getStatus() == ScrapeJobOutboxStatus.RETRYABLE_FAILED);
  }

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param jobId 작업 식별자
   * @param outboxId 아웃박스 식별자
   * @param reused reused 값
   * @param dispatchRequired dispatch required 값
   */
  public record PreparedJob(
      String jobId, String outboxId, boolean reused, boolean dispatchRequired) {
    static PreparedJob created(ScrapeJob job, ScrapeJobOutbox outbox) {
      return new PreparedJob(job.getJobId(), outbox.getOutboxId(), false, true);
    }

    static PreparedJob loaded(ScrapeJob job, ScrapeJobOutbox outbox, boolean dispatchRequired) {
      return new PreparedJob(job.getJobId(), outbox.getOutboxId(), true, dispatchRequired);
    }
  }

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param jobId 작업 식별자
   * @param outboxId 아웃박스 식별자
   * @param jobStatus 작업 상태
   * @param outboxStatus 아웃박스 상태
   * @param queueMessageId 큐 메시지 식별자
   * @param lastError last 오류 값
   */
  public record DispatchSnapshot(
      String jobId,
      String outboxId,
      ScrapeJobStatus jobStatus,
      ScrapeJobOutboxStatus outboxStatus,
      String queueMessageId,
      String lastError) {
    public boolean isSent() {
      return outboxStatus == ScrapeJobOutboxStatus.SENT && jobStatus == ScrapeJobStatus.RUNNING;
    }
  }
}
