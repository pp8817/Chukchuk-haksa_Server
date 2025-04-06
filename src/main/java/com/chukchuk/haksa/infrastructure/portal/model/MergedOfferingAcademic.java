package com.chukchuk.haksa.infrastructure.portal.model;

import org.springframework.lang.Nullable;

public class MergedOfferingAcademic {
    private final PortalOfferingCreationData offering;
    private final @Nullable PortalCourseInfo academic;

    public MergedOfferingAcademic(PortalOfferingCreationData offering, PortalCourseInfo academic) {
        this.offering = offering;
        this.academic = academic;
    }

    public PortalOfferingCreationData getOffering() {
        return offering;
    }

    public PortalCourseInfo getAcademic() {
        return academic;
    }
}