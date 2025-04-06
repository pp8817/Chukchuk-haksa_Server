package com.chukchuk.haksa.domain.department.service;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    // 학과 코드로 조회하고, 없으면 새로 생성
    @Transactional
    public Department getOrCreateDepartment(String departmentCode, String establishedDepartmentName) {
        return departmentRepository.findByDepartmentCode(departmentCode)
                .orElseGet(() -> {
                    Department department = new Department(departmentCode, establishedDepartmentName);
                    return departmentRepository.save(department);
                });
    }
}
