package com.chukchuk.haksa.application.api.wrapper;

import com.chukchuk.haksa.application.api.dto.RefreshScrapingResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RefreshScrapingApiResponse", description = "포털 재연동 응답")
public class RefreshScrapingApiResponse extends ApiResponse<RefreshScrapingResponse> {
    public RefreshScrapingApiResponse() {
        super(true, new RefreshScrapingResponse("dummy-task-id", "포털 재연동 및 학업 이력 동기화 완료"), null, null);
    }
}