package com.chukchuk.haksa.global.portal.model;

import java.util.List;

public record PortalAcademicData(
        List<SemesterCourseInfo> semesters,
        GradeSummary grades,
        AcademicSummary summary
) {}