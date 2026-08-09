// dev 테스트 어드민 API 요청과 응답 DTO를 정의한다

package com.chukchuk.haksa.domain.admin.dto;

import com.chukchuk.haksa.domain.course.model.FacultyDivision;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

/** 계층 간 전달할 데이터를 표현한다. */
public class AdminTestDto {

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param name 이름
   * @param departmentId 학과 식별자
   * @param majorId 전공 id 식별자
   * @param secondaryMajorDepartmentId secondary 전공 학과 식별자
   * @param admissionYear admission 연도
   * @param isPortalLinked is 포털 linked 여부
   */
  @Schema(description = "테스트 계정 생성 요청")
  public record CreateTestUserRequest(
      @Schema(description = "테스트 사용자 이름", example = "프론트테스트") String name,
      @Schema(description = "학과 ID", example = "1") Long departmentId,
      @Schema(description = "주전공 학과 ID", example = "1") Long majorId,
      @Schema(description = "복수전공 학과 ID", example = "2") Long secondaryMajorDepartmentId,
      @Schema(description = "입학년도", example = "2024") Integer admissionYear,
      @Schema(description = "포털 연동 여부. 비어 있으면 true로 처리합니다.", example = "true")
          Boolean isPortalLinked) {
    /**
     * 필수 의존성과 초기 상태를 받아 인스턴스를 생성한다.
     *
     * @param name 이름
     * @param departmentId 학과 식별자
     * @param majorId 전공 id 식별자
     * @param secondaryMajorDepartmentId secondary 전공 학과 식별자
     * @param admissionYear admission 연도
     */
    public CreateTestUserRequest(
        String name,
        Long departmentId,
        Long majorId,
        Long secondaryMajorDepartmentId,
        Integer admissionYear) {
      this(name, departmentId, majorId, secondaryMajorDepartmentId, admissionYear, null);
    }
  }

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param userId 사용자 식별자
   * @param studentId 학생 식별자
   * @param email 이메일 값
   * @param studentCode 학번
   * @param accessToken 접근 토큰 값
   * @param refreshToken refresh token 원문
   */
  @Schema(description = "테스트 계정 생성 응답")
  public record TestUserResponse(
      @Schema(description = "사용자 ID") UUID userId,
      @Schema(description = "학생 ID") UUID studentId,
      @Schema(description = "테스트 계정 이메일") String email,
      @Schema(description = "테스트 학번") String studentCode,
      @Schema(description = "Access Token") String accessToken,
      @Schema(description = "Refresh Token") String refreshToken) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param departments departments 값
   * @param graduationAreas 졸업 areas 값
   */
  @Schema(description = "테스트 조작 옵션 응답")
  public record TestOptionsResponse(
      @Schema(description = "학과 목록") List<DepartmentOption> departments,
      @Schema(description = "졸업요건 영역 목록") List<GraduationAreaOption> graduationAreas) {}

  /**
   * 학과 option 데이터를 전달한다.
   *
   * @param id id 식별자
   * @param code code 값
   * @param name 이름
   */
  @Schema(description = "학과 선택지")
  public record DepartmentOption(
      @Schema(description = "학과 ID") Long id,
      @Schema(description = "학과 코드") String code,
      @Schema(description = "학과명") String name) {}

  /**
   * 졸업 area option 데이터를 전달한다.
   *
   * @param code code 값
   * @param name 이름
   */
  @Schema(description = "졸업요건 영역 선택지")
  public record GraduationAreaOption(
      @Schema(description = "영역 코드") String code, @Schema(description = "영역 표시명") String name) {}

  /**
   * 과목 offering search 요청 데이터를 전달한다.
   *
   * @param keyword 검색어
   * @param area area 값
   * @param year 연도
   * @param semester 학기 값
   * @param departmentId 학과 식별자
   */
  @Schema(description = "강의 후보 검색 요청")
  public record CourseOfferingSearchRequest(
      @Schema(description = "과목명 또는 학수번호 검색어") String keyword,
      @Schema(description = "졸업요건 영역") FacultyDivision area,
      @Schema(description = "연도") Integer year,
      @Schema(description = "학기") Integer semester,
      @Schema(description = "학과 ID. 선교처럼 학과 필터가 필요 없는 영역은 생략합니다.") Long departmentId) {}

