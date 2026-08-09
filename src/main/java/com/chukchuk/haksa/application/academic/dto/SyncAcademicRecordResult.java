package com.chukchuk.haksa.application.academic.dto;

/** 척척학사의 sync 학사 record 처리 결과를 전달한다. */
public class SyncAcademicRecordResult {
  private final boolean isSuccess;
  private final String error;

  /**
   * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
   *
   * @param isSuccess is success 여부
   * @param error 오류 값
   */
  public SyncAcademicRecordResult(boolean isSuccess, String error) {
    this.isSuccess = isSuccess;
    this.error = error;
  }

  /**
   * 성공 결과를 생성한다.
   *
   * @return sync 학사 record 결과
   */
  public static SyncAcademicRecordResult success() {
    return new SyncAcademicRecordResult(true, null);
  }

  /**
   * 실패 사유를 포함한 결과를 생성한다.
   *
   * @param error 오류 값
   * @return sync 학사 record 결과
   */
  public static SyncAcademicRecordResult failure(String error) {
    return new SyncAcademicRecordResult(false, error);
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public String getError() {
    return error;
  }
}
