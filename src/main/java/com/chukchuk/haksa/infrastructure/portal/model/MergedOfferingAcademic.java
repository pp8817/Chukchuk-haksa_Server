package com.chukchuk.haksa.infrastructure.portal.model;

public class MergedOfferingAcademic {
    private PortalOfferingCreationData offering;
    private PortalCourseInfo academic; // null 가능

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