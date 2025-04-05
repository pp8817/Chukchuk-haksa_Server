package com.chukchuk.haksa.application.academic;

import lombok.Data;

@Data
public class SemesterGrade {

    private int year;
    private int semester;
    private int attemptedCredits;
    private int earnedCredits;
    private double semesterGpa;
    private double semesterPercentile;
    private Double attemptedCreditsGpa;
    private Integer classRank;
    private Integer totalStudents;

    public SemesterGrade(int year, int semester, int attemptedCredits, int earnedCredits, double semesterGpa, double semesterPercentile, Double attemptedCreditsGpa, Integer classRank, Integer totalStudents) {
        this.year = year;
        this.semester = semester;
        this.attemptedCredits = attemptedCredits;
        this.earnedCredits = earnedCredits;
        this.semesterGpa = semesterGpa;
        this.semesterPercentile = semesterPercentile;
        this.attemptedCreditsGpa = attemptedCreditsGpa;
        this.classRank = classRank;
        this.totalStudents = totalStudents;
    }
}