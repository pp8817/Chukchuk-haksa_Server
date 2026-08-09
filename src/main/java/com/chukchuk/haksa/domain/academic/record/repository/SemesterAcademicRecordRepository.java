package com.chukchuk.haksa.domain.academic.record.repository;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 학기 학사 record repository 기능의 계약을 정의한다. */
@Repository
public interface SemesterAcademicRecordRepository
    extends JpaRepository<SemesterAcademicRecord, UUID> {

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
    SELECT sar
    FROM SemesterAcademicRecord sar
    WHERE sar.student.id = :studentId
      AND sar.year = :year
      AND sar.semester = :semester
      """)
  Optional<SemesterAcademicRecord> findByStudentIdAndYearAndSemester(
      @Param("studentId") UUID studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  List<SemesterAcademicRecord> findByStudentId(UUID studentId); // studentID로 data 얻어오기

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  List<SemesterAcademicRecord> findByStudentIdOrderByYearDescSemesterDesc(UUID studentId);

  /**
   * 척척학사의 delete by 학생 id 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteByStudentId(UUID studentId);

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
        DELETE FROM SemesterAcademicRecord sar
        WHERE sar.student.id = :studentId
          AND sar.year = :year
          AND sar.semester = :semester
      """)
  void deleteByStudentIdAndYearAndSemester(
      @Param("studentId") UUID studentId,
      @Param("year") Integer year,
      @Param("semester") Integer semester);
}
