package com.chukchuk.haksa.application.portal;

import com.chukchuk.haksa.domain.auth.service.AuthService;
import com.chukchuk.haksa.domain.department.model.Department;
import com.chukchuk.haksa.domain.department.service.DepartmentService;
import com.chukchuk.haksa.domain.user.model.StudentInitializationDataType;
import com.chukchuk.haksa.domain.student.model.StudentStatus;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserPortalConnectionRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.infrastructure.portal.model.InitializePortalConnectionResult;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.model.PortalStudentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.chukchuk.haksa.infrastructure.portal.model.InitializePortalConnectionResult.*;

/* 포털 연동 초기화 유스케이스 실행 */
@Service
@RequiredArgsConstructor
public class InitializePortalConnectionService {

    private final DepartmentService departmentService;
    private final UserPortalConnectionRepository userPortalConnectionRepository;
    private final UserService userService;
    private final AuthService authService;

    public InitializePortalConnectionResult executeWithPortalData(PortalData portalData) {
        try {
            PortalStudentInfo student = portalData.student();
            UUID userId = UUID.fromString(authService.getAuthenticatedUserId());

            // 사용자 조회 및 포털 연동 여부 확인
            User user = userService.getUserById(userId);
            if (user.getPortalConnected()) {
                return failure("이미 포털 계정과 연동된 사용자입니다.");
            }

            // 학과 및 전공 정보 설정
            Department department = departmentService.getOrCreateDepartment(
                    student.department().code(), student.department().name());
            Department major = student.major().code() != null
                    ? departmentService.getOrCreateDepartment(
                    student.major().code(), student.major().name())
                    : null;
            Department secondaryMajor = student.secondaryMajor() != null
                    ? departmentService.getOrCreateDepartment(
                    student.secondaryMajor().code(), student.secondaryMajor().name())
                    : null;

            // StudentInitializationDataType 생성
            if (department == null) {
                throw new IllegalStateException("학과/전공 정보 초기화 실패");
            }

            // 학과 및 전공 정보 포함된 StudentInitializationDataType 생성
            StudentInitializationDataType studentData = StudentInitializationDataType.builder()
                    .studentCode(student.studentCode())
                    .name(student.name())
                    .department(department)  // 학과 객체
                    .major(major)            // 전공 객체
                    .secondaryMajor(secondaryMajor) // 복수 전공 객체
                    .admissionYear(student.admission().year())
                    .semesterEnrolled(student.admission().semester())
                    .isTransferStudent(student.admission().type().contains("편입"))
                    .isGraduated(student.status().equals(StudentStatus.졸업.name()))
                    .status(StudentStatus.valueOf(student.status()))
                    .gradeLevel(student.academic().gradeLevel())
                    .completedSemesters(student.academic().completedSemesters())
                    .admissionType(student.admission().type())
                    .build();


            // 포털 연동 초기화
            userPortalConnectionRepository.initializePortalConnection(userId, studentData);

            StudentInfo studentInfo = new StudentInfo(
                    student.name(),
                    "수원대학교",
                    major != null ? major.getEstablishedDepartmentName() : department.getEstablishedDepartmentName(),
                    student.studentCode(),
                    student.academic().gradeLevel(),
                    student.status(),
                    student.academic().completedSemesters() % 2 == 0 ? 1 : 2
            );

            return success(student.studentCode(), studentInfo);
        } catch (Exception e) {
            return failure(
                    e.getMessage() != null ? e.getMessage() : "포털 연동 중 오류가 발생했습니다.");
        }
    }
}