package com.chukchuk.haksa.global.portal.dto.raw;

import java.util.List;

public record RawPortalSemesterDTO(
        String semester,
        List<RawPortalCourseDTO> courses
) {}