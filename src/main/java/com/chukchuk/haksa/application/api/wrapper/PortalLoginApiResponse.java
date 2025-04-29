package com.chukchuk.haksa.application.api.wrapper;

import com.chukchuk.haksa.application.api.dto.PortalLoginResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PortalLoginApiResponse", description = "포털 로그인 응답")
public class PortalLoginApiResponse extends SuccessResponse<PortalLoginResponse> {

    public PortalLoginApiResponse() {
        super(new PortalLoginResponse("로그인 성공"), "요청 성공");
    }
}