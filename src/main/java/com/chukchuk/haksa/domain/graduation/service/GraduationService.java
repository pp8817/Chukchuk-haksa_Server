package com.chukchuk.haksa.domain.graduation.service;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.service.SemesterAcademicRecordService;
import com.chukchuk.haksa.domain.academic.record.service.StudentAcademicRecordService;
import com.chukchuk.haksa.domain.graduation.dto.AreaProgressDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressDto;
import com.chukchuk.haksa.domain.graduation.repository.GraduationQueryRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraduationService {

    private final UserService userService;
    private final StudentService studentService;
    private final GraduationQueryRepository graduationQueryRepository;
    private final SemesterAcademicRecordService semesterAcademicRecordService;
    private final StudentAcademicRecordService studentAcademicRecordService;

    /* 졸업 요건, 학사 성적 정보 조회 */
    public GraduationProgressDto getGraduationProgress(UUID userId) {
        Student student = studentService.getStudentById(userId);

        // 전공 코드가 없는 학과도 있으므로 majorId가 없으면 departmentId를 사용
        Long effectiveDepartmentId = student.getMajor() != null ? student.getMajor().getId() : student.getDepartment().getId();

        // 졸업 요건 조회
        List<AreaProgressDto> areaProgress = graduationQueryRepository.getStudentAreaProgress(userId, effectiveDepartmentId, student.getAcademicInfo().getAdmissionYear());

        // 학기별 성적 조회
        List<SemesterAcademicRecordDto.SemesterGradeDto> semesterGrades = semesterAcademicRecordService.getAllSemesterGrades(userId);

        // 학업 성적 요약 조회
        StudentAcademicRecordDto.AcademicSummaryDto academicSummary = studentAcademicRecordService.getAcademicSummary(userId);

        return new GraduationProgressDto(academicSummary, semesterGrades, areaProgress);
    }
}
