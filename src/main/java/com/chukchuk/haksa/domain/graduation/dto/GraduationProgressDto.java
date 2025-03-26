package com.chukchuk.haksa.domain.graduation.dto;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "졸업 진행 현황 응답")
public class GraduationProgressDto {
    @Schema(description = "학업 요약 정보")
    private StudentAcademicRecordDto.AcademicSummaryDto academicSummary;
    @Schema(description = "학기별 성적 정보 목록")
    private List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades;
    @Schema(description = "졸업 요건 영역별 이수 현황")
    private List<AreaProgressDto> graduationProgress;
}
