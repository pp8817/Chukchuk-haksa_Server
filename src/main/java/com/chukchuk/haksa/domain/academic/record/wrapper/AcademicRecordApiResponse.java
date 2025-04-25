package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AcademicRecordApiResponse", description = "학기별 성적 및 수강 과목 정보 응답")
public class AcademicRecordApiResponse extends ApiResponse<AcademicRecordResponse> {
    public AcademicRecordApiResponse() {
        super(true, new AcademicRecordResponse(null, null), null, null);
    }
}
