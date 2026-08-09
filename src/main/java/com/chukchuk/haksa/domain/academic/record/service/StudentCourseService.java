package com.chukchuk.haksa.domain.academic.record.service;

import static com.chukchuk.haksa.domain.academic.record.dto.StudentCourseDto.CourseDetailDto;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 학생 과목 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentCourseService {
  private final StudentCourseRepository studentCourseRepository;

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @param year 연도
   * @param semester 학기 값
   * @return 조회
   */
  public List<CourseDetailDto> getStudentCourses(UUID studentId, Integer year, Integer semester) {
    List<StudentCourse> courses =
        studentCourseRepository.findByStudentIdAndYearAndSemester(studentId, year, semester);

    return courses.stream().map(CourseDetailDto::from).collect(Collectors.toList());
  }
}
