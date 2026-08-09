package com.chukchuk.haksa.infrastructure.portal.dto.raw;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawPortalData(
    @JsonAlias("studentInfo") RawPortalStudentDTO student,
    List<RawPortalSemesterDTO> semesters,
    RawPortalGradeResponseDTO academicRecords) {}
