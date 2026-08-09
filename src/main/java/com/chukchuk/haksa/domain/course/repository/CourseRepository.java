package com.chukchuk.haksa.domain.course.repository;

import com.chukchuk.haksa.domain.course.model.Course;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
  Optional<Course> findByCourseCode(String courseCode);

  List<Course> findByCourseCodeIn(Collection<String> courseCodes);
}
