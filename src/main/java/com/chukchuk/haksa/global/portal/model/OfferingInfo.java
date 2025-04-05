package com.chukchuk.haksa.global.portal.model;

public record OfferingInfo(
        String courseCode,
        int year,
        int semester,
        String classSection,
        String professorName,
        String scheduleSummary,
        Integer points,
        String hostDepartment,
        String facultyDivisionName,
        Integer subjectEstablishmentSemester,
        Integer areaCode,
        Integer originalAreaCode
) {}
