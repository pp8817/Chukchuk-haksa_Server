package com.chukchuk.haksa.application.academic;


import com.chukchuk.haksa.application.academic.enrollment.CourseEnrollments;

import java.util.Collections;
import java.util.List;

public class AcademicRecord {
    private String studentId;
    private List<SemesterGrade> semesters;
    private AcademicSummary summary;
    private CourseEnrollments courseEnrollments;

    public AcademicRecord(String studentId, List<SemesterGrade> semesters, AcademicSummary summary, CourseEnrollments courseEnrollments) {
        this.studentId = studentId;
        this.semesters = semesters;
        this.summary = summary;
        this.courseEnrollments = courseEnrollments;
    }

    public static AcademicRecord create(String studentId, List<SemesterGrade> semesters, AcademicSummary summary, CourseEnrollments courseEnrollments) {
        return new AcademicRecord(studentId, semesters, summary, courseEnrollments);
    }

    public String getStudentId() {
        return studentId;
    }

    public List<SemesterGrade> getSemesters() {
        if (semesters == null) {
            return Collections.emptyList();
        }
        Collections.sort(semesters, (a, b) -> {
            if (b.getYear() != a.getYear()) {
                return Integer.compare(b.getYear(), a.getYear());
            }
            return Integer.compare(b.getSemester(), a.getSemester());
        });
        return semesters;
    }

    public SemesterGrade getSemesterByYearAndSemester(int year, int semester) {
        return semesters.stream()
                .filter(sem -> sem.getYear() == year && sem.getSemester() == semester)
                .findFirst()
                .orElse(null);
    }

    public SemesterGrade getLatestSemester() {
        return getSemesters().isEmpty() ? null : getSemesters().get(0);
    }

    public AcademicSummary getSummary() {
        return summary;
    }

    public CourseEnrollments getCourseEnrollments() {
        return courseEnrollments;
    }

    public Integer getTotalEarnedCredits() {
        return summary.getTotalEarnedCredits();
    }

    public Double getCumulativeGpa() {
        return summary.getCumulativeGpa();
    }

    public Double getPercentile() {
        return summary.getPercentile();
    }

    public boolean validateGradeConsistency() {
        Double calculatedGPA = courseEnrollments.calculateGPA();
        Double reportedGPA = summary.getCumulativeGpa();
        return calculatedGPA != null && reportedGPA != null && Math.abs(calculatedGPA - reportedGPA) < 0.01;
    }

    public boolean validateCreditsConsistency() {
        int calculatedCredits = courseEnrollments.calculateEarnedCredits();
        return calculatedCredits == summary.getTotalEarnedCredits();
    }

    public CourseEnrollments getCompletedCourses() {
        return courseEnrollments.getCompleted();
    }

    public CourseEnrollments getPassedCourses() {
        return courseEnrollments.getPassed();
    }

    public CourseEnrollments getRetakeCourses() {
        return courseEnrollments.getRetakes();
    }

    public CourseEnrollments getEligibleForRetakeCourses() {
        return courseEnrollments.getEligibleForRetake();
    }

    public boolean hasPassedCourse(int offeringId) {
        return courseEnrollments.hasPassedCourse(offeringId);
    }

    public boolean isCurrentlyEnrolled(int offeringId) {
        return courseEnrollments.isCurrentlyEnrolled(offeringId);
    }

    public boolean canTakeCourse(int offeringId) {
        return !hasPassedCourse(offeringId) && !isCurrentlyEnrolled(offeringId);
    }
}