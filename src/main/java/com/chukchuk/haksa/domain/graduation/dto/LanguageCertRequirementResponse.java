// 사용자 학과에 적용되는 외국어 인증 기준을 전달하는 응답 DTO

package com.chukchuk.haksa.domain.graduation.dto;

import com.chukchuk.haksa.domain.graduation.model.LanguageCertMatchStatus;
import com.chukchuk.haksa.domain.graduation.model.LanguageCertRequirement;
import com.chukchuk.haksa.domain.graduation.model.LanguageCertTestType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 계층 간 전달할 데이터를 표현한다.
 *
 * @param departmentCode 학과 코드
 * @param departmentName 학과 이름
 * @param admissionYear admission 연도
 * @param policyGroupKey policy group key 값
 * @param policyGroupName policy group 이름
 * @param matchStatus match 상태
 * @param note note 값
 * @param requirements requirements 값
 */
@Schema(description = "외국어 인증 기준 응답")
public record LanguageCertRequirementResponse(
    @Schema(description = "기준 조회에 사용된 학과 코드", example = "2000514") String departmentCode,
    @Schema(description = "기준 조회에 사용된 학과명", example = "컴퓨터SW") String departmentName,
    @Schema(description = "입학년도", example = "2021") Integer admissionYear,
    @Schema(description = "외국어 인증 정책 그룹 키", example = "ICT_OTHER", nullable = true)
        String policyGroupKey,
    @Schema(description = "외국어 인증 정책 그룹명", example = "ICT융합대학 그외학부", nullable = true)
        String policyGroupName,
    @Schema(description = "학과-정책 매핑 상태", implementation = LanguageCertMatchStatus.class)
        LanguageCertMatchStatus matchStatus,
    @Schema(description = "매핑 비고", example = "컴퓨터SW 21학번 이후 기준") String note,
    @Schema(description = "시험별 통과 기준") List<Requirement> requirements) {

  /**
   * 학과 정책이 연결되지 않은 응답을 생성한다.
   *
   * @param departmentCode 학과 코드
   * @param departmentName 학과 이름
   * @param admissionYear admission 연도
   * @param note note 값
   * @return language cert 요건 응답 결과
   */
  public static LanguageCertRequirementResponse unmapped(
      String departmentCode, String departmentName, Integer admissionYear, String note) {
    return new LanguageCertRequirementResponse(
        departmentCode,
        departmentName,
        admissionYear,
        null,
        null,
        LanguageCertMatchStatus.UNMAPPED,
        note,
        List.of());
  }

  /**
   * 요건 데이터를 전달한다.
   *
   * @param testType test type 값
   * @param minimumScore minimum 점수
   * @param minimumGrade minimum 성적 값
   * @param displayText display text 값
   * @param sortOrder sort order 값
   */
  public record Requirement(
      @Schema(description = "시험 종류", implementation = LanguageCertTestType.class)
          LanguageCertTestType testType,
      @Schema(description = "최소 점수", example = "650", nullable = true) Integer minimumScore,
      @Schema(description = "최소 등급", example = "IM1", nullable = true) String minimumGrade,
      @Schema(description = "표시 문구", example = "TOEIC 650점 이상") String displayText,
      @Schema(description = "표시 순서", example = "1") Integer sortOrder) {
    /**
     * 외국어 인증 기준 도메인을 응답 항목으로 변환한다.
     *
     * @param requirement 외국어 인증 기준
     * @return 외국어 인증 기준 응답 항목
     */
    public static Requirement from(LanguageCertRequirement requirement) {
      return new Requirement(
          requirement.getTestType(),
          requirement.getMinimumScore(),
          requirement.getMinimumGrade(),
          requirement.getDisplayText(),
          requirement.getSortOrder());
    }
  }
}
