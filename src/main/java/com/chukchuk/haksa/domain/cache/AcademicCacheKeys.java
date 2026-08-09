package com.chukchuk.haksa.domain.cache;

import java.util.UUID;

/** 학사 데이터 캐시 키를 일관된 형식으로 생성한다. */
public final class AcademicCacheKeys {

  private AcademicCacheKeys() {}

  /**
   * 학생별 학사 요약 캐시 키를 반환한다.
   *
   * @param studentId 학생 식별자
   * @return string
   */
  public static String summary(UUID studentId) {
    return "student:" + studentId + ":summary";
  }

  /**
   * 학생별 학기 목록 캐시 키를 반환한다.
   *
   * @param studentId 학생 식별자
   * @return string
   */
  public static String semesters(UUID studentId) {
    return "student:" + studentId + ":semesters";
  }

  /**
   * 졸업 처리를 수행한다.
   *
   * @param studentId 학생 식별자
   * @return string
   */
  public static String graduation(UUID studentId) {
    return "student:" + studentId + ":graduation";
  }

  /**
   * 졸업 requirements 처리를 수행한다.
   *
   * @param departmentId 학과 식별자
   * @param admissionYear admission 연도
   * @return string
   */
  public static String graduationRequirements(Long departmentId, Integer admissionYear) {
    return "graduation:requirements:" + departmentId + ":" + admissionYear;
  }

  /**
   * 복수전공 졸업요건 캐시 키를 반환한다.
   *
   * @param primaryMajorId 주전공 식별자
   * @param secondaryMajorId 복수전공 식별자
   * @param admissionYear admission 연도
   * @return string
   */
  public static String dualGraduationRequirements(
      Long primaryMajorId, Long secondaryMajorId, Integer admissionYear) {
    return "graduation:dual-requirements:"
        + primaryMajorId
        + ":"
        + secondaryMajorId
        + ":"
        + admissionYear;
  }

  /**
   * 학기 summaries 처리를 수행한다.
   *
   * @param studentId 학생 식별자
   * @return string
   */
  public static String semesterSummaries(UUID studentId) {
    return "student:" + studentId + ":semester-summaries";
  }

  /**
   * 학생 prefix 처리를 수행한다.
   *
   * @param studentId 학생 식별자
   * @return string
   */
  public static String studentPrefix(UUID studentId) {
    return "student:" + studentId + ":";
  }
}
