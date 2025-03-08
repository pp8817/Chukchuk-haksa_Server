package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chukchuk.haksa.domain.academic.record.dto.StudentCourseDto.CourseDetailDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCourseService {
    private StudentCourseRepository studentCourseRepository;

    public List<CourseDetailDto> getStudentCourses(UUID studentId, Integer year, Integer semester) {
        List<StudentCourse> courses = studentCourseRepository.findByStudentIdAndYearAndSemester(studentId, year, semester);

        return courses.stream().map(CourseDetailDto::from)
                .collect(Collectors.toList());
    }
}
