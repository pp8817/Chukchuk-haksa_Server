package com.chukchuk.haksa.infrastructure.portal.model;

import java.util.List;

/**
 * 계층 간 전달할 포털 curriculum data 데이터를 표현한다.
 *
 * @param courses courses 값
 * @param professors professors 값
 * @param offerings offerings 값
 */
public record PortalCurriculumData(
    List<CourseInfo> courses, List<ProfessorInfo> professors, List<OfferingInfo> offerings) {}
