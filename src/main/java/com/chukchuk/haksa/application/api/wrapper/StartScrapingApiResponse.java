package com.chukchuk.haksa.application.api.wrapper;

import com.chukchuk.haksa.application.api.dto.StartScrapingResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StartScrapingApiResponse", description = "포털 데이터 크롤링 응답")
public class StartScrapingApiResponse extends SuccessResponse<StartScrapingResponse> {

    public StartScrapingApiResponse() {
        super(new StartScrapingResponse("dummy-task-id", null), "요청 성공");
    }
}