  /**
   * 과목 offering option 데이터를 전달한다.
   *
   * @param offeringId 과목 개설 식별자
   * @param courseCode 과목 코드
   * @param courseName 과목 이름
   * @param year 연도
   * @param semester 학기 값
   * @param credits 학점
   * @param area area 값
   * @param rawArea raw area 값
   * @param departmentName 학과 이름
   */
  @Schema(description = "강의 후보 선택지")
  public record CourseOfferingOption(
      @Schema(description = "개설강의 ID") Long offeringId,
      @Schema(description = "학수번호") String courseCode,
      @Schema(description = "과목명") String courseName,
      @Schema(description = "연도") Integer year,
      @Schema(description = "학기") Integer semester,
      @Schema(description = "학점") Integer credits,
      @Schema(description = "졸업요건 영역") FacultyDivision area,
      @Schema(description = "포털 원본 영역명") String rawArea,
      @Schema(description = "개설 학과명") String departmentName) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param area area 값
   * @param addOfferingIds add offering ids 식별자
   * @param removeStudentCourseIds remove 학생 과목 ids 식별자
   * @param grade 성적 값
   * @param points 학점
   * @param isRetake is retake 여부
   * @param originalScore 원점수
   */
  @Schema(description = "현재 인증 계정 강의 데이터 수정 요청")
  public record UpdateGraduationCoursesRequest(
      @Schema(description = "수정 대상 졸업요건 영역") FacultyDivision area,
      @Schema(description = "추가할 개설강의 ID 목록") List<Long> addOfferingIds,
      @Schema(description = "삭제할 학생 수강 row ID 목록") List<Long> removeStudentCourseIds,
      @Schema(description = "성적", example = "A+") String grade,
      @Schema(description = "학점", example = "3") Integer points,
      @Schema(description = "재수강 여부", example = "false") Boolean isRetake,
      @Schema(description = "원점수", example = "95") Integer originalScore) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param majorDepartmentId 전공 학과 식별자
   * @param dualMajorEnabled dual 전공 enabled 값
   * @param secondaryMajorDepartmentId secondary 전공 학과 식별자
   */
  @Schema(description = "현재 인증 계정 전공 상태 수정 요청")
  public record UpdateMajorRequest(
      @Schema(description = "주전공 학과 ID") Long majorDepartmentId,
      @Schema(description = "복수전공 사용 여부") boolean dualMajorEnabled,
      @Schema(description = "복수전공 학과 ID") Long secondaryMajorDepartmentId) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param courseCode 과목 코드
   * @param courseName 과목 이름
   * @param area area 값
   * @param departmentId 학과 식별자
   * @param hostDepartment 주관 학과
   * @param year 연도
   * @param semester 학기 값
   * @param credits 학점
   * @param grade 성적 값
   * @param isRetake is retake 여부
   * @param originalScore 원점수
   */
  @Schema(description = "현재 인증 계정 테스트 강의 생성 요청")
  public record CreateTestCourseRequest(
      @Schema(description = "테스트 학수번호. test_ prefix가 없으면 자동으로 붙습니다.") String courseCode,
      @Schema(description = "테스트 과목명") String courseName,
      @Schema(description = "졸업요건 영역") FacultyDivision area,
      @Schema(description = "학과 ID") Long departmentId,
      @Schema(description = "개설 학과명. departmentId가 없을 때 사용할 수 있습니다.") String hostDepartment,
      @Schema(description = "이수 연도") Integer year,
      @Schema(description = "학기") Integer semester,
      @Schema(description = "학점") Integer credits,
      @Schema(description = "성적") String grade,
      @Schema(description = "재수강 여부") Boolean isRetake,
      @Schema(description = "원점수") Integer originalScore) {}

  /**
   * 계층 간 전달할 데이터를 표현한다.
   *
   * @param studentCourseId 학생 과목 식별자
   * @param offeringId 과목 개설 식별자
   * @param courseCode 과목 코드
   * @param courseName 과목 이름
   * @param area area 값
   */
  @Schema(description = "현재 인증 계정 테스트 강의 생성 응답")
  public record TestCourseResponse(
      @Schema(description = "생성된 학생 수강 row ID") Long studentCourseId,
      @Schema(description = "생성된 개설강의 ID") Long offeringId,
      @Schema(description = "생성된 학수번호") String courseCode,
      @Schema(description = "생성된 과목명") String courseName,
      @Schema(description = "졸업요건 영역") FacultyDivision area) {}
}
