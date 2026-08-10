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

/** 학생의 학기 성적을 학생·연도·학기로 조회하고 학생 단위로 삭제하는 저장소다. */
@Repository
public interface SemesterAcademicRecordRepository
    extends JpaRepository<SemesterAcademicRecord, UUID> {

  /**
   * 학사 기록를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param studentId 학생 식별자
   * @param year 대상 연도
   * @param semester 대상 학기
   * @return 조건에 일치하는 학사 기록가 있으면 포함한 선택값
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
   * 학사 기록를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조건에 일치하는 학사 기록 목록
   */
  List<SemesterAcademicRecord> findByStudentId(UUID studentId); // studentID로 data 얻어오기

  /**
   * 학사 기록를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조건에 일치하는 학사 기록 목록
   */
  List<SemesterAcademicRecord> findByStudentIdOrderByYearDescSemesterDesc(UUID studentId);

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
   * @param year 대상 연도
   * @param semester 대상 학기
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
