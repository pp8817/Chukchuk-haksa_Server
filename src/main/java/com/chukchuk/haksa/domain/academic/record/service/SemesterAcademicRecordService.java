package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import com.chukchuk.haksa.global.exception.FreshManException;
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

    public List<SemesterAcademicRecord> getSemesterRecords(UUID studentId, Integer year, Integer semester) {
        return semesterAcademicRecordRepository
                .findByStudentIdAndYearAndSemester(studentId, year, semester);
    }

    public List<SemesterGradeDto> getSemesterGrades(UUID studentId, Integer year, Integer semester) {
        List<SemesterAcademicRecord> records = getSemesterRecords(studentId, year, semester);

        if (records.isEmpty()) {
            throw new DataNotFoundException("학기 성적 데이터를 찾을 수 없습니다.");
        }

        return records.stream()
                .map(SemesterGradeDto::from)
                .collect(Collectors.toList());
    }

    public List<SemesterGradeDto> getSemesterGrades(UUID studentId) {
        List<SemesterAcademicRecord> records = semesterAcademicRecordRepository.findByStudentIdOrderByYearDescSemesterDesc(studentId);


        if (records.isEmpty()) {
            throw new DataNotFoundException("학기 성적 데이터를 찾을 수 없습니다.");
        }

        return records.stream()
                .map(SemesterGradeDto::from)
                .collect(Collectors.toList());
    }


    public List<SemesterAcademicRecord> getStudentRecord(UUID studentId) {
        return semesterAcademicRecordRepository
                .findByStudentId(studentId);
    }

    public List<StudentSemesterDto.StudentSemesterInfoDto> getStudentSemester(String email) {
        UUID studentId = userService.getUserId(email);

        List<SemesterAcademicRecord> records = getStudentRecord(studentId); //email로 studentId 얻어오기
        if (records.isEmpty()) { //신입생 예외처리
            throw new FreshManException("신입생은 학기 기록이 없습니다.");
        }

        return records.stream()
                .map(StudentSemesterDto.StudentSemesterInfoDto::from)
                .collect(Collectors.toList());
    }

}
