package com.chukchuk.haksa.infrastructure.portal.model;

public record PortalData(
        PortalStudentInfo student,
        PortalAcademicData academic,
        PortalCurriculumData curriculum
) {}