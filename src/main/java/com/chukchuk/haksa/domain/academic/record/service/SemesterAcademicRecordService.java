package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import com.chukchuk.haksa.global.exception.FreshmanException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chukchuk.haksa.domain.academic.record.dto.SemesterAcademicRecordDto.SemesterGradeDto;

@Service
@RequiredArgsConstructor
public class SemesterAcademicRecordService {
    private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;
    private final UserService userService;

    public UUID getStudentId(String email) {
        return userService.getUserId(email);
    }

    public List<SemesterAcademicRecord> getSemesterRecords(UUID studentId, Integer year, Integer semester) {
        return semesterAcademicRecordRepository
                .findByStudentIdAndYearAndSemester(studentId, year, semester);
    }

    public List<SemesterGradeDto> getSemesterGrades(String email, Integer year, Integer semester) {
        UUID studentId = getStudentId(email);
        List<SemesterAcademicRecord> records = getSemesterRecords(studentId, year, semester);

        if (isFreshman(studentId)) {
            throw new FreshmanException("신입생은 학기 기록이 없습니다.");
        }

        if (records.isEmpty()) {
            throw new DataNotFoundException("해당 학기에 대한 학기 기록이 존재하지 않습니다.");
        }
        validateSemesterRequest(year, semester);

        return records.stream()
                .map(SemesterGradeDto::from)
                .collect(Collectors.toList());
    }

    private void validateSemesterRequest(Integer year, Integer semester) {
        if (year < 2000 || year > 2025) {
            throw new IllegalArgumentException("유효하지 않은 연도입니다.");
        }
        if (semester < 1 || semester > 4) {
            throw new IllegalArgumentException("유효하지 않은 학기입니다.");
        }
    }

    private boolean isFreshman(UUID studentId) {
        return semesterAcademicRecordRepository.findByStudentId(studentId).isEmpty();
    }
}
