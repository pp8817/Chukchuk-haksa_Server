package com.chukchuk.haksa.domain.academic.record.wrapper;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

@Schema(name = "SemesterGradesApiResponse", description = "학기별 성적 목록 응답")
public class SemesterGradesApiResponse extends ApiResponse<List<SemesterAcademicRecordDto.SemesterGradeDto>> {
    public SemesterGradesApiResponse() {
        super(true, Collections.emptyList(), null, null);
    }
}
