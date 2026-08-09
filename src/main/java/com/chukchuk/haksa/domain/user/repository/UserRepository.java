package com.chukchuk.haksa.domain.user.repository;

import com.chukchuk.haksa.domain.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/* JpaRepository를 확장, 커스텀 메서드 정의 */
/** 구현체가 제공해야 할 사용자 repository 기능의 계약을 정의한다. */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param email 이메일 값
   * @return 조회
   */
  Optional<User> findByEmail(String email);

  /**
   * 현재 상태가 조건을 충족하는지 반환한다.
   *
   * @param email 이메일 값
   * @return 조건 충족 여부
   */
  boolean existsByEmail(String email);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentCode 학번
   * @return 조회
   */
  Optional<User> findByStudentStudentCode(String studentCode);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param userId 사용자 식별자
   * @return 조회
   */
  @Query(
      """
            SELECT u FROM User u
            LEFT JOIN FETCH u.student s
            LEFT JOIN FETCH s.department
            LEFT JOIN FETCH s.major
            LEFT JOIN FETCH s.secondaryMajor
            WHERE u.id = :userId
      """)
  Optional<User> findProfileByIdWithAssociations(@Param("userId") UUID userId);
}
