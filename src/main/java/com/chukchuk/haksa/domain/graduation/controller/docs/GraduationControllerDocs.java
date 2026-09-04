package com.chukchuk.haksa.domain.graduation.controller.docs;

import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.graduation.dto.LanguageCertRequirementResponse;
import com.chukchuk.haksa.domain.graduation.dto.TransferManualReviewRequest;
import com.chukchuk.haksa.domain.graduation.wrapper.GraduationProgressApiResponse;
import com.chukchuk.haksa.domain.graduation.wrapper.LanguageCertRequirementApiResponse;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

/** 인증 학생의 졸업요건 진행률과 외국어 인증 기준 조회 API를 정의한다. */
@Tag(name = "Graduation", description = "졸업 요건 및 진행 현황 관련 API")
public interface GraduationControllerDocs {

  /**
   * 로그인 학생의 영역별 졸업 진행도를 조회한다.
   *
   * @param userDetails 인증된 사용자 정보
   * @return 영역별 이수 현황과 전체 졸업 상태 성공 응답
   */
  @Operation(
      summary = "졸업 요건 진행 상황 조회",
      description =
          "로그인된 사용자의 졸업 요건 충족 여부와 외국어 졸업 인증 통과 여부를 조회합니다. areaType이 선교인 영역의 "
              + "courses[] 과목은 liberalAreaCode를 포함할 수 있습니다. areaType이 선교가 아닌 영역의 "
              + "courses[] 과목은 liberalAreaCode 키가 응답에 포함되지 않습니다. 편입생은 일반 재학생의 영역별 졸업요건을 "
              + "transferProgress에 총 취득학점, 편입 인정학점, 전공 요건, GPA, 지정과목 이수 현황과 최종 졸업 가능 여부를 "
              + "반환합니다. 포털에서 확인할 수 없는 등록 학기나 졸업심사 값이 없으면 analysisStatus가 "
              + "MANUAL_REVIEW_REQUIRED로 반환됩니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "졸업 요건 충족 여부 조회 성공",
            content =
                @Content(schema = @Schema(implementation = GraduationProgressApiResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "사용자 정보 없음 (ErrorCode: S01, FRESHMAN_NO_SEMESTER)",
            content = @Content(schema = @Schema(implementation = ErrorResponseWrapper.class))),
        @ApiResponse(
            responseCode = "404",
            description = "졸업 요건 정보 없음 (ErrorCode: G01, GRADUATION_REQUIREMENTS_NOT_FOUND)",
            content = @Content(schema = @Schema(implementation = ErrorResponseWrapper.class)))
      })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<GraduationProgressResponse>> getGraduationProgress(
      @AuthenticationPrincipal CustomUserDetails userDetails);

  /**
   * 로그인 학생에게 적용되는 외국어 인증 기준을 조회한다.
   *
   * @param userDetails 인증된 사용자 정보
   * @return 학생에게 적용되는 외국어 인증 기준 성공 응답
   */
  @Operation(
      summary = "외국어 인증 기준 조회",
      description = "로그인된 사용자의 학과 코드와 입학년도에 적용되는 외국어 인증 기준을 조회합니다. 미매핑 학과도 200 응답으로 반환됩니다.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "외국어 인증 기준 조회 성공",
            content =
                @Content(
                    schema = @Schema(implementation = LanguageCertRequirementApiResponse.class))),
        @ApiResponse(
            responseCode = "404",
            description = "사용자 정보 없음 (ErrorCode: S01)",
            content = @Content(schema = @Schema(implementation = ErrorResponseWrapper.class)))
      })
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<LanguageCertRequirementResponse>> getLanguageCertRequirement(
      @AuthenticationPrincipal CustomUserDetails userDetails);

  /**
   * 편입생의 포털 외부 수동 졸업진단 정보를 저장한다.
   *
   * @param userDetails 인증된 사용자 정보
   * @param request 저장할 수동 졸업진단 정보
   * @return 저장 완료 메시지
   */
  @Operation(summary = "편입생 수동 졸업진단 정보 저장", description = "편입 후 등록 학기 수와 학과 졸업심사 결과를 저장합니다.")
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> updateTransferManualReview(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody TransferManualReviewRequest request);
}
