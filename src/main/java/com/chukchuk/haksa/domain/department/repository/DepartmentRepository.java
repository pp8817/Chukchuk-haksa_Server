package com.chukchuk.haksa.domain.department.repository;

import com.chukchuk.haksa.domain.department.model.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** 학과 repository 기능의 계약을 정의한다. */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param departmentCode 학과 코드
   * @return 조회
   */
  Optional<Department> findByDepartmentCode(String departmentCode); // departmentCode로 학과 찾기

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param establishedDepartmentName established 학과 이름
   * @return 조회
   */
  List<Department> findAllByEstablishedDepartmentName(String establishedDepartmentName);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param keyword 검색어
   * @return 조회
   */
  @Query(
      """
        SELECT d FROM Department d
        WHERE LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(d.establishedDepartmentName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY d.establishedDepartmentName ASC, d.departmentCode ASC
      """)
  List<Department> searchAdminDepartments(String keyword);
}
