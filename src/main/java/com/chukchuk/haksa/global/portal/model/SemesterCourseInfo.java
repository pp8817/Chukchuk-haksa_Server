package com.chukchuk.haksa.global.portal.model;

import java.util.List;

public record SemesterCourseInfo(
        int year,
        int semester,
        List<CourseInfo> courses
) {}