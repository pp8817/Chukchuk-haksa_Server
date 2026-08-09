package com.chukchuk.haksa.infrastructure.portal.dto.raw;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 계층 간 전달할 raw 포털 data 데이터를 표현한다.
 *
 * @param student 학생 값
 * @param semesters semesters 값
 * @param academicRecords 학사 records 값
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawPortalData(
    @JsonAlias("studentInfo") RawPortalStudentDto student,
    List<RawPortalSemesterDto> semesters,
    RawPortalGradeResponseDto academicRecords) {}
