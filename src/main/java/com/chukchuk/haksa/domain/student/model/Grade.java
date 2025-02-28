package com.chukchuk.haksa.domain.student.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Grade {

    @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
    private GradeType value;

    protected Grade() {} // JPA를 위한 기본 생성자

    public Grade(GradeType value) {
        this.value = value;
    }

    public static Grade createInProgress() {
        return new Grade(GradeType.IP);
    }

    public GradeType getValue() {
        return value;
    }

    public boolean isCompleted() {
        return value != GradeType.IP;
    }

    public boolean isPassingGrade() {
        return value != GradeType.F && value != GradeType.NP && value != GradeType.IP;
    }

    public double getGradePoint() {
        return value.getGradePoint();
    }
}