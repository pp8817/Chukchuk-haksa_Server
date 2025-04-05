package com.chukchuk.haksa.global.portal.dto.raw;
import java.util.List;

public record RawPortalData(
        RawPortalStudentDTO student,
        List<RawPortalSemesterDTO> semesters,
        RawPortalGradeResponseDTO academicRecords
) {}