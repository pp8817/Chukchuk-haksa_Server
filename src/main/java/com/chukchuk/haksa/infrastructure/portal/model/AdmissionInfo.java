package com.chukchuk.haksa.infrastructure.portal.model;

public record AdmissionInfo(
        int year,
        int semester,
        String type // 예: 정시, 편입 등
) {}