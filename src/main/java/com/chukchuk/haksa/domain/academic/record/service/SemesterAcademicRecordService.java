package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.BusinessException;
import com.chukchuk.haksa.global.exception.EntityNotFoundException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto.SemesterGradeDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemesterAcademicRecordService {
    private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;
    private final UserService userService;

    /* 특정 학생의 특정 학기 성적 조회 */
    public SemesterGradeDto getSemesterGradesByYearAndSemester(UUID studentId, Integer year, Integer semester) {
        return SemesterGradeDto.from(findSemesterRecordsByYearAndSemester(studentId, year, semester));
    }

    /* 특정 학생의 전체 학기 성적 조회 (최신순 정렬) */
    public List<SemesterGradeDto> getAllSemesterGrades(UUID studentId) {
        return findAllSemesterRecords(studentId).stream()
                .map(SemesterGradeDto::from)
                .collect(Collectors.toList());
    }

    /* 특정 학생의 특정 학기 성적 조회 (없으면 예외 발생) */
    private SemesterAcademicRecord findSemesterRecordsByYearAndSemester(UUID studentId, Integer year, Integer semester) {
        return semesterAcademicRecordRepository.findByStudentIdAndYearAndSemester(studentId, year, semester)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SEMESTER_RECORD_NOT_FOUND));
    }

    /* 특정 학생의 전체 학기 성적 조회 (최신순 정렬, 없으면 예외 발생) */
    private List<SemesterAcademicRecord> findAllSemesterRecords(UUID studentId) {
        List<SemesterAcademicRecord> records = semesterAcademicRecordRepository.findByStudentIdOrderByYearDescSemesterDesc(studentId);

        if (records.isEmpty()) {
            throw new EntityNotFoundException(ErrorCode.SEMESTER_RECORD_EMPTY);
        }

        return records;
    }

    /* 학생의 학기 정보 조회 */
    public List<StudentSemesterDto.StudentSemesterInfoDto> getSemestersByStudentEmail(UUID userId) {

        return findSemestersByStudent(userId).stream()
                .map(StudentSemesterDto.StudentSemesterInfoDto::from)
                .collect(Collectors.toList());
    }

    /* 특정 학생의 학기 정보 조회 (신입생 예외 처리) */
    private List<SemesterAcademicRecord> findSemestersByStudent(UUID studentId) {
        List<SemesterAcademicRecord> records = semesterAcademicRecordRepository.findByStudentId(studentId);

        if (records.isEmpty()) {
            throw new BusinessException(ErrorCode.FRESHMAN_NO_SEMESTER);
        }

        return records;
    }

}
