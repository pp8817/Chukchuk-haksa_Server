package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

public record PortalAcademicData(
        List<SemesterCourseInfo> semesters,
        GradeSummary grades,
        AcademicSummary summary
) {}