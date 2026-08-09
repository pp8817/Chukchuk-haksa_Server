// dev 테스트 어드민 API 문서 인터페이스를 정의한다

package com.chukchuk.haksa.domain.admin.controller.docs;

import com.chukchuk.haksa.domain.admin.dto.AdminTestDto;
import com.chukchuk.haksa.global.common.response.MessageOnlyResponse;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

/** 척척학사의 admin test controller docs 기능의 계약을 정의한다. */
@Tag(name = "Admin Test", description = "dev 전용 프론트 테스트 데이터 조작 API")
public interface AdminTestControllerDocs {

  /**
   * 척척학사의 create test 사용자 대상을 생성한다.
   *
   * @param request 요청 정보
   * @return 생성된
   */
  @Operation(summary = "테스트 계정 생성", description = "dev 환경에서 테스트 계정을 생성하고 JWT 토큰을 발급합니다.")
  ResponseEntity<SuccessResponse<AdminTestDto.TestUserResponse>> createTestUser(
      AdminTestDto.CreateTestUserRequest request);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @return 조회
   */
  @Operation(summary = "테스트 조작 옵션 조회", description = "dev 환경에서 토큰 없이 학과와 졸업요건 영역 선택지를 조회합니다.")
  ResponseEntity<SuccessResponse<AdminTestDto.TestOptionsResponse>> getTestOptions();

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param keyword 검색어
   * @return 조회
   */
  @Operation(summary = "학과 검색", description = "dev 환경에서 토큰 없이 학과 코드와 학과명으로 학과 선택지를 검색합니다.")
  ResponseEntity<SuccessResponse<List<AdminTestDto.DepartmentOption>>> searchDepartments(
      String keyword);

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param keyword 검색어
   * @param area area 값
   * @param year 연도
   * @param semester 학기 값
   * @param departmentId 학과 식별자
   * @return 조회
   */
  @Operation(summary = "강의 후보 조회", description = "dev 환경에서 토큰 없이 테스트 데이터에 추가할 개설강의 후보를 검색합니다.")
  ResponseEntity<SuccessResponse<List<AdminTestDto.CourseOfferingOption>>> searchCourseOfferings(
      String keyword,
      com.chukchuk.haksa.domain.course.model.FacultyDivision area,
      Integer year,
      Integer semester,
      Long departmentId);

  /**
   * 척척학사의 update 졸업 courses 대상을 갱신한다.
   *
   * @param userDetails 사용자 상세 정보
   * @param request 요청 정보
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(summary = "현재 계정 강의 데이터 수정", description = "현재 인증 계정의 졸업요건 강의 데이터를 추가하거나 삭제합니다.")
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> updateGraduationCourses(
      CustomUserDetails userDetails, AdminTestDto.UpdateGraduationCoursesRequest request);

  /**
   * 척척학사의 update 전공 대상을 갱신한다.
   *
   * @param userDetails 사용자 상세 정보
   * @param request 요청 정보
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(summary = "현재 계정 전공 상태 수정", description = "현재 인증 계정의 주전공과 복수전공 상태를 수정합니다.")
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> updateMajor(
      CustomUserDetails userDetails, AdminTestDto.UpdateMajorRequest request);

  /**
   * 현재 관리자 테스트 계정의 학사 데이터를 초기화한다.
   *
   * @param userDetails 사용자 상세 정보
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "현재 계정 테스트 데이터 초기화",
      description = "현재 인증 계정의 수강 데이터와 전공 상태를 프론트 테스트 기준 상태로 초기화합니다.")
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> resetCurrentAccount(
      CustomUserDetails userDetails);

  /**
   * 척척학사의 create test 과목 대상을 생성한다.
   *
   * @param userDetails 사용자 상세 정보
   * @param request 요청 정보
   * @return 생성된
   */
  @Operation(
      summary = "현재 계정 테스트 강의 생성",
      description = "테스트 강의와 개설강의를 만들고 현재 인증 계정의 수강 데이터에 바로 추가합니다.")
  @SecurityRequirement(name = "bearerAuth")
  ResponseEntity<SuccessResponse<AdminTestDto.TestCourseResponse>> createTestCourse(
      CustomUserDetails userDetails, AdminTestDto.CreateTestCourseRequest request);

  /**
   * 척척학사의 set lecture evaluation empty 학기 대상을 설정한다.
   *
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "dev 강의평가 empty-semester 상태 세팅",
      description = "고정 프론트 테스트 계정의 target 학기 평가/수강/학기 row를 삭제합니다.")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> setLectureEvaluationEmptySemester();

  /**
   * 척척학사의 set lecture evaluation not released 대상을 설정한다.
   *
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "dev 강의평가 NOT_RELEASED 상태 세팅",
      description = "고정 프론트 테스트 계정의 target 학기를 성적 미공개 상태로 재구성합니다.")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> setLectureEvaluationNotReleased();

  /**
   * 척척학사의 set lecture evaluation pending 대상을 설정한다.
   *
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "dev 강의평가 PENDING 상태 세팅",
      description = "고정 프론트 테스트 계정의 target 학기를 강의평가 대기 상태로 재구성합니다.")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> setLectureEvaluationPending();

  /**
   * 척척학사의 set lecture evaluation skipped 대상을 설정한다.
   *
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "dev 강의평가 SKIPPED 상태 세팅",
      description = "고정 프론트 테스트 계정의 target 학기를 강의평가 건너뛰기 상태로 재구성합니다.")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> setLectureEvaluationSkipped();

  /**
   * 척척학사의 set lecture evaluation completed 대상을 설정한다.
   *
   * @return 응답 entity success 응답 메시지 only 응답 결과
   */
  @Operation(
      summary = "dev 강의평가 COMPLETED 상태 세팅",
      description = "고정 프론트 테스트 계정의 target 학기를 강의평가 완료 상태로 재구성합니다.")
  ResponseEntity<SuccessResponse<MessageOnlyResponse>> setLectureEvaluationCompleted();
}
