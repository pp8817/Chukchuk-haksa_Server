package com.chukchuk.haksa.domain.lectureevaluations.repository;

import com.chukchuk.haksa.domain.lectureevaluations.model.CourseEvaluationTag;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 과목 evaluation tag repository 기능의 계약을 정의한다. */
@Repository
public interface CourseEvaluationTagRepository extends JpaRepository<CourseEvaluationTag, Long> {

  /**
   * 척척학사의 delete by 학생 id and year and 학기 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   * @param year 연도
   * @param semester 학기 값
   */
  @Modifying
  @Query(
      """
        DELETE FROM CourseEvaluationTag tag
        WHERE tag.courseEvaluation.id IN (
            SELECT ce.id
            FROM CourseEvaluation ce
            WHERE ce.student.id = :studentId
              AND ce.year = :year
              AND ce.semester = :semester
        )
      """)
  void deleteByStudentIdAndYearAndSemester(
      @Param("studentId") UUID studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);
}
