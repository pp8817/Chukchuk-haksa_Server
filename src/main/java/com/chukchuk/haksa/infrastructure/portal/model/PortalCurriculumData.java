package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

public record PortalCurriculumData(
        List<CourseInfo> courses,
        List<ProfessorInfo> professors,
        List<OfferingInfo> offerings
) {}