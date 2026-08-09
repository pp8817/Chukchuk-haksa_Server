package com.chukchuk.haksa.domain.academic.record.repository;

import com.chukchuk.haksa.domain.academic.record.model.StudentCourse;
import com.chukchuk.haksa.domain.student.model.Student;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 학생 과목 repository 기능의 계약을 정의한다. */
@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @param year 연도
   * @param semester 학기 값
   * @return 조회
   */
  @Query(
      """
        SELECT sc FROM StudentCourse sc
        JOIN FETCH sc.offering co
        JOIN FETCH co.course c
        LEFT JOIN FETCH co.professor p
        LEFT JOIN FETCH co.liberalArtsAreaCode lac
        WHERE sc.student.id = :studentId
        AND co.year = :year
        AND co.semester = :semester
      """)
  List<StudentCourse> findByStudentIdAndYearAndSemester(
      @Param("studentId") UUID studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param student 학생 값
   * @return 조회
   */
  List<StudentCourse> findByStudent(Student student);

  /**
   * 척척학사의 delete by 학생 id 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteByStudentId(UUID studentId);

  /**
   * 척척학사의 delete owned by 학생 id and id in 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   * @param ids ids 식별자
   */
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM StudentCourse sc WHERE sc.student.id = :studentId AND sc.id IN :ids")
  void deleteOwnedByStudentIdAndIdIn(
      @Param("studentId") UUID studentId, @Param("ids") List<Long> ids);

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
        DELETE FROM StudentCourse sc
        WHERE sc.student.id = :studentId
          AND sc.offering.year = :year
          AND sc.offering.semester = :semester
      """)
  void deleteByStudentIdAndYearAndSemester(
      @Param("studentId") UUID studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);
}
