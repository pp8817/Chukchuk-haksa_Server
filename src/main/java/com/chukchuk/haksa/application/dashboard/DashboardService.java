package com.chukchuk.haksa.application.dashboard;

import com.chukchuk.haksa.application.dashboard.dto.DashboardResponse;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.student.dto.StudentDto;
import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final UserService userService;
    private final StudentService studentService;
    private final StudentAcademicRecordService studentAcademicRecordService;

    // 대시보드 정보 조회
    public DashboardResponse getDashboard(String email) {
        User user = userService.getUserByEmail(email);
        UUID userId = user.getId();

        StudentDto.StudentInfoDto studentInfo = studentService.getStudentInfo(userId);
        StudentAcademicRecordDto.AcademicSummaryDto academicSummary = studentAcademicRecordService.getAcademicSummary(userId);
        int currentSemester = getCurrentSemester(studentInfo.gradeLevel(), studentInfo.completedSemesters());
        String lastSyncedAt = user.getLastSyncedAt() != null ? user.getLastSyncedAt().toString() : "";

        return DashboardResponse.from(studentInfo, academicSummary, lastSyncedAt, currentSemester);
    }

    public static int getCurrentSemester(Integer gradeLevel, Integer completedSemesters) {

        int safeGradeLevel = (gradeLevel != null) ? gradeLevel : 0;
        int safeCompletedSemesters = (completedSemesters != null) ? completedSemesters : 0;

        // 1️⃣ 해당 학년 이전에 완료해야 하는 최소 학기 수
        int expectedCompleted = (safeGradeLevel - 1) * 2;

        // 2️⃣ 만약 completedSemesters가 예상보다 낮으면 최소값으로 간주 (방학 중 업데이트가 안된 경우 대비)
        int effectiveCompleted = Math.max(safeCompletedSemesters, expectedCompleted);

        // 3️⃣ 현재 학기 계산
        int currentSemester = effectiveCompleted - expectedCompleted + 1;

        // 4️⃣ currentSemester는 1 또는 2만 가능하도록 보정
        if (currentSemester < 1) {
            return 1;
        } else if (currentSemester > 2) {
            return 2;
        }
        return currentSemester;
    }
}
