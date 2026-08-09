package com.chukchuk.haksa.domain.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/** 포털 link 계층 간 데이터를 전달한다. */
public class PortalLinkDto {

  /**
   * 로그인 요청 데이터를 전달한다.
   *
   * @param portalType 포털 유형
   * @param username user이름
   * @param password 포털 비밀번호
   */
  @Schema(description = "포털 로그인 검증 요청")
  public record LoginRequest(
      @NotBlank @JsonProperty("portal_type") @Schema(description = "포털 타입", example = "suwon")
          String portalType,
      @NotBlank @Schema(description = "포털 아이디", example = "17019013") String username,
      @NotBlank @Schema(description = "포털 비밀번호", example = "pw") String password) {}

  /**
   * 로그인 응답 데이터를 전달한다.
   *
   * @param portalVerificationToken 포털 검증 토큰 값
   */
  @Schema(description = "포털 로그인 검증 응답")
  public record LoginResponse(
      @JsonProperty("portal_verification_token")
          @Schema(description = "포털 로그인 검증 token", example = "token")
          String portalVerificationToken) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param portalType 포털 유형
   * @param username user이름
   * @param password 포털 비밀번호
   * @param portalVerificationToken 포털 검증 토큰 값
   */
  @Schema(description = "포털 연동 job 생성 요청")
  public record LinkRequest(
      @NotBlank @JsonProperty("portal_type") @Schema(description = "포털 타입", example = "suwon")
          String portalType,
      @NotBlank @Schema(description = "포털 아이디", example = "17019013") String username,
      @NotBlank @Schema(description = "포털 비밀번호", example = "pw") String password,
      @NotBlank
          @JsonProperty("portal_verification_token")
          @Schema(description = "포털 로그인 검증 token", example = "token")
          String portalVerificationToken) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param pollingEndpoint polling endpoint 값
   */
  @Schema(description = "스크래핑 job 수락 응답")
  public record AcceptedResponse(
      @JsonProperty("job_id") @Schema(description = "job id", example = "job-123") String jobId,
      @Schema(description = "수락 상태", example = "accepted") String status,
      @JsonProperty("polling_endpoint")
          @Schema(description = "상태 조회 경로", example = "/portal/link/jobs/job-123")
          String pollingEndpoint) {}

  /**
   * 작업 status 응답 데이터를 전달한다.
   *
   * @param jobId 작업 식별자
   * @param portalType 포털 유형
   * @param status 상태
   * @param errorCode 오류 코드
   * @param errorMessage 오류 응답 메시지
   * @param retryable retryable 값
   * @param createdAt created at 값
   * @param updatedAt 수정 시각
   * @param finishedAt 처리 종료 시각
   */
  @Schema(description = "스크래핑 job 상태 응답")
  public record JobStatusResponse(
      @JsonProperty("job_id") String jobId,
      @JsonProperty("portal_type") String portalType,
      String status,
      @JsonProperty("error_code") String errorCode,
      @JsonProperty("error_message") String errorMessage,
      Boolean retryable,
      @JsonProperty("created_at") Instant createdAt,
      @JsonProperty("updated_at") Instant updatedAt,
      @JsonProperty("finished_at") Instant finishedAt) {}

  /**
   * 작업 summary 응답 데이터를 전달한다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param studentInfo 학생 info 값
   * @param finishedAt 처리 종료 시각
   */
  @Schema(description = "스크래핑 job 요약 응답")
  public record JobSummaryResponse(
      @JsonProperty("job_id") String jobId,
      String status,
      @JsonProperty("studentInfo") StudentInfoSummary studentInfo,
      @JsonProperty("finished_at") Instant finishedAt) {}

  /**
   * 작업 duration 응답 데이터를 전달한다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param success success 값
   * @param startedAt started at 값
   * @param endedAt ended at 값
   * @param elapsedMillis elapsed millis 값
   * @param elapsedTime elapsed time 값
   */
  @Schema(description = "스크래핑 job 소요 시간 응답")
  public record JobDurationResponse(
      @JsonProperty("job_id") String jobId,
      String status,
      Boolean success,
      @JsonProperty("started_at") Instant startedAt,
      @JsonProperty("ended_at") Instant endedAt,
      @JsonProperty("elapsed_millis") Long elapsedMillis,
      @JsonProperty("elapsed_time") String elapsedTime) {}

  /**
   * 학생 info summary 데이터를 전달한다.
   *
   * @param name 이름
   * @param school school 값
   * @param majorName 전공 이름
   * @param studentCode 학번
   * @param gradeLevel 학년
   * @param status 상태
   * @param completedSemesterType completed 학기 type 값
   */
  @Schema(description = "포털 학생 요약 정보")
  public record StudentInfoSummary(
      String name,
      String school,
      String majorName,
      String studentCode,
      int gradeLevel,
      String status,
      int completedSemesterType) {}

  /**
   * 스크래핑 결과 콜백 요청 데이터를 전달한다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param attempt attempt 값
   * @param resultS3Key 결과 S3 key 값
   * @param resultChecksum 결과 checksum 값
   * @param errorCode 오류 코드
   * @param errorMessage 오류 응답 메시지
   * @param retryable retryable 값
   * @param finishedAt 처리 종료 시각
   * @param metadata meta응답 데이터
   */
  public record ScrapeResultCallbackRequest(
      @JsonProperty("job_id") String jobId,
      String status,
      Integer attempt,
      @JsonProperty("result_s3_key") String resultS3Key,
      @JsonProperty("result_checksum") String resultChecksum,
      @JsonProperty("error_code") String errorCode,
      @JsonProperty("error_message") String errorMessage,
      Boolean retryable,
      @JsonProperty("finished_at") Instant finishedAt,
      @JsonProperty("metadata") JsonNode metadata) {}
}
