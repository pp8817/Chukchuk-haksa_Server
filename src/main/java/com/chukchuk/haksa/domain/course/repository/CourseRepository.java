package com.chukchuk.haksa.domain.course.repository;

import com.chukchuk.haksa.domain.course.model.Course;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 과목 repository 기능의 계약을 정의한다. */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param courseCode 과목 코드
   * @return 조회
   */
  Optional<Course> findByCourseCode(String courseCode);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param courseCodes 과목 codes 값
   * @return 조회
   */
  List<Course> findByCourseCodeIn(Collection<String> courseCodes);
}
