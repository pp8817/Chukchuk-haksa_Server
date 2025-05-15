package com.chukchuk.haksa.application.api.dto;

import com.chukchuk.haksa.infrastructure.portal.model.InitializePortalConnectionResult;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포털 연동 및 학업 이력 동기화 성공 응답")
public record StartScrapingResponse(
        @Schema(description = "작업 ID", example = "4aabf0d0-1c23-4f3d-845e-24c9c943deed", required = true)
        String taskId,

        @Schema(description = "학생 정보 요약", required = true)
        InitializePortalConnectionResult.StudentInfo studentInfo
) {}