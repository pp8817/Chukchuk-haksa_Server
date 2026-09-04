// 편입생 졸업요건 한 영역의 필요·취득 학점과 충족 여부를 전달한다.

package com.chukchuk.haksa.domain.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 편입생 전공 영역별 졸업요건 진행 현황이다. */
@Schema(description = "편입생 전공 영역별 졸업요건 진행 현황")
public record TransferRequirementProgressDto(
    @Schema(description = "필요 학점", example = "60", nullable = true) Integer requiredCredits,
    @Schema(description = "취득 학점", example = "45", nullable = true) Integer earnedCredits,
    @Schema(description = "충족 여부", nullable = true) Boolean fulfilled) {}
