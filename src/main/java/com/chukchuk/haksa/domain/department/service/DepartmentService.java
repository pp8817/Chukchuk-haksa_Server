package com.chukchuk.haksa.domain.department.service;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 학과 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
  private final DepartmentRepository departmentRepository;

  // 학과 코드로 조회하고, 없으면 새로 생성
  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param departmentCode 학과 코드
   * @param establishedDepartmentName established 학과 이름
   * @return 조회
   */
  @Transactional
  public Department getOrCreateDepartment(String departmentCode, String establishedDepartmentName) {
    return departmentRepository
        .findByDepartmentCode(departmentCode)
        .orElseGet(
            () -> {
              Department department = new Department(departmentCode, establishedDepartmentName);
              return departmentRepository.save(department);
            });
  }
}
