package com.chukchuk.haksa.domain.student.service;

import com.chukchuk.haksa.domain.academic.record.repository.SemesterAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentAcademicRecordRepository;
import com.chukchuk.haksa.domain.academic.record.repository.StudentCourseRepository;
import com.chukchuk.haksa.domain.graduation.repository.StudentGraduationProgressRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 탈퇴 학생의 학사 데이터를 제거하고 개인정보를 익명화한다. */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentDeletionService {

  private final StudentAcademicRecordRepository studentAcademicRecordRepository;
  private final SemesterAcademicRecordRepository semesterAcademicRecordRepository;
  private final StudentCourseRepository studentCourseRepository;
  private final StudentGraduationProgressRepository studentGraduationProgressRepository;
  private final StudentRepository studentRepository;

  /**
   * 지정된 데이터를 삭제한다.
   *
   * @param student 기록의 소유 학생
   */
  @Transactional
  public void anonymizeByStudent(Student student) {
    if (student == null) {
      log.warn("[BIZ] student.deletion.skip, reason=unconnected_user");
      return;
    }

    UUID studentId = student.getId();
    if (studentId != null) {
      studentCourseRepository.deleteByStudentId(studentId);
      semesterAcademicRecordRepository.deleteByStudentId(studentId);
      studentAcademicRecordRepository.deleteByStudentId(studentId);
      studentGraduationProgressRepository.deleteByStudentId(studentId);
    }
    student.anonymize();
    studentRepository.save(student);
  }
}
