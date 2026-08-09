package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 포털 연동 결과 데이터를 표현한다.
 *
 * @param isSuccess is success 여부
 * @param studentId 학생 식별자
 * @param studentInfo 학생 info 값
 * @param error 오류 값
 */
public record PortalConnectionResult(
    boolean isSuccess, String studentId, StudentInfo studentInfo, String error) {
  /**
   * 성공 결과를 생성한다.
   *
   * @param studentId 학생 식별자
   * @param studentInfo 학생 info 값
   * @return 포털 연동 결과
   */
  public static PortalConnectionResult success(String studentId, StudentInfo studentInfo) {
    return new PortalConnectionResult(true, studentId, studentInfo, null);
  }

  /**
   * 실패 사유를 포함한 결과를 생성한다.
   *
   * @param error 오류 값
   * @return 포털 연동 결과
   */
  public static PortalConnectionResult failure(String error) {
    return new PortalConnectionResult(false, null, null, error);
  }

  /**
   * 계층 간 전달할 학생 info 데이터를 표현한다.
   *
   * @param name 이름
   * @param school school 값
   * @param majorName 전공 이름
   * @param studentCode 학번
   * @param gradeLevel 학년
   * @param status 상태
   * @param completedSemesterType completed 학기 type 값
   */
  public record StudentInfo(
      String name,
      String school,
      String majorName,
      String studentCode,
      int gradeLevel,
      String status,
      int completedSemesterType) {}
}
