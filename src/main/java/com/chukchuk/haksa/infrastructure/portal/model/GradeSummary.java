package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

public record GradeSummary(
        List<SemesterGrade> semesters,
        AcademicSummary summary
) {}
