package com.chukchuk.haksa.domain.scrapejob.repository;

import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOutbox;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobOutboxStatus;
import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJobStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 구현체가 제공해야 할 스크래핑 작업 아웃박스 repository 기능의 계약을 정의한다. */
public interface ScrapeJobOutboxRepository extends JpaRepository<ScrapeJobOutbox, String> {

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param jobId 작업 식별자
   * @return 조회
   */
  Optional<ScrapeJobOutbox> findByJobId(String jobId);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param outboxId 아웃박스 식별자
   * @return 조회
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select outbox
            from ScrapeJobOutbox outbox
            where outbox.outboxId = :outboxId
      """)
  Optional<ScrapeJobOutbox> findForUpdateByOutboxId(@Param("outboxId") String outboxId);

  /**
   * 지정한 상태의 아웃박스 수를 반환한다.
   *
   * @param status 상태
   * @return long
   */
  long countByStatus(ScrapeJobOutboxStatus status);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param outboxId 아웃박스 식별자
   * @param statuses statuses 값
   * @param now now 값
   * @return 조회
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select outbox
            from ScrapeJobOutbox outbox
            where outbox.outboxId = :outboxId
              and outbox.status in :statuses
              and outbox.nextAttemptAt <= :now
      """)
  Optional<ScrapeJobOutbox> findPublishTargetForUpdateByOutboxId(
      @Param("outboxId") String outboxId,
      @Param("statuses") Collection<ScrapeJobOutboxStatus> statuses,
      @Param("now") Instant now);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param statuses statuses 값
   * @param now now 값
   * @param pageable pageable 값
   * @return 조회
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select outbox
            from ScrapeJobOutbox outbox
            where outbox.status in :statuses
              and outbox.nextAttemptAt <= :now
            order by outbox.createdAt asc
      """)
  List<ScrapeJobOutbox> findPublishTargetsForUpdate(
      @Param("statuses") Collection<ScrapeJobOutboxStatus> statuses,
      @Param("now") Instant now,
      Pageable pageable);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param outboxStatus 아웃박스 상태
   * @param sentBefore sent before 값
   * @param jobStatus 작업 상태
   * @param pageable pageable 값
   * @return 조회
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
            select outbox
            from ScrapeJobOutbox outbox
            join ScrapeJob job on job.jobId = outbox.jobId
            where outbox.status = :outboxStatus
              and outbox.sentAt <= :sentBefore
              and job.status = :jobStatus
            order by outbox.sentAt asc
      """)
  List<ScrapeJobOutbox> findStaleSentTargetsForUpdate(
      @Param("outboxStatus") ScrapeJobOutboxStatus outboxStatus,
      @Param("sentBefore") Instant sentBefore,
      @Param("jobStatus") ScrapeJobStatus jobStatus,
      Pageable pageable);
}
