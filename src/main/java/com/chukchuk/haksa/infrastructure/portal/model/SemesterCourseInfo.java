package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

public record SemesterCourseInfo(
        int year,
        int semester,
        List<CourseInfo> courses
) {}