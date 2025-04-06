package com.chukchuk.haksa.application.academic.enrollment;

import java.util.List;
import java.util.stream.Collectors;

public class CourseEnrollments {
    private List<CourseEnrollment> enrollments;

    public CourseEnrollments(List<CourseEnrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public static CourseEnrollments create(List<CourseEnrollment> enrollments) {
        return new CourseEnrollments(enrollments);
    }

    public List<CourseEnrollment> getAll() {
        return enrollments;
    }

    // '수강' 상태의 과목을 필터링하여 반환
    public CourseEnrollments getCompleted() {
        List<CourseEnrollment> completedEnrollments = enrollments.stream()
                .filter(CourseEnrollment::isCompleted)
                .collect(Collectors.toList());
        return new CourseEnrollments(completedEnrollments);
    }

    // '합격' 상태의 과목을 필터링하여 반환
    public CourseEnrollments getPassed() {
        List<CourseEnrollment> passedEnrollments = enrollments.stream()
                .filter(CourseEnrollment::isPassed)
                .collect(Collectors.toList());
        return new CourseEnrollments(passedEnrollments);
    }

    // '재수강' 상태의 과목을 필터링하여 반환
    public CourseEnrollments getRetakes() {
        List<CourseEnrollment> retakeEnrollments = enrollments.stream()
                .filter(CourseEnrollment::isRetake)
                .collect(Collectors.toList());
        return new CourseEnrollments(retakeEnrollments);
    }

    // '재수강 가능' 상태의 과목을 필터링하여 반환
    public CourseEnrollments getEligibleForRetake() {
        List<CourseEnrollment> eligibleForRetakeEnrollments = enrollments.stream()
                .filter(CourseEnrollment::isEligibleForRetake)
                .collect(Collectors.toList());
        return new CourseEnrollments(eligibleForRetakeEnrollments);
    }

    // 과목을 수강한 적이 있는지 확인
    public boolean hasPassedCourse(Long offeringId) {
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getOfferingId().equals(offeringId) && enrollment.isPassed());
    }

    // 현재 수강 중인 과목이 있는지 확인
    public boolean isCurrentlyEnrolled(Long offeringId) {
        return enrollments.stream()
                .anyMatch(enrollment -> enrollment.getOfferingId().equals(offeringId) && !enrollment.isCompleted());
    }

    // 총 학점을 계산하는 메서드
    public int calculateEarnedCredits() {
        return enrollments.stream()
                .filter(CourseEnrollment::isPassed)
                .mapToInt(CourseEnrollment::getPoints)
                .sum();
    }

    // GPA 계산하는 메서드
    public double calculateGPA() {
        double totalPoints = 0;
        double totalCredits = 0;
        for (CourseEnrollment enrollment : enrollments) {
            if (enrollment.isCompleted()) {
                totalPoints += enrollment.getGradePoint() * enrollment.getPoints();
                totalCredits += enrollment.getPoints();
            }
        }
        return totalCredits == 0 ? 0 : totalPoints / totalCredits;
    }
}
