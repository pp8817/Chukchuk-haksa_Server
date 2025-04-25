package com.chukchuk.haksa.domain.auth.wrapper;

import com.chukchuk.haksa.domain.auth.dto.AuthDto.RefreshResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RefreshTokenApiResponse", description = "토큰 재발급 응답 포맷")
public class RefreshTokenApiResponse extends ApiResponse<RefreshResponse> {
    public RefreshTokenApiResponse() {
        super(true, new RefreshResponse("new-access-token", "new-refresh-token"), null, null);
    }
}