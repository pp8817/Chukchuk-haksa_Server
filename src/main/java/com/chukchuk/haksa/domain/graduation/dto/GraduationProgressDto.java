package com.chukchuk.haksa.domain.graduation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "졸업 요건 진행 상황 응답")
public class GraduationProgressDto {
    @Schema(description = "졸업 요건 영역별 이수 현황")
    private List<AreaProgressDto> graduationProgress;
}
