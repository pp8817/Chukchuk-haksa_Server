package com.chukchuk.haksa.global.portal.model;

public record PortalStudentInfo(
        String studentCode,
        String name,
        CodeName college,
        CodeName department,
        CodeName major,
        CodeName secondaryMajor,
        String status,
        AdmissionInfo admission,
        PortalAcademicInfo academic
) {}