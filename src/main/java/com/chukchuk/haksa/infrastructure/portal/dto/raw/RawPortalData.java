package com.chukchuk.haksa.infrastructure.portal.dto.raw;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 워커 payload에서 역직렬화한 학생·학기·성적 원본 데이터를 묶어 전달한다.
 *
 * @param student 포털에서 조회한 학생 기본 정보
 * @param semesters 학기별 수강 과목 원본 목록
 * @param academicRecords 학기별 성적과 누적 요약 응답
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawPortalData(
    @JsonAlias("studentInfo") RawPortalStudentDto student,
    List<RawPortalSemesterDto> semesters,
    RawPortalGradeResponseDto academicRecords) {}
