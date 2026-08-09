package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 포털 학생 info 데이터를 표현한다.
 *
 * @param studentCode 학번
 * @param name 이름
 * @param college 단과대학 값
 * @param department 학과 값
 * @param major 전공 값
 * @param secondaryMajor 복수전공
 * @param status 상태
 * @param admission admission 값
 * @param academic 학사 값
 * @param languageCertFulfilled 어학 인증 충족 여부
 */
public record PortalStudentInfo(
    String studentCode,
    String name,
    CodeName college,
    CodeName department,
    CodeName major,
    CodeName secondaryMajor,
    String status,
    AdmissionInfo admission,
    PortalAcademicInfo academic,
    Boolean languageCertFulfilled) {}
