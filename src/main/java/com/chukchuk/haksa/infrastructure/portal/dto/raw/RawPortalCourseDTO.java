package com.chukchuk.haksa.infrastructure.portal.dto.raw;

public record RawPortalCourseDTO(
        String subjtCd,
        String subjtNm,
        String ltrPrfsNm,
        String estbDpmjNm,
        Integer point,
        String cretGrdCd,
        String refacYearSmr,
        String timtSmryCn,
        String facDvnm,
        String cltTerrNm,
        String cltTerrCd,
        String subjtEstbSmrCd,
        String subjtEstbYearSmr,
        String diclNo,
        String gainPont
) {}