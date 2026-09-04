// 학생 졸업 진행 상태 중 외국어 인증 정보를 동기화하고 조회하는 서비스

package com.chukchuk.haksa.domain.graduation.service;

import com.chukchuk.haksa.domain.cache.AcademicCache;
import com.chukchuk.haksa.domain.graduation.model.StudentGraduationProgress;
import com.chukchuk.haksa.domain.graduation.repository.StudentGraduationProgressRepository;
import com.chukchuk.haksa.domain.student.model.Student;
import com.chukchuk.haksa.domain.student.repository.StudentRepository;
import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.exception.type.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 학생의 외국어 인증 충족 여부를 저장하고 조회한다. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentGraduationProgressService {

  private final StudentGraduationProgressRepository repository;
  private final StudentRepository studentRepository;
  private final AcademicCache academicCache;

  /**
   * 학생의 어학 인증 충족 상태를 동기화한다.
   *
   * @param student 기록의 소유 학생
   * @param languageCertFulfilled 어학 인증 충족 여부
   */
  @Transactional
  public void syncLanguageCert(Student student, Boolean languageCertFulfilled) {
    if (languageCertFulfilled == null) {
      return;
    }

    UUID studentId = student.getId();
    StudentGraduationProgress progress =
        repository
            .findByStudentId(studentId)
            .map(
                existing -> {
                  existing.updateLanguageCert(languageCertFulfilled);
                  return existing;
                })
            .orElseGet(
                () ->
                    StudentGraduationProgress.createForLanguageCert(
                        student, languageCertFulfilled));

    repository.save(progress);
    academicCache.deleteAllByStudentId(studentId);
  }

  /**
   * 학생의 외국어 인증 충족 여부를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 저장된 충족 여부가 있으면 포함한 선택값
   */
  public Optional<Boolean> getLanguageCertFulfilled(UUID studentId) {
    return repository
        .findByStudentId(studentId)
        .map(StudentGraduationProgress::getLanguageCertFulfilled);
  }

  /**
   * 편입생의 학생별 등록 학기와 졸업심사 결과를 수동으로 저장한다.
   *
   * @param studentId 대상 학생 식별자
   * @param registeredSemesters 편입 후 등록 학기 수
   * @param graduationReviewFulfilled 학과 졸업심사 통과 여부
   * @throws EntityNotFoundException 학생이 없는 경우
   * @throws IllegalArgumentException 편입생이 아닌 경우
   */
  @Transactional
  public void updateTransferManualReview(
      UUID studentId, Integer registeredSemesters, Boolean graduationReviewFulfilled) {
    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.STUDENT_NOT_FOUND));
    if (!student.isTransferStudent()) {
      throw new IllegalArgumentException("편입생만 수동 졸업진단 정보를 저장할 수 있습니다.");
    }

    student.updateTransferRegisteredSemesters(registeredSemesters);
    StudentGraduationProgress progress =
        repository
            .findByStudentId(studentId)
            .orElseGet(() -> StudentGraduationProgress.createForManualReview(student));
    progress.updateGraduationReview(graduationReviewFulfilled);
    repository.save(progress);
    academicCache.deleteAllByStudentId(studentId);
  }

  /**
   * 학생별 졸업심사 통과 여부를 조회한다.
   *
   * @param studentId 대상 학생 식별자
   * @return 저장된 졸업심사 결과
   */
  public Optional<Boolean> getGraduationReviewFulfilled(UUID studentId) {
    return repository
        .findByStudentId(studentId)
        .map(StudentGraduationProgress::getGraduationReviewFulfilled);
  }
}
