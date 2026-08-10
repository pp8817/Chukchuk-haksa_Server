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

/** 학생의 수강 내역을 학생·연도·학기로 조회하고 소유권 범위에서 삭제하는 저장소다. */
@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
  /**
   * 학사 기록를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param studentId 학생 식별자
   * @param year 대상 연도
   * @param semester 대상 학기
   * @return 조건에 일치하는 학사 기록 목록
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
   * 학사 기록를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param student 대상 학생
   * @return 조건에 일치하는 학사 기록 목록
   */
  List<StudentCourse> findByStudent(Student student);

  /**
   * 지정된 조건에 해당하는 학사 기록를 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteByStudentId(UUID studentId);

  /**
   * 지정된 조건에 해당하는 학사 기록를 삭제한다.
   *
   * @param studentId 학생 식별자
   * @param ids 삭제할 식별자 집합
   */
  @Modifying(clearAutomatically = true)
  @Query("DELETE FROM StudentCourse sc WHERE sc.student.id = :studentId AND sc.id IN :ids")
  void deleteOwnedByStudentIdAndIdIn(
      @Param("studentId") UUID studentId, @Param("ids") List<Long> ids);

  /**
   * 지정된 조건에 해당하는 학사 기록를 삭제한다.
   *
   * @param studentId 학생 식별자
   * @param year 대상 연도
   * @param semester 대상 학기
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
