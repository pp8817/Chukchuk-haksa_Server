package com.chukchuk.haksa.global.portal.dto.raw;

public record RawPortalStudentDTO(
        String sno,
        String studNm,
        String univCd,
        String univNm,
        String dpmjCd,
        String dpmjNm,
        String mjorCd,
        String mjorNm,
        String the2MjorCd,
        String the2MjorNm,
        String scrgStatNm,
        String enscYear,
        String enscSmrCd,
        String enscDvcd,
        Integer studGrde,
        Integer facSmrCnt
) {}