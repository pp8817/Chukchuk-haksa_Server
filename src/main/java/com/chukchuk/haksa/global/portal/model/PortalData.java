package com.chukchuk.haksa.global.portal.model;

public record PortalData(
        PortalStudentInfo student,
        PortalAcademicData academic,
        PortalCurriculumData curriculum
) {}