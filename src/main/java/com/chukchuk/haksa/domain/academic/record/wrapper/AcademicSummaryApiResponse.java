package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import static com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto.AcademicSummaryDto;

@Schema(name = "AcademicSummaryApiResponse", description = "학업 요약 정보 응답")
public class AcademicSummaryApiResponse extends ApiResponse<AcademicSummaryDto> {
    public AcademicSummaryApiResponse() {
        super(true, new AcademicSummaryDto(0, BigDecimal.ZERO, BigDecimal.ZERO, 0), null, null);
    }
}
