package com.chukchuk.haksa.domain.graduation.dto;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GraduationProgressDto {
    private StudentAcademicRecordDto.AcademicSummaryDto academicSummary;
    private List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades;
    private List<AreaProgressDto> graduationProgress;
}
