package com.chukchuk.haksa.domain.course.service;

import com.chukchuk.haksa.domain.course.model.Course;
import com.chukchuk.haksa.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    @Transactional
    public Course getOrCreateCourse(String courseCode, String courseName) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseGet(() -> {
                    Course newCourse = new Course(courseCode, courseName);
                    return courseRepository.save(newCourse);
                });
    }
}
