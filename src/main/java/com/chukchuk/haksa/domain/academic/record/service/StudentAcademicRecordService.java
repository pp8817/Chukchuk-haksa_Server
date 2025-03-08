package com.chukchuk.haksa.domain.academic.record.service;

import com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentAcademicRecordService {

    private final StudentAcademicRecordRepository studentAcademicRecordRepository;

    public StudentAcademicRecordDto.AcademicSummaryDto getAcademicSummary(UUID userId) {
        StudentAcademicRecord studentAcademicRecord = getStudentAcademicRecordByStudentId(userId);
        return StudentAcademicRecordDto.AcademicSummaryDto.from(studentAcademicRecord);
    }

    public StudentAcademicRecord getStudentAcademicRecordByStudentId(UUID id) {
        return studentAcademicRecordRepository.findByStudentId(id)
                .orElseThrow(() -> new DataNotFoundException("해당 학생의 학적 정보가 존재하지 않습니다."));
    }
}
