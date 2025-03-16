package com.chukchuk.haksa.domain.academic.record.repository;

import com.chukchuk.haksa.domain.academic.record.model.SemesterAcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SemesterAcademicRecordRepository extends JpaRepository<SemesterAcademicRecord, UUID> {

    Optional<SemesterAcademicRecord> findByStudentIdAndYearAndSemester(UUID studentId, Integer year, Integer semester);

    List<SemesterAcademicRecord> findByStudentId(UUID studentId); //studentID로 data 얻어오기

    List<SemesterAcademicRecord> findByStudentIdOrderByYearDescSemesterDesc(UUID studentId);
}
