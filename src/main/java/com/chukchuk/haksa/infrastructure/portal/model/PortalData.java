package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 한 번의 포털 조회에서 얻은 학생·학사·교육과정 데이터를 묶어 전달한다.
 *
 * @param student 학생 기본 정보와 현재 학적
 * @param academic 학기별 과목과 성적 요약
 * @param curriculum 과목·교수·개설 과목 목록
 */
public record PortalData(
    PortalStudentInfo student, PortalAcademicData academic, PortalCurriculumData curriculum) {}
