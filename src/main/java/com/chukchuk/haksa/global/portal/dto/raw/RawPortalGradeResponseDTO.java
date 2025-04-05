package com.chukchuk.haksa.global.portal.dto.raw;

import java.util.List;

public record RawPortalGradeResponseDTO(
        List<RawPortalSemesterGradeDTO> listSmrCretSumTabYearSmr,
        RawPortalGradeSummaryDTO selectSmrCretSumTabSjTotal
) {}