package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentCourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicRecordService {
    private final SemesterAcademicRecordService semesterAcademicRecordService;
    private final StudentAcademicRecordService studentAcademicRecordService;
    private final StudentCourseService studentCourseService;

    /* 학기별 성적 및 수강 과목 정보 조회 */
    public AcademicRecordResponse getAcademicRecord(UUID userId, Integer year, Integer semester) {

        // 학기별 성적 조회
        SemesterAcademicRecordDto.SemesterGradeDto semesterGrade =
                semesterAcademicRecordService.getSemesterGradesByYearAndSemester(userId, year, semester);

        // 수강 과목 조회 및 카테고리 분류
        Map<String, List<StudentCourseDto.CourseDetailDto>> categorizedCourses = categorizeCourses(
                studentCourseService.getStudentCourses(userId, year, semester));

        List<StudentCourseDto.CourseDetailDto> majorCourses = categorizedCourses.getOrDefault("major", List.of());
        List<StudentCourseDto.CourseDetailDto> liberalCourses = categorizedCourses.getOrDefault("liberal", List.of());

//        // 전공 평점 계산
//        double majorGpa = calculateMajorGpa(majorCourses);

        return new AcademicRecordResponse(
                semesterGrade,
                new AcademicRecordResponse.Courses(majorCourses, liberalCourses)
        );
    }

    public StudentAcademicRecordDto.AcademicSummaryDto getAcademicSummary(UUID userId) {

        return studentAcademicRecordService.getAcademicSummary(userId);
    }

    /* Using Method */

    /*과목을 전공/교양으로 분류*/
    private Map<String, List<StudentCourseDto.CourseDetailDto>> categorizeCourses(List<StudentCourseDto.CourseDetailDto> courses) {
        return courses.stream()
                .collect(Collectors.groupingBy(course -> switch (course.areaType()) {
                    case 전핵, 전선, 복선 -> "major";
                    default -> "liberal";
                }));
    }

    /* 전공 평점 계산 메서드 */
    private double calculateMajorGpa(List<StudentCourseDto.CourseDetailDto> majorCourses) {
        if (majorCourses.isEmpty()) {
            return 0.0;
        }

        Map<String, Double> gradePoints = Map.of(
                "A+", 4.5, "A0", 4.0,
                "B+", 3.5, "B0", 3.0,
                "C+", 2.5, "C0", 2.0,
                "D+", 1.5, "D0", 1.0,
                "F", 0.0
        );

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (StudentCourseDto.CourseDetailDto course : majorCourses) {
            if (gradePoints.containsKey(course.grade())) {
                double points = gradePoints.get(course.grade());
                int credits = course.credits().intValue();
                totalPoints += points * credits;
                totalCredits += credits;
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
}
