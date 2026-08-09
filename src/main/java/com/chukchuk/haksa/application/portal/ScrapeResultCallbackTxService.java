package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJob;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOperationType;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobStatus;
import com.chukchuk.haksa.domain.scrapejob.repository.ScrapeJobRepository;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.EntityNotFoundException;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 스크래핑 결과 콜백 tx 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapeResultCallbackTxService {

  private final ScrapeJobRepository scrapeJobRepository;
  private final PortalSyncService portalSyncService;
  private final MeterRegistry meterRegistry;

  /**
   * 성공 콜백을 검증하고 후처리할 작업 정보를 반환한다.
   *
   * @param jobId 작업 식별자
   * @param attempt attempt 값
   * @param resultS3Key 결과 S3 key 값
   * @param resultChecksum 결과 checksum 값
   * @param callbackMetadataJson 콜백 메타데이터 JSON
   * @param receivedAt 수신 시각
   * @return 콜백 receipt 결과
   */
  @Transactional
  public CallbackReceipt receiveSuccessCallback(
      String jobId,
      int attempt,
      String resultS3Key,
      String resultChecksum,
      String callbackMetadataJson,
      Instant receivedAt) {
    ScrapeJob job = findJobForUpdate(jobId);
    if (job.hasProcessedAttempt(attempt) || job.isCompleted()) {
      return CallbackReceipt.duplicate(job);
    }

    job.markPostProcessing(resultS3Key, resultChecksum, callbackMetadataJson, attempt, receivedAt);
    return CallbackReceipt.accepted(job);
  }

  /**
   * 실패 콜백을 검증하고 작업 실패 정보를 기록한다.
   *
   * @param jobId 작업 식별자
   * @param attempt attempt 값
   * @param callbackMetadataJson 콜백 메타데이터 JSON
   * @param errorCode 오류 코드
   * @param errorMessage 오류 응답 메시지
   * @param retryable retryable 값
   * @param receivedAt 수신 시각
   * @param finishedAt 처리 종료 시각
   * @return 콜백 receipt 결과
   */
  @Transactional
  public CallbackReceipt receiveFailedCallback(
      String jobId,
      int attempt,
      String callbackMetadataJson,
      String errorCode,
      String errorMessage,
      Boolean retryable,
      Instant receivedAt,
      Instant finishedAt) {
    ScrapeJob job = findJobForUpdate(jobId);
    if (job.hasProcessedAttempt(attempt) || job.isCompleted()) {
      return CallbackReceipt.duplicate(job);
    }

    job.recordFailedCallback(
        attempt,
        receivedAt,
        callbackMetadataJson,
        errorCode,
        errorMessage,
        retryable,
        finishedAt,
        Instant.now());
    recordQueuedAge(job, finishedAt);
    return CallbackReceipt.accepted(job);
  }

  /**
   * 콜백 후처리 성공 결과를 작업에 반영한다.
   *
   * @param jobId 작업 식별자
   * @param userId 사용자 식별자
   * @param operationType 작업 유형
   * @param portalData 포털 학사 데이터
   * @param payloadJson JSON payload
   * @param finishedAt 처리 종료 시각
   * @param queuedAgeSeconds queued age seconds 값
   * @param payloadHash payload hash 값
   */
  @Transactional
  public void completeSuccess(
      String jobId,
      UUID userId,
      ScrapeJobOperationType operationType,
      PortalData portalData,
      String payloadJson,
      Instant finishedAt,
      Double queuedAgeSeconds,
      String payloadHash) {
    ScrapeJob job = findJobForUpdate(jobId);
    log.info(
        "[BIZ] scrape.job.callback.postprocess.execute jobId={} currentStatus={}",
        job.getJobId(),
        job.getStatus());

    if (operationType == ScrapeJobOperationType.LINK) {
      portalSyncService.syncWithPortal(userId, portalData);
    } else {
      portalSyncService.refreshFromPortal(userId, portalData);
    }

    Instant resolvedFinishedAt = resolveFinishedAt(finishedAt);
    job.markSucceeded(payloadJson, resolvedFinishedAt, Instant.now());
    recordQueuedAge(job, finishedAt, queuedAgeSeconds);
    log.info(
        "[BIZ] scrape.job.succeeded jobId={} operationType={} payloadHash={} finishedAt={}",
        job.getJobId(),
        job.getOperationType(),
        payloadHash,
        resolvedFinishedAt);
  }

  /**
   * 처리 실패 상태와 원인을 기록한다.
   *
   * @param jobId 작업 식별자
   * @param finishedAt 처리 종료 시각
   * @param queuedAgeSeconds queued age seconds 값
   * @param errorCode 오류 코드
   * @param message 응답 메시지
   * @param retryable retryable 값
   */
  @Transactional
  public void markFailed(
      String jobId,
      Instant finishedAt,
      Double queuedAgeSeconds,
      String errorCode,
      String message,
      Boolean retryable) {
    ScrapeJob job = findJobForUpdate(jobId);
    if (job.isCompleted() || job.getStatus() == ScrapeJobStatus.SUCCEEDED) {
      log.info(
          "[BIZ] scrape.job.callback.fail.skip jobId={} status={} errorCode={}",
          job.getJobId(),
          job.getStatus(),
          errorCode);
      return;
    }
    job.markFailed(errorCode, message, retryable, resolveFinishedAt(finishedAt), Instant.now());
    recordQueuedAge(job, finishedAt, queuedAgeSeconds);
  }

  private ScrapeJob findJobForUpdate(String jobId) {
    return scrapeJobRepository
        .findForUpdateByJobId(jobId)
        .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SCRAPE_JOB_NOT_FOUND));
  }

  private void recordQueuedAge(ScrapeJob job, Instant finishedAt) {
    if (job.getCreatedAt() == null || finishedAt == null) {
      return;
    }
    double queuedAgeSeconds = Duration.between(job.getCreatedAt(), finishedAt).toMillis() / 1000.0;
    meterRegistry.summary("scrape.job.queued.age.seconds").record(queuedAgeSeconds);
  }

  private void recordQueuedAge(ScrapeJob job, Instant finishedAt, Double queuedAgeSeconds) {
    double value;
    if (queuedAgeSeconds != null) {
      value = queuedAgeSeconds;
    } else if (job.getCreatedAt() != null && finishedAt != null) {
      value = Duration.between(job.getCreatedAt(), finishedAt).toMillis() / 1000.0;
    } else {
      return;
    }
    meterRegistry.summary("scrape.job.queued.age.seconds").record(value);
  }

  private static Instant resolveFinishedAt(Instant finishedAt) {
    return finishedAt != null ? finishedAt : Instant.now();
  }

  /**
   * 콜백 receipt 데이터를 전달한다.
   *
   * @param duplicate duplicate 값
   * @param jobId 작업 식별자
   * @param userId 사용자 식별자
   * @param operationType 작업 유형
   * @param status 상태
   * @param queuedAgeSeconds queued age seconds 값
   */
  public record CallbackReceipt(
      boolean duplicate,
      String jobId,
      UUID userId,
      ScrapeJobOperationType operationType,
      String status,
      Double queuedAgeSeconds) {
    /**
     * 처리가 수락된 콜백 응답을 생성한다.
     *
     * @param job 작업 값
     * @return 콜백 receipt 결과
     */
    public static CallbackReceipt accepted(ScrapeJob job) {
      return new CallbackReceipt(
          false,
          job.getJobId(),
          job.getUserId(),
          job.getOperationType(),
          job.getStatus().name().toLowerCase(Locale.ROOT),
          calculateQueuedAgeSeconds(job));
    }

    /**
     * 중복 수신된 콜백 응답을 생성한다.
     *
     * @param job 작업 값
     * @return 콜백 receipt 결과
     */
    public static CallbackReceipt duplicate(ScrapeJob job) {
      return new CallbackReceipt(
          true,
          job.getJobId(),
          job.getUserId(),
          job.getOperationType(),
          job.getStatus().name().toLowerCase(Locale.ROOT),
          calculateQueuedAgeSeconds(job));
    }

    private static Double calculateQueuedAgeSeconds(ScrapeJob job) {
      if (job.getCreatedAt() == null) {
        return null;
      }
      return Duration.between(job.getCreatedAt(), Instant.now()).toMillis() / 1000.0;
    }
  }
}
