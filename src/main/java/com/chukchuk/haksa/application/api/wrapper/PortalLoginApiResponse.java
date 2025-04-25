package com.chukchuk.haksa.application.api.wrapper;

import com.chukchuk.haksa.application.api.dto.PortalLoginMessageResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PortalLoginApiResponse", description = "포털 로그인 응답")
public class PortalLoginApiResponse extends ApiResponse<PortalLoginMessageResponse> {
    public PortalLoginApiResponse() {
        super(true, new PortalLoginMessageResponse("로그인 성공"), null, null);
    }
}
