package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.portal.dto.PortalLinkDto;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOperationType;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/** 포털 link 작업 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
@Slf4j
public class PortalLinkJobService {

  private final PortalLinkJobTxService portalLinkJobTxService;
  private final ScrapeJobOutboxDispatcher scrapeJobOutboxDispatcher;
  private final UserService userService;
  private final ObjectMapper objectMapper;
  private final PortalLoginVerificationTokenService tokenService;

  /**
   * 포털 연동 작업을 접수하고 비동기 처리 정보를 반환한다.
   *
   * @param userId 사용자 식별자
   * @param idempotencyKey 멱등성 키
   * @param request 요청 정보
   * @return 포털 link dto accepted 응답 결과
   */
  public PortalLinkDto.AcceptedResponse acceptJob(
      UUID userId, String idempotencyKey, PortalLinkDto.LinkRequest request) {
    validateRequest(idempotencyKey, request);
    tokenService.verify(
        userId,
        request.portalType(),
        request.username(),
        request.password(),
        request.portalVerificationToken());

    User user = userService.getUserById(userId);
    ScrapeJobOperationType operationType =
        Boolean.TRUE.equals(user.getPortalConnected())
            ? ScrapeJobOperationType.REFRESH
            : ScrapeJobOperationType.LINK;

    String requestFingerprint =
        createRequestFingerprint(
            request.portalType(), request.username(), request.password(), operationType);
    String requestPayloadJson = toRequestPayloadJson(request.username(), request.password());
    Instant requestedAt = Instant.now();

    try {
      PortalLinkJobTxService.PreparedJob preparedJob =
          portalLinkJobTxService.createOrLoadJob(
              userId,
              idempotencyKey,
              request.portalType(),
              operationType,
              requestFingerprint,
              requestPayloadJson,
              request.username(),
              request.password(),
              requestedAt);
      return finalizeAcceptedJob(preparedJob, userId, request, operationType, idempotencyKey);
    } catch (DataIntegrityViolationException exception) {
      PortalLinkJobTxService.PreparedJob preparedJob =
          portalLinkJobTxService.loadExistingJob(userId, idempotencyKey, requestFingerprint);
      return finalizeAcceptedJob(preparedJob, userId, request, operationType, idempotencyKey);
    } catch (CommonException exception) {
      throw exception;
    } catch (RuntimeException exception) {
      log.warn(
          "[BIZ] scrape.job.enqueue.fail userId={} portalType={} idempotencyKey={}",
          userId,
          request.portalType(),
          idempotencyKey,
          exception);
      throw new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED, exception);
    }
  }

  /**
   * 척척학사의 create 요청 fingerprint 대상을 생성한다.
   *
   * @param portalType 포털 유형
   * @param username user이름
   * @param password 포털 비밀번호
   * @param operationType 작업 유형
   * @return 생성된
   */
  public static String createRequestFingerprint(
      String portalType, String username, String password, ScrapeJobOperationType operationType) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String canonical =
          String.join("|", normalize(portalType), username.trim(), password, operationType.name());
      return HexFormat.of().formatHex(digest.digest(canonical.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to create request fingerprint", exception);
    }
  }

  private static String normalize(String portalType) {
    return portalType == null ? "" : portalType.trim().toLowerCase();
  }

  private void validateRequest(String idempotencyKey, PortalLinkDto.LinkRequest request) {
    if (idempotencyKey == null || idempotencyKey.isBlank()) {
      throw new CommonException(ErrorCode.INVALID_ARGUMENT);
    }
    if (request.username() == null
        || request.username().isBlank()
        || request.password() == null
        || request.password().isBlank()) {
      throw new CommonException(ErrorCode.INVALID_ARGUMENT);
    }
    if (request.portalVerificationToken() == null || request.portalVerificationToken().isBlank()) {
      throw new CommonException(ErrorCode.INVALID_ARGUMENT);
    }
    if (!"suwon".equals(normalize(request.portalType()))) {
      throw new CommonException(ErrorCode.UNSUPPORTED_PORTAL_TYPE);
    }
  }

  private String toRequestPayloadJson(String username, String password) {
    try {
      return objectMapper.writeValueAsString(
          new ScrapeJobMessage.RequestPayload(username, password));
    } catch (JsonProcessingException exception) {
      throw new CommonException(ErrorCode.INVALID_ARGUMENT, exception);
    }
  }

  private PortalLinkDto.AcceptedResponse finalizeAcceptedJob(
      PortalLinkJobTxService.PreparedJob preparedJob,
      UUID userId,
      PortalLinkDto.LinkRequest request,
      ScrapeJobOperationType operationType,
      String idempotencyKey) {
    if (preparedJob.dispatchRequired()) {
      dispatchSynchronously(preparedJob, request.portalType(), idempotencyKey);
    }

    if (preparedJob.reused()) {
      log.info(
          "[BIZ] scrape.job.idempotent.reuse jobId={} userId={} portalType={} "
              + "operationType={} idempotencyKey={}",
          preparedJob.jobId(),
          userId,
          request.portalType(),
          operationType,
          idempotencyKey);
    } else {
      log.info(
          "[BIZ] scrape.job.accepted jobId={} userId={} portalType={} "
              + "operationType={} idempotencyKey={}",
          preparedJob.jobId(),
          userId,
          request.portalType(),
          operationType,
          idempotencyKey);
    }
    return new PortalLinkDto.AcceptedResponse(
        preparedJob.jobId(), "accepted", "/portal/link/jobs/" + preparedJob.jobId());
  }

  private void dispatchSynchronously(
      PortalLinkJobTxService.PreparedJob preparedJob, String portalType, String idempotencyKey) {
    try {
      scrapeJobOutboxDispatcher.dispatchOnce(preparedJob.outboxId());
      PortalLinkJobTxService.DispatchSnapshot snapshot =
          portalLinkJobTxService.loadDispatchSnapshot(preparedJob.outboxId());
      if (!snapshot.isSent()) {
        log.warn(
            "[BIZ] scrape.job.enqueue.sync.fail jobId={} outboxId={} portalType={} "
                + "idempotencyKey={} jobStatus={} outboxStatus={} queueMessageId={} "
                + "lastError={}",
            snapshot.jobId(),
            snapshot.outboxId(),
            portalType,
            idempotencyKey,
            snapshot.jobStatus(),
            snapshot.outboxStatus(),
            snapshot.queueMessageId(),
            snapshot.lastError());
        throw new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED);
      }
    } catch (CommonException exception) {
      throw exception;
    } catch (RuntimeException exception) {
      log.warn(
          "[BIZ] scrape.job.enqueue.sync.exception jobId={} outboxId={} "
              + "portalType={} idempotencyKey={}",
          preparedJob.jobId(),
          preparedJob.outboxId(),
          portalType,
          idempotencyKey,
          exception);
      throw new CommonException(ErrorCode.SCRAPE_JOB_ENQUEUE_FAILED, exception);
    }
  }
}
