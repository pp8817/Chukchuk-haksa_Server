package com.chukchuk.haksa.domain.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/** 포털 link 계층 간 데이터를 전달한다. */
public class PortalLinkDto {

  /**
   * 검증할 포털 종류와 계정 자격 증명을 담는다.
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
   * 검증 완료 후 연동 작업 생성에 사용할 일회성 토큰을 담는다.
   *
   * @param portalVerificationToken 응답에 포함할 포털 검증 토큰
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
   * @param portalVerificationToken 응답에 포함할 포털 검증 토큰
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
   * @param pollingEndpoint 응답에 포함할 polling endpoint
   */
  @Schema(description = "스크래핑 job 수락 응답")
  public record AcceptedResponse(
      @JsonProperty("job_id") @Schema(description = "job id", example = "job-123") String jobId,
      @Schema(description = "수락 상태", example = "accepted") String status,
      @JsonProperty("polling_endpoint")
          @Schema(description = "상태 조회 경로", example = "/portal/link/jobs/job-123")
          String pollingEndpoint) {}

  /**
   * 포털 연동 작업의 현재 상태·오류·생성 및 완료 시각을 담는다.
   *
   * @param jobId 작업 식별자
   * @param portalType 포털 유형
   * @param status 상태
   * @param errorCode 오류 코드
   * @param errorMessage 오류 응답 메시지
   * @param retryable 실패 후 재시도 가능한지 여부
   * @param createdAt 응답에 포함할 created at
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
   * 완료된 포털 연동 작업의 학생 요약과 완료 시각을 담는다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param studentInfo 응답에 포함할 학생 info
   * @param finishedAt 처리 종료 시각
   */
  @Schema(description = "스크래핑 job 요약 응답")
  public record JobSummaryResponse(
      @JsonProperty("job_id") String jobId,
      String status,
      @JsonProperty("studentInfo") StudentInfoSummary studentInfo,
      @JsonProperty("finished_at") Instant finishedAt) {}

  /**
   * 포털 연동 작업의 시작·종료 시각과 계산된 소요 시간을 담는다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param success 응답에 포함할 success
   * @param startedAt 응답에 포함할 started at
   * @param endedAt 응답에 포함할 ended at
   * @param elapsedMillis 응답에 포함할 elapsed millis
   * @param elapsedTime 응답에 포함할 elapsed time
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
   * @param school 응답에 포함할 school
   * @param majorName 전공 이름
   * @param studentCode 학번
   * @param gradeLevel 학년
   * @param status 상태
   * @param completedSemesterType 응답에 포함할 completed 학기 type
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
   * 스크래핑 워커가 전송한 결과 위치·처리 상태·재시도 정보와 메타데이터를 담는다.
   *
   * @param jobId 작업 식별자
   * @param status 상태
   * @param attempt 응답에 포함할 attempt
   * @param resultS3Key 응답에 포함할 결과 S3 key
   * @param resultChecksum 응답에 포함할 결과 checksum
   * @param errorCode 오류 코드
   * @param errorMessage 오류 응답 메시지
   * @param retryable 실패 후 재시도 가능한지 여부
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
