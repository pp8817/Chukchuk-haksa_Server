package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.FreshManException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chukchuk.haksa.domain.student.dto.StudentSemesterDto.StudentSemesterInfoDto;


//get-semester만을 위한 service
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentSemesterService {
    private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;
    private final UserService userService;

    public UUID getStudentId(String email) { //email로 studentID 얻기
        return userService.getUserId(email);
    }

    public List<SemesterAcademicRecord> getStudentRecord(UUID studentId, Integer year, Integer semester) {
        validateSemesterRequest(year, semester);
        return semesterAcademicRecordRepository
                .findByStudentIdAndYearAndSemester(studentId, year, semester);
    }

    public List<StudentSemesterInfoDto> getStudentSemester(UUID studentId, Integer year, Integer semester) {
        List<SemesterAcademicRecord> records = getStudentRecord(studentId, year, semester);
        int currentYear = LocalDate.now().getYear();
        if (records.isEmpty()) { //신입생 예외처리
                throw new FreshManException("신입생은 학기 기록이 없습니다.");
        }

        return records.stream()
                .map(StudentSemesterInfoDto::from)
                .collect(Collectors.toList());
    }

    private void validateSemesterRequest(Integer year, Integer semester) { //year, semester 값 유효성 검사
        int currentYear = LocalDate.now().getYear();//현재 년도
        if (year < 2000 || year > currentYear) {
            throw new IllegalArgumentException("유효하지 않은 연도입니다.");
        }
        if (semester < 1 || semester > 4) {
            throw new IllegalArgumentException("유효하지 않은 학기입니다.");
        }
    }
}