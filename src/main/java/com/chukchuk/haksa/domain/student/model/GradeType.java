package com.chukchuk.haksa.domain.student.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GradeType {
    A_PLUS("A+", 4.5),
    A0("A0", 4.0),
    B_PLUS("B+", 3.5),
    B0("B0", 3.0),
    C_PLUS("C+", 2.5),
    C0("C0", 2.0),
    D_PLUS("D+", 1.5),
    D0("D0", 1.0),
    F("F", 0.0),
    P("P", 0.0),
    NP("NP", 0.0),
    IP("IP", 0.0); // In Progress

    private final String value;
    private final double gradePoint;

    public boolean isPassingGrade() {
        return this != F && this != NP && this != IP;
    }

    public boolean isCompleted() {
        return this != IP;
    }
}