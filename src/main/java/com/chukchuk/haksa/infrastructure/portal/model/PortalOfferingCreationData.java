package com.chukchuk.haksa.infrastructure.portal.model;

import lombok.Data;

@Data
public class PortalOfferingCreationData {
    private String courseCode;
    private int year;
    private int semester;
    private String professorName;
    private String scheduleSummary;
    private Integer points;
    private Integer subjectEstablishmentSemester;
    private Boolean isVideoLecture;
    private String classSection;
    private String evaluationType;
    private String facultyDivisionName;
    private Integer areaCode;
    private Integer originalAreaCode;
    private String hostDepartment;

    // 생성자, getter 생략 가능 (Lombok 사용 시 @Getter @NoArgsConstructor @AllArgsConstructor)
}