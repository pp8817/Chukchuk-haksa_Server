// 편입생 졸업진단에서 학생이 직접 확인한 값을 전달한다.

package com.chukchuk.haksa.domain.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

/** 포털에서 확인할 수 없는 편입생 졸업심사 정보를 입력한다. */
@Schema(description = "편입생 수동 졸업진단 정보")
public record TransferManualReviewRequest(
    @Schema(description = "편입 후 등록 학기 수", example = "4", nullable = true)
        @Min(value = 0, message = "등록 학기 수는 0 이상이어야 합니다.")
        Integer registeredSemesters,
    @Schema(description = "학과 졸업논문·시험·작품·실기 통과 여부", nullable = true)
        Boolean graduationReviewFulfilled) {}
