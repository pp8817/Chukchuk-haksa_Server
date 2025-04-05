package com.chukchuk.haksa.global.portal.model;

import java.util.List;

public record GradeSummary(
        List<SemesterGrade> semesters,
        AcademicSummary summary
) {}
