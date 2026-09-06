# 편입생 영역별 학점 및 지정과목 이수 현황 설계

## 기준과 상태

- 작업 이슈는 [#341](https://github.com/cchaksa/cchaksa-backend/issues/341)이다.
- 요구사항은 [노션 작업 사항](https://app.notion.com/p/3d01a2480da18046853de1e393ed876d)을 우선한다.
- 2026-09-06 사용자와 검토한 범위를 반영한다. 이 문서는 기존 최종 졸업 판정 설계를 대체한다.
- 코드 확인 기준은 `dev`의 `461abd38`과 `feat/341`의 `adb44c55`다. 기존 구현은 새 설계의 완료 증거가 아니다.
- 이번 문서 작업은 설계 갱신과 [구현 계획](plan.md) 작성까지다. 제품 코드, DB, GitHub 이슈와 Wiki는 변경하지 않는다.

## 목표와 범위

편입생에게 전핵·전선의 기준 대비 이수 현황, 나머지 영역의 취득학점, 지정과목의 취득학점 합계와 과목별 이수 상태를 제공한다.

| 대상 | 제공할 정보 | 판정 범위 |
| --- | --- | --- |
| 전핵 | 적용 교육과정의 3·4학년 전핵 목록과 이수 현황 | 대상 과목 전부 이수했는지 확인한다. |
| 전선 | 본인 학번에 적용되는 기준학점의 50%와 취득학점 | 필요학점 이상인지 확인한다. |
| 교양·전취·일선 등 | 취득학점과 이수 과목 목록 | 면제·충족 여부를 판정하지 않는다. |
| 지정과목 | 별도 취득학점 합계와 과목별 상태 | 실제 수강 기록과 대조한다. |

총 취득학점, 편입 인정학점, GPA, 외국어 인증의 기존 `dev` 제공 기능은 유지한다. 기존 130학점·GPA 정책을 이번 작업에서 재설계하거나 전체 졸업 자격으로 확대하지 않는다.

최종 졸업 가능 여부, 등록학기·졸업심사 추가 판정, 학생별 수동 정보 저장 API, 교양 일괄 면제, 범용 정책 엔진과 관리자 편집 UI는 제외한다. 규정이 확인되지 않은 편입 유형·복수전공에는 단일전공 규칙을 임의 확장하지 않고 확인 가능한 이수 내역을 제공한다.

## 현재 코드의 근거

경로는 저장소 루트 기준이다.

| 파일 | 현재 동작과 변경 방향 |
| --- | --- |
| `src/main/java/com/chukchuk/haksa/domain/graduation/service/GraduationService.java` | 캐시 조회 전 편입 전용 분기를 유지한다. |
| `src/main/java/com/chukchuk/haksa/domain/graduation/service/TransferGraduationAnalysisService.java` | 브랜치의 전핵·전필·전취 합산, 수동값 의존, 최종 판정을 영역별 표시로 교체한다. |
| `src/main/java/com/chukchuk/haksa/domain/graduation/policy/DesignatedCourseEvaluator.java` | 지정과목 상태와 인정학점 코드 중복 제거를 재사용한다. 브랜치의 offering 학점 fallback은 개인 취득학점 근거로 사용하지 않는다. |
| `src/main/java/com/chukchuk/haksa/domain/graduation/repository/GraduationQueryRepository.java` | 일반 조회는 F·R만 제외하고 요건 표를 순회한다. NP·IP 포함 가능성과 미등록 영역 누락 때문에 편입 계산에 그대로 사용하지 않는다. |
| `src/main/java/com/chukchuk/haksa/domain/academic/record/repository/StudentCourseRepository.java` | `findAllWithCourseByStudentId`로 수강·개설·과목을 한 번에 조회한다. |
| `src/main/java/com/chukchuk/haksa/infrastructure/portal/mapper/PortalDataMapper.java` | `toPortalCurriculumInfo`는 수강 목록으로 만든다. 미이수 전핵을 포함한 전체 교육과정이 아니다. |
| `src/main/java/com/chukchuk/haksa/application/portal/PortalStudentDataMapper.java` | `admissionYear`는 포털 `enscYear`에서 온 값이다. 학번의 교육과정 연도와 동일하다고 확정할 수 없다. |
| `src/main/java/com/chukchuk/haksa/domain/department/model/DepartmentAreaRequirement.java` | 학과·연도·영역별 필요학점은 있으나 과목별 배정 학년은 없다. |

운영 DB, 별도 교육과정 자료, 기존 기능의 배포 여부는 이번 문서 작업에서 조회하지 않았다. 저장소에 전체 목록을 보장하는 구조가 없다는 관찰을 운영 데이터 자체가 없다는 결론으로 확대하지 않는다.

## 구현 전에 확인할 근거

| 항목 | 확보할 증거 | 없을 때의 처리 |
| --- | --- | --- |
| 적용 교육과정 연도 | 익명화한 포털 값과 학교 기준을 대조한 학번→연도 규칙 | 입학연도 그대로 사용, 학번 앞자리 추출, 편입연도에서 2년 차감을 추정으로 적용하지 않는다. |
| 전핵 대상 목록 | 학과·적용 연도별 3·4학년 전핵 과목코드·학점·출처·목록 완전성 | 전핵 필요학점·충족 여부를 미확인으로 반환한다. |
| 전선 기준 | 학과·학번별 기준학점, 50% 소수 처리와 복수전공 적용 범위 | 자동 올림을 확정하지 않는다. 근거 없는 대상은 기준 미확인으로 반환한다. |
| 기존 API 배포 | 배포 버전·OpenAPI와 프론트 사용 여부 | 브랜치 전용 API 삭제 전에 호환 이행을 결정한다. |
| V14 적용 | 적용 환경과 Flyway 이력 | 기존 migration과 nullable 컬럼을 보존한다. |

근거와 결정은 이 문서에 기록한다. DTO, 유효 이수 정리, 취득학점 전용 표시와 지정과목 합계는 기준 확인과 독립적으로 진행할 수 있다. 전핵·전선 기준이 미확인인 채 전체 작업을 완료 처리하지 않는다.

기존 관리 자료를 우선 사용한다. 새 참조 저장소가 필요하면 학과·적용 연도·배정 학년·과목코드·기준학점과 출처·완전성을 표현하는 최소 구조를 확정하고 계획에 실제 파일명을 추가한다. 전체 교육과정 크롤러나 정책 관리 시스템을 선제적으로 만들지 않는다.

## 계산 규칙

### 유효 이수와 학점

- 편입 경로에서만 `F`, `R`, `NP`, `IP`, 성적 미확인과 재수강 삭제 기록을 취득학점 집계에서 제외한다. 공용 성적 정책과 일반 재학생 SQL은 바꾸지 않는다.
- 과목코드를 trim·대문자로 정규화하고 같은 과목을 여러 번 더하지 않는다. 일반 과목은 삭제되지 않은 유효 기록 중 최신 연도·학기를 사용한다. 같은 최신 시점의 학점·이수구분이 충돌하면 영향을 받는 합계를 미확인으로 둔다.
- 코드 없는 이수 기록은 화면에서 누락하지 않되 중복 제거를 보장할 수 없으므로 해당 합계를 확정하지 않는다.
- 취득학점은 개인 수강 기록의 `StudentCourse.points`가 기준이다. 지정과목 원본 `point`나 공용 offering 학점으로 대체하지 않는다.
- 정상 수신한 빈 목록의 합계는 0이다. 필요한 학점이 null인 합계는 null이며 일부 합계를 전체 합계로 반환하지 않는다. 과목 이수 여부와 학점 확인 여부는 별개다.
- 인정학점 코드 `07045`, `07046`, `00111`, `07050`은 영역별 합계에서 제외하고 별도로 표시한다. 코드별 유효 인정학점의 최대값을 한 번 합산하는 기존 규칙을 유지한다.
- 인정학점이 미확인이면 `recognizedTransferCredits`를 nullable로 확장하고 사유를 남긴다. offering fallback으로 값을 채우지 않는다.
- 총 취득학점은 `StudentAcademicRecord.totalEarnedCredits` 단일 기준이다. 영역·인정학점·지정과목 합계로 재구성하지 않는다.

### 영역별 현황

- 실제 이수 영역을 바탕으로 목록을 구성한다. 비교 대상 전핵·전선은 수강이 없어도 포함한다. 출력 순서는 기존 `FacultyDivision` 순서로 고정한다.
- 전취를 전핵에 합치지 않는다. 저장된 분류와 기존 기타 분류를 보존한다.
- 전핵 필요학점은 검증된 3·4학년 대상 과목의 기준학점 합계다. 수강 당시 학년으로 필터링하지 않는다. 완료 조건은 대상 과목 전체의 이수이며 다른 전핵으로 학점만 채운 경우 완료 처리하지 않는다.
- 전핵 전체 취득학점과 필수 대상 취득학점은 다를 수 있다. 전체 과목 목록을 보존하면서 `earnedCredits`와 `countedCredits`를 구분한다. 전핵의 화면 비율은 `countedCredits / requiredCredits`다.
- 전선은 `countedCredits=earnedCredits`다. 기준은 검증된 학과·적용 연도의 원래 전선 학점의 50%이며 예시의 48을 고정하지 않는다.
- 새 DTO의 필요학점은 `BigDecimal`로 표현한다. 올림·절삭 여부는 검증한 규칙으로만 결정한다.
- 대체·동일 과목 인정은 확인된 매핑만 사용한다. 과목명 유사도로 필수과목을 충족시키지 않는다.
- 학과 개편 후보 조회는 `GraduationMajorResolver`의 근거를 활용하되 일반 요건 미존재 예외가 편입 전체 현황을 차단하지 않게 한다.

### 지정과목

- 기존 `designatedCourses`의 순서·코드·명칭·원본 학점·이수 상태를 유지한다.
- `designatedEarnedCredits`는 이수한 지정과목의 개인 취득학점을 코드별 한 번 합산한다. 중복 원본 행은 표시해도 합계에 반복 반영하지 않는다.
- 목록 미수신이면 `designatedCoursesNeedsRefresh=true`, 합계 null이다. 수신한 빈 목록이면 새로고침 불필요, 합계 0이다.
- 코드 미확인 지정과목이나 개인 학점 미확인 이수 과목이 있으면 합계 null과 사유를 제공한다. 미이수 과목은 합산하지 않는다.
- 지정과목과 전공 영역의 중복 표시는 허용하지만 총학점에는 다시 더하지 않는다.

## API 계약

`GET /api/graduation/progress`, `analysisType=TRANSFER`, 편입 캐시 우회와 일반 재학생의 `graduationProgress` 계약을 유지한다. 새 목록은 `transferProgress.areas`에 둔다. 일반용 `AreaProgressDto`는 변경하지 않는다.

| TransferAreaProgressDto 필드 | 타입 | 의미 |
| --- | --- | --- |
| `areaType` | `FacultyDivision` | 기존 이수구분이다. |
| `evaluationType` | `TransferAreaEvaluationType` | `COMPARISON`, `EARNED_ONLY`, `UNAVAILABLE`다. |
| `earnedCredits` | nullable `Integer` | 영역 전체 실제 취득학점이다. |
| `countedCredits` | nullable `Integer` | 비교 대상에 포함되는 취득학점이다. 학점 전용 영역은 null이다. |
| `requiredCredits` | nullable `BigDecimal` | 검증한 필요학점이다. |
| `fulfilled` | nullable `Boolean` | 비교 대상의 충족 여부다. |
| `courses` | `List<CourseDto>` | 기존 과목 표시 계약을 재사용한 전체 이수 목록이다. |
| `requiredCourses` | `List<DesignatedCourseProgressDto>` | 전핵 대상 목록과 이수 상태다. 지정과목과 표현만 재사용하며 원천은 별개다. |
| `unavailableReasons` | `List<String>` | 아래 문서화한 코드만 사용한다. |

영역 사유는 `CURRICULUM_YEAR_UNVERIFIED`, `CORE_CURRICULUM_UNAVAILABLE`, `ELECTIVE_REQUIREMENT_UNAVAILABLE`, `TRANSFER_POLICY_UNVERIFIED`, `COURSE_DATA_INCOMPLETE`로 제한한다.

`EARNED_ONLY`는 의도적으로 기준 비교를 하지 않는 상태이며 면제가 아니다. `UNAVAILABLE`은 전핵·전선 비교 근거가 없는 상태다. 교양 개인 학점이 누락되면 `EARNED_ONLY`를 유지하고 합계 null·데이터 사유를 반환한다. 검증한 기준이 있는 `COMPARISON`에서도 개인 데이터가 미확인이면 비율과 충족을 확정하지 않는다. 알려진 미이수 과목이 있으면 전핵 `fulfilled=false`, 판단 근거가 부족하면 null이다.

기존 `analysisStatus=MANUAL_REVIEW_REQUIRED`는 전체 졸업 자격을 확정하지 않는 편입 부분 진단 의미로 유지한다. 영역 계산이 완료됐다고 전체를 `CALCULATED`로 바꾸지 않는다. `manualReviewRequired`는 학교 확인이 남는다는 의미이며 수동 입력 API를 요구하지 않는다. 계산으로 해소된 전핵·전선 수동 사유는 제거한다.

`transferProgress`에 `areas`, nullable `designatedEarnedCredits`, `designatedCreditUnavailableReasons`를 추가한다. 지정과목 사유는 `SNAPSHOT_NOT_RECEIVED`, `COURSE_DATA_INCOMPLETE`다. 인정학점 미확인은 기존 수동 사유에 `RECOGNIZED_CREDITS_INCOMPLETE`를 추가한다.

안내 문구는 “편입생은 개인별 적용 조건이 달라 전핵·전선은 기준 대비 이수 현황을, 나머지 영역은 취득학점만 제공합니다. 최종 졸업요건은 학과에 확인해 주세요.”로 제안한다. 문구·최상단 배치·드롭다운은 프론트가 담당하며 백엔드에 화면 설정 시스템을 만들지 않는다.

## 기존 feat/341 구현의 처리

`adb44c55` 전체 revert나 `dev` 파일 일괄 복원 대신 파일별 후속 변경으로 조정한다.

| 기존 구현 | 후속 구현 시 처리 |
| --- | --- |
| `majorCoreProgress`, `majorElectiveProgress` | `areas`로 통합하고 미배포·미사용 확인 후 중복 필드를 제거한다. |
| `graduationEligible`, 등록학기·졸업심사·지정과목 전체 충족 필드 | 최종 판정 결합을 제거한다. 필드 삭제 전 사용 여부를 확인한다. |
| `PATCH /api/graduation/transfer/manual-review` | 미배포·미사용 확인 후 controller/docs/request/service 경로와 전용 테스트만 제거한다. |
| 엔티티의 수동값 필드 | 분석에서 사용하지 않는다. 저장 데이터는 삭제하지 않고 불필요한 엔티티 정리로 범위를 넓히지 않는다. |
| `V14__add_transfer_manual_graduation_fields.sql` | 원문과 nullable 컬럼 보존이 기본안이다. 무단 DROP·rollback·수정하지 않는다. |
| 새 교육과정 DB 구조 | 필요할 때 최신 migration 다음 번호로 추가한다. 현재 마지막은 V14이나 구현 시 다시 확인한다. |

기존 API가 사용 중이면 삭제 전에 필요한 호환 이행을 이 문서에 확정한다. 이 확인은 문서 작성이나 독립 집계 작업의 차단 조건이 아니다.

## 완료 조건과 Wiki

1. 적용 학번·전핵 전체 목록의 출처와 완전성, 전선 50%·소수 정책 근거가 있다.
2. 전핵 미이수 과목을 식별하고 전선 경계값을 정확히 계산한다.
3. 교양·전취를 면제로 처리하지 않고 기준표 밖의 실제 영역도 표시한다.
4. 성적·재수강·중복·코드 충돌·null 학점에서 영역과 지정과목 집계가 일관된다.
5. 지정과목 미수신·빈 목록·중복 표시를 구분하고 총학점은 포털 누적값 그대로다.
6. 기준 누락이 확인 가능한 이수 현황까지 차단하지 않는다.
7. 일반 재학생 API·계산·캐시와 기존 총학점·GPA·외국어 인증을 보존한다.
8. API 배포·V14 검토 결과와 단위·통합·계약·실제 API 검증 증거가 계획에 기록돼 있다.

Wiki `master`의 `16fac8d`에서 편입생 계약을 확인했다. 구현 완료 시 `API-and-Authentication`, `Core-Domain-Flows`, `Troubleshooting`을 갱신하고 schema 추가 시 `Project-Architecture`도 갱신한다. 문서 작성 단계에서는 운영 Wiki를 바꾸지 않는다.

## 결정 기록

- 2026-09-06. 노션 우선, 전체 졸업 판정·수동 입력 제외, 편입 전용 영역 DTO와 유효 이수 집계를 선택했다.
- 2026-09-06. 기준 연도·전핵 원천·전선 소수 정책·API 배포·V14 적용은 미확인이다. 의존 작업의 근거 확보 단계로 계획에 명시했다.
- 문서 검사와 후속 구현 증거는 [계획의 실행 기록](plan.md#실행-기록)에 기록한다.
