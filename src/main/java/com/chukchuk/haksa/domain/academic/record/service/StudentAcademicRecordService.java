package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.domain.graduation.repository.GraduationQueryRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.service.StudentService;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentAcademicRecordService {

    private final StudentAcademicRecordRepository studentAcademicRecordRepository;
    private final GraduationQueryRepository graduationQueryRepository;
    private final StudentService studentService;

    public StudentAcademicRecordDto.AcademicSummaryDto getAcademicSummary(UUID userId) {
        StudentAcademicRecord studentAcademicRecord = getStudentAcademicRecordByStudentId(userId);

        Student student = studentService.getStudentById(userId);

        // 전공 코드가 없는 학과도 있으므로 majorId가 없으면 departmentId를 사용
        Long effectiveDepartmentId = student.getMajor() != null ? student.getMajor().getId() : student.getDepartment().getId();
        Integer admissionYear = student.getAcademicInfo().getAdmissionYear();

        Integer totalRequiredGraduationCredits = graduationQueryRepository.getTotalRequiredGraduationCredits(effectiveDepartmentId, admissionYear);

        return StudentAcademicRecordDto.AcademicSummaryDto.from(studentAcademicRecord, totalRequiredGraduationCredits);
    }

    public StudentAcademicRecord getStudentAcademicRecordByStudentId(UUID id) {
        return studentAcademicRecordRepository.findByStudentId(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_ACADEMIC_RECORD_NOT_FOUND));
    }
}
