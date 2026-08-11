// 졸업요건 학과 판별 실패 시 Sentry 문맥을 검증하는 테스트

package com.chukchuk.haksa.domain.graduation.policy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.repository.DepartmentRepository;
import com.chukchuk.haksa.domain.graduation.repository.GraduationQueryRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.CommonException;
import com.chukchuk.haksa.global.logging.util.HashUtil;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class GraduationMajorResolverTest {

  @Mock private GraduationQueryRepository graduationQueryRepository;
  @Mock private DepartmentRepository departmentRepository;
  @InjectMocks private GraduationMajorResolver resolver;

  @AfterEach
  void clearMdc() {
    MDC.clear();
  }

  @Test
  @DisplayName("졸업요건이 없으면 학번 해시와 학과 문맥을 남긴다")
  void logsStudentHashAndDepartmentContextWhenRequirementsAreMissing() {
    Student student = mock(Student.class);
    Department department = mock(Department.class);
    when(student.getStudentCode()).thenReturn("17015080");
    when(student.getDepartment()).thenReturn(department);
    when(department.getId()).thenReturn(38L);
    when(graduationQueryRepository.getAreaRequirementsWithCache(38L, 2017)).thenReturn(List.of());

    assertThatThrownBy(() -> resolver.resolve(student, 2017))
        .isInstanceOf(CommonException.class)
        .satisfies(
            error ->
                assertThat(((CommonException) error).getCode())
                    .isEqualTo(ErrorCode.GRADUATION_REQUIREMENTS_DATA_NOT_FOUND.code()));

    assertThat(MDC.get("studentCodeHash")).isEqualTo(HashUtil.sha256Short("17015080"));
    assertThat(MDC.get("admissionYear")).isEqualTo("2017");
    assertThat(MDC.get("departmentId")).isEqualTo("38");
    assertThat(MDC.get("secondaryDepartmentId")).isEqualTo("NONE");
    assertThat(MDC.get("majorType")).isEqualTo("SINGLE");
    assertThat(MDC.get("student_code")).isNull();
  }
}
