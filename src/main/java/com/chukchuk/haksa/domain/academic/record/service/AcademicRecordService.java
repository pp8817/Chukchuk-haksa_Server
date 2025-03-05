package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.dto.AcademicRecordResponse;
import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentCourseDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicRecordService {
    private final SemesterAcademicRecordService semesterAcademicRecordService;
    private final StudentCourseService studentCourseService;
    private final UserService userService;

    /* 학기별 성적 및 수강 과목 정보 조회 */
    public AcademicRecordResponse getAcademicRecord(String userEmail, Integer year, Integer semester) {

        UUID studentId = userService.getUserId(userEmail);

        // 학기별 성적 조회
        List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades =
                semesterAcademicRecordService.getSemesterGrades(studentId, year, semester);

        // TODO: 수강 과목 조회, 과목 카테고리 분류를 하나로 병합?
        // 수강 과목 조회
        List<StudentCourseDto.CourseDetailDto> courses = studentCourseService.getStudentCourses(studentId, year, semester);

        // 과목 카테고리 분류
        Map<String, List<StudentCourseDto.CourseDetailDto>> categorizedCourses = courses.stream()
                .collect(Collectors.groupingBy(course -> getCourseCategory(course.areaType())));

        List<StudentCourseDto.CourseDetailDto> majorCourses = categorizedCourses.getOrDefault("major", List.of());
        List<StudentCourseDto.CourseDetailDto> liberalCourses = categorizedCourses.getOrDefault("liberal", List.of());

        // 5️⃣ 전공 평점 계산
        double majorGpa = calculateMajorGpa(majorCourses);

        // TODO: earnedCredits, semesterGpa이 Null로 오는 경우 대비 필요
        // 최종 응답 데이터 생성
        return new AcademicRecordResponse(
                semesterGrades,
                new AcademicRecordResponse.Courses(majorCourses, liberalCourses),
                AcademicRecordResponse.Summary.from(semesterGrades, majorGpa)
        );
    }

    /* Using Method */

    // 전공, 교양 분류 메서드
    private String getCourseCategory(String areaType) {
        return switch (areaType) {
            case "전핵", "전선" -> "major";
            default -> "liberal";
        };
    }

    // 전공 평점 계산 메서드
    private double calculateMajorGpa(List<StudentCourseDto.CourseDetailDto> majorCourses) {
        if (majorCourses.isEmpty()) {
            return 0;
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
                totalPoints += points * course.credits().intValue();
                totalCredits += course.credits().intValue();
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
}
