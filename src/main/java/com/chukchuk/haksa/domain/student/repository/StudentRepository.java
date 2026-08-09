package com.chukchuk.haksa.domain.student.repository;

import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** 구현체가 제공해야 할 학생 repository 기능의 계약을 정의한다. */
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param user 사용자 값
   * @return 조회
   */
  Optional<Student> findByUser(User user);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentCode 학번
   * @return 조회
   */
  Optional<Student> findByStudentCode(String studentCode);

  /**
   * 현재 상태를 요청 내용에 맞게 갱신한다.
   *
   * @param studentId 학생 식별자
   * @param targetGpa target gpa 값
   */
  @Modifying(clearAutomatically = true)
  @Query("UPDATE Student s SET s.targetGpa = :targetGpa WHERE s.id = :studentId")
  void updateTargetGpaByStudentId(
      @Param("studentId") UUID studentId, @Param("targetGpa") Double targetGpa);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  @Query(
      "SELECT s FROM Student s "
          + "JOIN FETCH s.user u "
          + "LEFT JOIN FETCH s.department d "
          + "LEFT JOIN FETCH s.major m "
          + "WHERE s.id = :studentId")
  Optional<Student> findProfileByIdWithAssociations(@Param("studentId") UUID studentId);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  @Query(
      "SELECT s FROM Student s JOIN s.user u WHERE u.id = :userId AND "
          + "(u.portalConnected = false OR u.portalConnected IS NULL)")
  Optional<Student> findPortalPendingStudent(@Param("userId") UUID userId);
}
