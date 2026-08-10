package com.chukchuk.haksa.domain.department.repository;

import com.chukchuk.haksa.domain.department.model.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** 학과 코드·표준 학과명·검색어로 학과를 조회하는 저장소다. */
public interface DepartmentRepository extends JpaRepository<Department, Long> {
  /**
   * 학과를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param departmentCode 학과 코드
   * @return 조건에 일치하는 학과가 있으면 포함한 선택값
   */
  Optional<Department> findByDepartmentCode(String departmentCode); // departmentCode로 학과 찾기

  /**
   * 학과를 메서드에 지정된 식별 조건과 정렬 기준으로 조회한다.
   *
   * @param establishedDepartmentName established 학과 이름
   * @return 조건에 일치하는 학과 목록
   */
  List<Department> findAllByEstablishedDepartmentName(String establishedDepartmentName);

  /**
   * 학과에 대한 LOWER 작업을 수행한다.
   *
   * @param keyword 검색할 과목 코드 또는 이름
   * @return 처리된 학과
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
