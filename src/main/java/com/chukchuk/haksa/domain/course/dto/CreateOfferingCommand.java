package com.chukchuk.haksa.domain.course.dto;

public record CreateOfferingCommand(
        Long courseId,
        Integer year,
        Integer semester,
        String classSection,
        Long professorId,
        Long departmentId,
        String scheduleSummary,
        String evaluationType, // EvaluationType name (ex: "ABSOLUTE")
        Boolean isVideoLecture,
        Integer subjectEstablishmentSemester,
        String facultyDivisionName,
        Integer areaCode,
        Integer originalAreaCode,
        Integer points,
        String hostDepartment
) {}
