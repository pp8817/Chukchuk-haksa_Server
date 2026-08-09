package com.chukchuk.haksa.domain.academic.record.repository;

import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 학생 학사 record repository 기능의 계약을 정의한다. */
@Repository
public interface StudentAcademicRecordRepository
    extends JpaRepository<StudentAcademicRecord, UUID> {

  // student_id(UUID)로 직접 조회
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  @Query(
      """
        SELECT r FROM StudentAcademicRecord r
        JOIN FETCH r.student s
        WHERE s.id = :studentId
      """)
  Optional<StudentAcademicRecord> findByStudentId(@Param("studentId") UUID studentId);

  /**
   * 척척학사의 delete by 학생 id 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteByStudentId(UUID studentId);
}
