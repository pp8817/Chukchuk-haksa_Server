package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 admission info 데이터를 표현한다.
 *
 * @param year 연도
 * @param semester 학기 값
 */
public record AdmissionInfo(int year, int semester, String type // 예: 정시, 편입 등
    ) {}
