package com.chukchuk.haksa.domain.scrapejob.repository;

import com.chukchuk.haksa.domain.scrapejob.model.ScrapeJob;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 구현체가 제공해야 할 스크래핑 작업 repository 기능의 계약을 정의한다. */
public interface ScrapeJobRepository extends JpaRepository<ScrapeJob, String> {

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @param idempotencyKey 멱등성 키
   * @return 조회
   */
  Optional<ScrapeJob> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param jobId 작업 식별자
   * @param userId 사용자 식별자
   * @return 조회
   */
  Optional<ScrapeJob> findByJobIdAndUserId(String jobId, UUID userId);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param jobId 작업 식별자
   * @return 조회
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select job from ScrapeJob job where job.jobId = :jobId")
  Optional<ScrapeJob> findForUpdateByJobId(@Param("jobId") String jobId);
}
