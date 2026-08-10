package com.chukchuk.haksa.domain.course.repository;

import com.chukchuk.haksa.domain.course.model.Course;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 과목 코드와 이름으로 과목을 조회하는 저장소다. */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  /**
   * 과목 및 개설 강의를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param courseCode 과목 코드
   * @return 조건에 일치하는 과목 및 개설 강의가 있으면 포함한 선택값
   */
  Optional<Course> findByCourseCode(String courseCode);

  /**
   * 과목 및 개설 강의를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param courseCodes 과목 codes
   * @return 조건에 일치하는 과목 및 개설 강의 목록
   */
  List<Course> findByCourseCodeIn(Collection<String> courseCodes);
}
