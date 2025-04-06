package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.service.DepartmentService;
import com.chukchuk.haksa.domain.student.model.StudentStatus;
import com.chukchuk.haksa.domain.user.model.StudentInitializationDataType;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserPortalConnectionRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.model.PortalStudentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshPortalConnectionService {

    private final DepartmentService departmentService;
    private final UserPortalConnectionRepository userPortalConnectionRepository;
    private final UserService userService;

    public boolean executeWithPortalData(UUID userId, PortalData portalData) {
        try {
            User user = userService.getUserById(userId);

            // 포털 연동되지 않은 사용자는 거부
            if (!user.getPortalConnected()) {
                throw new IllegalStateException("아직 포털 계정과 연동되지 않은 사용자입니다.");
            }

            PortalStudentInfo student = portalData.student();

            // 학과 및 전공 정보 설정
            Department department = departmentService.getOrCreateDepartment(
                    student.department().code(), student.department().name());
            Department major = student.major().code() != null
                    ? departmentService.getOrCreateDepartment(student.major().code(), student.major().name())
                    : null;
            Department secondaryMajor = student.secondaryMajor() != null
                    ? departmentService.getOrCreateDepartment(student.secondaryMajor().code(), student.secondaryMajor().name())
                    : null;

            if (department == null) {
                throw new IllegalStateException("학과/전공 정보 초기화 실패");
            }

            StudentInitializationDataType studentData = StudentInitializationDataType.builder()
                    .studentCode(student.studentCode())
                    .name(student.name())
                    .department(department)
                    .major(major)
                    .secondaryMajor(secondaryMajor)
                    .admissionYear(student.admission().year())
                    .semesterEnrolled(student.admission().semester())
                    .isTransferStudent(student.admission().type().contains("편입"))
                    .isGraduated(student.status().equals(StudentStatus.졸업.name()))
                    .status(StudentStatus.valueOf(student.status()))
                    .gradeLevel(student.academic().gradeLevel())
                    .completedSemesters(student.academic().completedSemesters())
                    .admissionType(student.admission().type())
                    .build();

            // 포털 정보 갱신 (기존 initialize와 다른 포인트)
            userPortalConnectionRepository.refreshPortalConnection(user, studentData);

            return true;
        } catch (Exception e) {
            // 실패 로깅 또는 예외 핸들링
            return false;
        }
    }
}