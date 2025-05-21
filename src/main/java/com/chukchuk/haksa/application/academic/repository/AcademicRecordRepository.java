package com.chukchuk.haksa.application.academic.repository;

import com.chukchuk.haksa.application.academic.AcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.model.StudentAcademicRecord;
import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.infrastructure.portal.mapper.AcademicRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class AcademicRecordRepository {
    private final StudentRepository studentRepository;
    private final StudentAcademicRecordRepository studentAcademicRecordRepository;
    private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;

    @Transactional
    public void upsertAcademicRecords(AcademicRecord academicRecord, Student student) {
        UUID studentId = student.getId();

        // 1. StudentAcademicRecord upsert
        studentAcademicRecordRepository.findByStudentId(studentId)
                .ifPresentOrElse(
                        existing -> {
                            existing.updateWith(academicRecord.getSummary()); // update 메서드 정의 시
                            student.setAcademicRecord(existing);
                            studentAcademicRecordRepository.save(existing);
                        },
                        () -> {
                            StudentAcademicRecord newRecord = AcademicRecordMapper.toEntity(student, academicRecord.getSummary());
                            student.setAcademicRecord(newRecord);
                            studentAcademicRecordRepository.save(newRecord);
                        }
                );

//         2. SemesterAcademicRecord bulk insert
        List<SemesterAcademicRecord> semesters = academicRecord.getSemesters().stream()
                .map(g -> AcademicRecordMapper.toEntity(student, g))
                .toList();
        semesters.forEach(student::addSemesterRecord);

        semesterAcademicRecordRepository.saveAll(semesters); // 명시적 저장
        // student를 저장하면 cascade 설정 덕분에 academicRecord/semesterRecord도 자동 저장
        studentRepository.save(student);
    }
}