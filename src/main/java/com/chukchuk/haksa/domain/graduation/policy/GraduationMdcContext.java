// 졸업요건 조회 실패에 필요한 Sentry MDC 문맥을 설정하는 유틸리티
package com.chukchuk.haksa.domain.graduation.policy;

import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.global.logging.util.HashUtil;
import org.slf4j.MDC;

public final class GraduationMdcContext {

  private GraduationMdcContext() {}

  public static void bind(
      Student student, Long primaryMajorId, Long secondaryMajorId, int admissionYear) {
    MDC.put("studentCodeHash", HashUtil.sha256Short(student.getStudentCode()));
    MDC.put("admissionYear", String.valueOf(admissionYear));
    MDC.put("departmentId", String.valueOf(primaryMajorId));
    MDC.put(
        "secondaryDepartmentId",
        secondaryMajorId == null ? "NONE" : String.valueOf(secondaryMajorId));
    MDC.put("majorType", secondaryMajorId == null ? "SINGLE" : "DUAL");
  }
}
