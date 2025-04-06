package com.chukchuk.haksa.infrastructure.portal.dto.raw;
import java.util.List;

public record RawPortalData(
        RawPortalStudentDTO student,
        List<RawPortalSemesterDTO> semesters,
        RawPortalGradeResponseDTO academicRecords
) {}