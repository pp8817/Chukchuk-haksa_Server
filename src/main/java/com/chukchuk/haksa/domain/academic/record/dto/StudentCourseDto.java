package com.chukchuk.haksa.domain.academic.record.dto;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;

import java.util.Objects;

public class StudentCourseDto {

    // TODO: 타입 일치하는지 확인
    public record CourseDetailDto(
            String id,
            String courseName, // 과목명
            String courseCode, // 학수번호
            String areaType, // 영역 (전공, 교양 등)
            Integer credits, // 학점
            String professor, // 교수명
            String grade, // 성적 (A+, A0, B+ 등)
            Integer score, // 실점수 (있는 경우)
            Boolean isRetake, // 재수강 여부
            Boolean isOnline, // 사이버강의 여부
            Integer year, // 이수년도
            Integer semester, // 이수학기코드
            Integer originalScore // 실 점수
    ) {
        public static CourseDetailDto from(StudentCourse course) {

            return new CourseDetailDto(
                    String.valueOf(course.getId()),
                    course.getOffering().getCourse().getCourseName(),
                    course.getOffering().getCourse().getCourseCode(),
                    course.getOffering().getFacultyDivisionName().name(),
                    course.getOffering().getPoints(),
                    course.getOffering().getProfessor() != null ? course.getOffering().getProfessor().getProfessorName() : "미지정",
                    course.getGrade() != null ? course.getGrade().getValue().getValue() : "F",
                    course.getPoints() != null ? course.getPoints() : 0,
                    Objects.requireNonNullElse(course.getIsRetake(), false),
                    Objects.requireNonNullElse(course.getOffering().getIsVideoLecture(), false),
                    course.getOffering().getYear(),
                    course.getOffering().getSemester(),
                    course.getOriginalScore() != null ? course.getOriginalScore() : 0
            );
        }
    }
}
