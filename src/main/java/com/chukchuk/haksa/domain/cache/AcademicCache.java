package com.chukchuk.haksa.domain.cache;

import static com.chukchuk.haksa.domain.academic.record.dto.StudentAcademicRecordDto.AcademicSummaryResponse;

import com.chukchuk.haksa.domain.academic.record.dto.SemesterSummaryResponse;
import com.chukchuk.haksa.domain.graduation.dto.AreaRequirementDto;
import com.chukchuk.haksa.domain.graduation.dto.GraduationProgressResponse;
import com.chukchuk.haksa.domain.student.dto.StudentSemesterDto;
import java.util.List;
import java.util.UUID;

/** 학사 캐시 기능의 계약을 정의한다. */
public interface AcademicCache {

  /**
   * 척척학사의 set 학사 summary 대상을 설정한다.
   *
   * @param studentId 학생 식별자
   * @param summary summary 값
   */
  void setAcademicSummary(UUID studentId, AcademicSummaryResponse summary);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  AcademicSummaryResponse getAcademicSummary(UUID studentId);

  /**
   * 척척학사의 set 학기 list 대상을 설정한다.
   *
   * @param studentId 학생 식별자
   * @param list list 값
   */
  void setSemesterList(UUID studentId, List<StudentSemesterDto.StudentSemesterInfoResponse> list);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  List<StudentSemesterDto.StudentSemesterInfoResponse> getSemesterList(UUID studentId);

  /**
   * 척척학사의 set 졸업 progress 대상을 설정한다.
   *
   * @param studentId 학생 식별자
   * @param progress progress 값
   */
  void setGraduationProgress(UUID studentId, GraduationProgressResponse progress);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  GraduationProgressResponse getGraduationProgress(UUID studentId);

  /**
   * 척척학사의 set 졸업 requirements 대상을 설정한다.
   *
   * @param departmentId 학과 식별자
   * @param admissionYear admission 연도
   * @param requirements requirements 값
   */
  void setGraduationRequirements(
      Long departmentId, Integer admissionYear, List<AreaRequirementDto> requirements);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param departmentId 학과 식별자
   * @param admissionYear admission 연도
   * @return 조회
   */
  List<AreaRequirementDto> getGraduationRequirements(Long departmentId, Integer admissionYear);

  /**
   * 척척학사의 set dual 전공 requirements 대상을 설정한다.
   *
   * @param primaryMajorId 주전공 식별자
   * @param secondaryMajorId 복수전공 식별자
   * @param admissionYear admission 연도
   * @param requirements requirements 값
   */
  void setDualMajorRequirements(
      Long primaryMajorId,
      Long secondaryMajorId,
      Integer admissionYear,
      List<AreaRequirementDto> requirements);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param primaryMajorId 주전공 식별자
   * @param secondaryMajorId 복수전공 식별자
   * @param admissionYear admission 연도
   * @return 조회
   */
  List<AreaRequirementDto> getDualMajorRequirements(
      Long primaryMajorId, Long secondaryMajorId, Integer admissionYear);

  /**
   * 척척학사의 set 학기 summaries 대상을 설정한다.
   *
   * @param studentId 학생 식별자
   * @param list list 값
   */
  void setSemesterSummaries(UUID studentId, List<SemesterSummaryResponse> list);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param studentId 학생 식별자
   * @return 조회
   */
  List<SemesterSummaryResponse> getSemesterSummaries(UUID studentId);

  /**
   * 척척학사의 delete all by 학생 id 대상을 삭제한다.
   *
   * @param studentId 학생 식별자
   */
  void deleteAllByStudentId(UUID studentId);
}
