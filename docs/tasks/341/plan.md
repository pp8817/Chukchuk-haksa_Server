# 편입생 영역별 학점 및 지정과목 이수 현황 구현 계획

> **For agentic workers:** 구현 착수 시 `superpowers:executing-plans`를 사용한다. 겹치는 서비스·DTO를 순서대로 변경하므로 기본 실행은 단일 구현 흐름이며 저장소의 위험도별 검토 규칙을 따른다.

**Goal:** 노션 기준으로 전핵·전선 비교, 나머지 영역 취득학점, 지정과목 이수 현황을 완성한다.

**Architecture:** 기존 편입 분기에서 수강 기록을 한 번 조회하고 편입 전용 정책으로 정규화한다. 검증한 교육과정과 결합해 `transferProgress.areas`를 만들고 동일한 이수 근거로 지정과목·인정학점을 계산한다. 일반 재학생 계산은 유지한다.

**Tech Stack:** Java 17, Spring Boot 3.2.5, Gradle, PostgreSQL, Flyway, JUnit 5, AssertJ, Mockito, Springdoc.

**Spec:** [design.md](design.md). 요구사항 우선순위, 계산 규칙과 미확인 사항의 단일 기준이다.

## 공통 제약

- 현재 기준 브랜치는 `feat/341`, 시작 커밋은 `adb44c55`, 비교 기준 `dev`는 `461abd38`다.
- 최종 졸업 판정·추가 졸업심사·수동 입력 기능은 제외한다. 기존 총학점·GPA·외국어 인증과 일반 재학생 동작은 보존한다.
- 개인 학점 누락을 offering 학점·0으로 대체하지 않고, 기준을 추정하지 않는다. 전취를 전핵에 합산하지 않는다.
- 기존 migration은 수정·삭제·rollback하지 않는다. 새 migration은 구현 당시 마지막 version 다음 번호다.
- 사용자 변경과 기존 커밋을 보존하고 전체 revert·reset·파일 일괄 복원을 하지 않는다.
- 커밋은 `341 {type}: {한국어 메시지}` 형식을 사용한다. 새 Java·SQL 소스에는 한국어 역할 주석을 두며 Java 스타일 문서를 따른다.
- 문서 작업은 LOW다. 후속 제품 구현은 공개 API·도메인 기준·필요 시 migration을 다루므로 HIGH로 취급한다. 관련 테스트와 실제 사용 검증 후 독립 Sol 검토를 수행한다. 코드 리뷰 전 저장소 규칙에 따라 리뷰 그래프를 확인한다.
- 교육과정 원천을 확인하기 전에는 전핵·전선 비교를 완료 처리하지 않는다. 확인 전 구현은 이수 현황과 `UNAVAILABLE` 상태만 제공한다.

## 파일 경계

아래 경로는 저장소 루트 기준이다. 새 파일은 예정 경로이며 아직 생성하지 않았다. 뒤의 Task에서 축약 파일명은 이 표의 전체 경로를 가리킨다.

| 처리 | 파일 | 역할 |
| --- | --- | --- |
| 추가 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferAreaProgressDto.java` | 설계의 영역별 응답이다. |
| 추가 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferAreaEvaluationType.java` | COMPARISON·EARNED_ONLY·UNAVAILABLE를 구분한다. |
| 추가 | `src/main/java/com/chukchuk/haksa/domain/graduation/policy/TransferCourseEvaluator.java` | 유효 이수·중복·누락을 정리한다. 결과 record는 이 파일에 둔다. |
| 추가 | `src/main/java/com/chukchuk/haksa/domain/graduation/policy/TransferAreaEvaluator.java` | 기준과 이수 내역을 영역 응답으로 변환한다. 기준 입력 record는 이 파일에 둔다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/policy/DesignatedCourseEvaluator.java` | 같은 이수 근거로 지정과목 합계를 계산한다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/service/TransferGraduationAnalysisService.java` | 조회·기준 해석·응답 조립을 담당한다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferGraduationProgressDto.java` | areas·지정과목 합계·누락 사유를 추가한다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/GraduationProgressResponse.java` | 편입 부분 진단 envelope을 유지한다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferManualReviewReason.java` | 해소된 사유와 인정학점 미확인 사유를 정확히 표현한다. |
| 조건부 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/controller/GraduationController.java` | 미배포 확인 후 수동 PATCH를 제거한다. |
| 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/controller/docs/GraduationControllerDocs.java` | 실제 최종 API 계약을 문서화한다. |
| 조건부 수정 | `src/main/java/com/chukchuk/haksa/domain/graduation/service/StudentGraduationProgressService.java` | 미배포 확인 후 수동 입력 서비스 경로를 제거한다. |
| 조건부 삭제 | `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferManualReviewRequest.java`, `src/main/java/com/chukchuk/haksa/domain/graduation/dto/TransferRequirementProgressDto.java` | 후속 구현으로 사용처가 없어진 경우만 삭제한다. |

전핵 참조 저장·조회 파일과 migration은 Task 1에서 실제 원천을 확인해 확정한다. 가상 저장소·빈 데이터 공급자를 제품에 먼저 추가하지 않는다.

## Task 1. 원천과 기존 배포 계약을 확인한다.

**Files:** 이 계획과 설계, 설계의 코드 근거 표에 있는 포털 mapper·학과 요건, `src/main/java/com/chukchuk/haksa/domain/graduation/policy/GraduationMajorResolver.java`, `src/main/resources/db/migration/V14__add_transfer_manual_graduation_fields.sql`.

**산출물:** 근거 URL·적용 범위·익명화 fixture·결정 내용과 필요한 실제 파일 목록을 두 문서에 기록한다.

- [ ] 포털 입학연도·학번·학과와 학교 적용 교육과정 연도를 대조해 검증한 규칙을 기록한다. 개인정보 원문은 문서에 넣지 않는다.
- [ ] 기존 운영 자료부터 확인해 학과·학번별 3·4학년 전핵 코드·학점·목록 완전성을 검증한다. 전핵이 없다는 확인과 자료가 없다는 상태를 구분한다.
- [ ] 전선 기준학점·소수 정책·편입 유형과 복수전공 적용 범위를 확인한다. 단일전공 기준을 다른 유형에 자동 적용하지 않는다.
- [ ] 참조 저장소가 필요하면 최소 schema·조회 계약과 실제 소스·migration·테스트 경로를 설계와 계획에 추가한다. 기존 자료로 해결되면 신규 테이블을 만들지 않는다.
- [ ] 수동 PATCH와 최종 판정 필드의 배포·소비 여부, V14 적용 이력을 확인한다. 비밀값 없이 버전·적용 여부만 기록한다.
- [ ] 비교 근거 미확인이면 Task 5만 보류하고 Task 2~4는 진행한다. API 배포 미확인이면 Task 6의 삭제만 보류한다.

**검증:** 적용 연도가 다른 사례, 기준 누락 사례, 검증된 빈 전핵 목록을 근거와 대조한다. 운영 자료에 접근하지 못한 것을 원천 부재로 기록하지 않는다.

**커밋:** 근거와 결정이 추가됐을 때 `341 docs: 편입생 교육과정 기준과 적용 범위 확정`으로 기록한다.

## Task 2. 편입 전용 응답 계약을 고정한다.

**Files:** 새 영역 DTO·enum, 편입 DTO·envelope, `src/test/java/com/chukchuk/haksa/domain/graduation/dto/GraduationProgressResponseJsonTest.java`, `src/test/java/com/chukchuk/haksa/global/config/OpenApiResponseContractTest.java`.

**입출력:** 기존 응답에 `areas`, `designatedEarnedCredits`, `designatedCreditUnavailableReasons`를 추가한다. 타입·사유 코드는 설계의 API 표를 따른다.

- [x] 기존 편입 fixture에 아래 직렬화 사례를 추가한다. 일반 학생의 `transferProgress` 미노출 assertion을 유지한다.

```json
{
  "areaType": "전취",
  "evaluationType": "EARNED_ONLY",
  "earnedCredits": 0,
  "countedCredits": null,
  "requiredCredits": null,
  "fulfilled": null,
  "courses": [],
  "requiredCourses": [],
  "unavailableReasons": []
}
```

```java
JsonNode area = json.path("transferProgress").path("areas").get(0);
assertThat(area.path("evaluationType").asText()).isEqualTo("EARNED_ONLY");
assertThat(area.path("requiredCredits").isNull()).isTrue();
assertThat(area.path("fulfilled").isNull()).isTrue();
assertThat(json.path("analysisStatus").asText()).isEqualTo("MANUAL_REVIEW_REQUIRED");
```

- [ ] `./gradlew test --tests '*GraduationProgressResponseJsonTest' --tests '*OpenApiResponseContractTest' --no-daemon`으로 새 계약이 없어 실패함을 확인한다.
- [x] DTO·enum·nullable 필드를 추가한다. 기존 dev 계약을 유지하고 브랜치 전용 필드 삭제는 Task 6에서 수행한다.
- [x] COMPARISON·UNAVAILABLE, null 합계·실제 0, 일반 응답도 같은 명령으로 검증한다.
- [ ] `341 feat: 편입생 영역별 이수 현황 응답 계약 추가`로 커밋한다.

## Task 3. 편입 유효 이수 기록을 일관되게 정리한다.

**Files:** `TransferCourseEvaluator.java`, `DesignatedCourseEvaluator.java`, 새 `src/test/java/com/chukchuk/haksa/domain/graduation/policy/TransferCourseEvaluatorTest.java`, 기존 `src/test/java/com/chukchuk/haksa/domain/graduation/policy/DesignatedCourseEvaluatorTest.java`.

**입출력:** `TransferCourseEvaluator.evaluate(List<StudentCourse>)`는 nested `Evaluation`을 반환한다. 결과에는 코드별 유효 기록, 영역별 전체 목록, nullable 인정학점 합계와 영향을 받는 코드·영역의 누락 정보를 담는다. 영역과 지정과목 평가기가 같은 결과를 소비한다.

- [x] 기존 `studentCourse` fixture를 참고해 등급별 제외, 재수강 삭제, 중복 코드·최신 기록, 동일 시점 충돌, 빈 코드, null·0학점을 테스트한다.
- [ ] `./gradlew test --tests '*TransferCourseEvaluatorTest' --tests '*DesignatedCourseEvaluatorTest' --no-daemon`으로 실패를 확인한다.
- [x] 다음 알고리즘을 편입 전용 평가기에 구현한다. 일반 SQL과 공용 성적 정책은 변경하지 않는다.

```text
유효 성적과 재수강 삭제 여부로 필터링한다.
코드를 정규화하고 코드별로 묶는다.
일반 과목은 최신 유효 기록을 선택하되 동시점 충돌은 미확인으로 남긴다.
인정학점 코드는 별도로 기존 코드별 최대 유효 학점 규칙을 적용한다.
개인 학점이 없으면 unknown을 보존하고 offering으로 대체하지 않는다.
전체 이수 목록과 집계 결과를 함께 보관한다.
```

- [x] 지정과목 평가기가 `Evaluation`을 소비하도록 연결하고 offering fallback 테스트를 null·미확인 사유 검증으로 교체한다. 모든 호출부는 `rg`로 확인한다.
- [x] 동일 명령을 재실행한다. 인정학점 `07045:15,18`과 `07050:17`의 합계 35를 검증하는 기존 사례도 유지한다.
- [ ] `341 feat: 편입생 유효 이수와 학점 집계 기준 통일`로 커밋한다.

## Task 4. 모든 영역과 지정과목의 취득학점을 제공한다.

**Files:** `TransferAreaEvaluator.java`, `TransferGraduationAnalysisService.java`, `DesignatedCourseEvaluator.java`, 새 `src/test/java/com/chukchuk/haksa/domain/graduation/policy/TransferAreaEvaluatorTest.java`, 기존 `src/test/java/com/chukchuk/haksa/domain/graduation/service/TransferGraduationAnalysisServiceTests.java`.

**입출력:** `TransferAreaEvaluator.evaluate(TransferCourseEvaluator.Evaluation, TransferAreaEvaluator.Requirements)`는 `List<TransferAreaProgressDto>`를 반환한다. nested `Requirements`는 검증된 전핵 대상 목록, 전선 필요학점, 기준별 미확인 사유를 담는다. 미수신과 검증된 빈 전핵 목록을 구분한다.

- [ ] 아래 사례를 테스트로 먼저 작성한다.

| 입력 | 기대 결과 |
| --- | --- |
| 요건 표에 없는 전취 6학점·기타 2학점 | 두 영역 모두 EARNED_ONLY로 반환한다. |
| 전핵·전선 기준 없음 | 과목·전체 취득학점은 남고 비교만 UNAVAILABLE다. |
| 지정과목 C101 3학점이 전핵에도 포함 | 양쪽에 표시하고 누적 총학점 112는 그대로다. |
| 지정과목 C101 원본 두 행 | 표시 순서는 보존하고 합계는 3이다. |
| 지정목록 미수신 / 수신한 빈 목록 | 합계 null·refresh=true / 합계 0·refresh=false다. |
| 이수한 지정과목의 개인 학점 null | 이수 상태는 유지하고 합계 null·사유를 반환한다. |

- [x] `./gradlew test --tests '*TransferAreaEvaluatorTest' --tests '*TransferGraduationAnalysisServiceTests' --tests '*DesignatedCourseEvaluatorTest' --no-daemon`으로 실패를 확인한다.
- [x] 수강 기록을 한 번 fetch한 뒤 정규화 결과를 두 평가기에 전달한다. 실제 영역과 전핵·전선의 합집합을 고정 순서로 출력한다.
- [x] 기준이 없는 전핵·전선은 실제 사유로 UNAVAILABLE를 반환한다. 기존 전취 합산·일괄 올림·수동값 최종 판정은 새 분석 경로에서 사용하지 않는다.
- [x] 지정과목 코드 집합과 유효 이수 기록을 교차해 합계를 계산한다. 원본 목록 학점이나 화면 합계로 총학점을 재계산하지 않는다.
- [ ] 같은 명령을 통과시키고 `341 feat: 편입생 영역별 취득학점과 지정과목 합계 제공`으로 커밋한다.

## Task 5. 검증한 전핵·전선 기준을 연결한다.

**선행 조건:** Task 1의 교육과정 연도·전핵 전체 목록·전선 소수 정책 근거가 기록돼 있어야 한다. 미확인이면 이 Task와 제품 전체를 완료 표시하지 않는다.

**Files:** `TransferAreaEvaluator.java`, `TransferGraduationAnalysisService.java`, Task 1에서 확정한 기준 조회 파일, `TransferAreaEvaluatorTest.java`, `src/test/java/com/chukchuk/haksa/domain/graduation/service/TransferGraduationAnalysisIntegrationTest.java`. schema를 추가하면 `src/test/java/com/chukchuk/haksa/global/db/FlywayMigrationTest.java`도 포함한다.

- [ ] 다음 경계 사례를 `Requirements` fixture로 작성한다. 과목코드와 숫자는 계산 검증용 합성 값이다.

| 기준·수강 | 기대 결과 |
| --- | --- |
| 전핵 필수 C301·C401 각 3학점, C301·기타 전핵 C999만 이수 | earned=6, counted=3, required=6, fulfilled=false다. |
| 전핵 필수 두 과목 모두 이수, 실제 수강 연도는 다름 | 교육과정 배정 학년을 기준으로 true다. |
| 전선 원래 기준 96, 취득 45·48·51 | 필요 48, 각각 false·true·true다. |
| 학번 기준 연도와 포털 편입연도가 다름 | 검증한 기준 연도의 요건을 선택한다. |
| 전핵 목록 완전성 미확인 | 빈 목록이라고 자동 완료하지 않는다. |
| 미지원 편입 유형·복수전공 | 절반 규칙을 임의 적용하지 않고 기준 미확인과 이수 현황을 제공한다. |

- [ ] `./gradlew test --tests '*TransferAreaEvaluatorTest' --tests '*TransferGraduationAnalysisIntegrationTest' --no-daemon`으로 실패를 확인한다.
- [ ] 검증한 원천만 `Requirements`에 연결한다. 학과 개편 후보는 기존 resolver 근거를 활용하되 기준 부재가 전체 응답 예외가 되지 않게 한다.
- [ ] 전핵은 대상 과목 전체 이수로 완료를 판정하고 countedCredits를 별도 계산한다. 대체과목은 검증된 매핑만 사용한다.
- [ ] 전선 필요학점은 BigDecimal로 계산해 확인한 소수 정책을 적용한다. 코드 편의로 올림·절삭하지 않는다.
- [ ] 필요한 migration은 새 version으로 추가하고 V14 원문을 보존한다. 해당 시 `./gradlew test --tests '*FlywayMigrationTest' --no-daemon`도 통과시킨다.
- [ ] 같은 명령을 통과시키고 `341 feat: 편입생 교육과정별 전핵 전선 기준 적용`으로 커밋한다.

## Task 6. 이전 전체 판정·수동 입력 계약을 정리한다.

**선행 조건:** Task 1에서 미배포·미사용을 확인한 경우의 실행안이다. 사용 중이면 삭제 전에 호환 이행안을 설계에 기록하고 그 검증을 추가한다.

**Files:** 파일 경계의 조건부 수정·삭제 파일, 편입 DTO·envelope·수동 사유, `src/test/java/com/chukchuk/haksa/domain/graduation/controller/GraduationControllerApiIntegrationTest.java`, `src/test/java/com/chukchuk/haksa/domain/graduation/service/StudentGraduationProgressServiceTests.java`, `src/test/java/com/chukchuk/haksa/domain/graduation/service/GraduationServiceTests.java`, JSON·OpenAPI 테스트.

- [x] GET JSON에 최종 판정 필드가 없고 OpenAPI에 수동 PATCH가 없는 테스트를 먼저 추가한다.

```java
assertThat(json.path("transferProgress").has("graduationEligible")).isFalse();
assertThat(openApi.path("paths").has("/api/graduation/transfer/manual-review")).isFalse();
```

- [ ] `./gradlew test --tests '*GraduationProgressResponseJsonTest' --tests '*GraduationControllerApiIntegrationTest' --tests '*OpenApiResponseContractTest' --tests '*StudentGraduationProgressServiceTests' --tests '*GraduationServiceTests' --no-daemon`으로 실패를 확인한다.
- [x] 수동 PATCH·request·service 쓰기 경로, 브랜치의 전체 판정 필드·함수와 사용처 없는 DTO만 제거한다. 다른 API·동기화·엔티티 데이터는 보존한다.
- [x] 편입 envelope의 MANUAL_REVIEW_REQUIRED 의미를 유지하고 영역 상태는 독립 제공한다. 해결된 전핵·전선 사유만 제거한다.
- [x] V14와 nullable 컬럼을 보존한다. 새 분석이 수동 컬럼을 읽거나 사용자 입력을 요구하지 않음을 검증한다.
- [ ] 같은 명령으로 일반 응답·캐시 분기·외국어 동기화를 검증하고 `341 refactor: 편입생 진단을 이수 현황 제공 범위로 정리`로 커밋한다.

## Task 7. 실제 API와 전체 검증을 완료한다.

**Files:** controller docs, OpenAPI 테스트, 실행 기록, 별도 Wiki의 `API-and-Authentication.md`, `Core-Domain-Flows.md`, `Troubleshooting.md`. 새 schema가 있으면 `Project-Architecture.md`도 포함한다.

- [ ] 익명화 포털 fixture→동기화→GET 응답을 통합 테스트한다. 새로고침 후 지정과목·영역 갱신, 중복 합산 없는 총학점, 일반 재학생 회귀를 포함한다.
- [ ] 프론트와 전핵 counted/required, 전선 earned/required, earned-only·unavailable, 지정과목 최상단·드롭다운·안내 문구를 확인한다. 이 저장소에서 프론트 소스를 수정하지 않는다.
- [x] Java 17과 로컬 PostgreSQL 등 기존 실행 조건을 확인한다. 비밀값은 명령 출력·문서·커밋에 기록하지 않는다.
- [x] `./gradlew spotlessApply --no-daemon`을 실행하고 요청 범위 밖의 포맷 변경이 없는지 확인한다.
- [x] `./gradlew check --stacktrace --no-daemon`을 통과시킨다. 실패나 환경 제한은 실제 오류·영향·재검증 결과를 기록한다.
- [ ] 로컬 앱을 실행하고 별도 터미널에서 OpenAPI를 조회한다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local' --no-daemon
```

```bash
curl --fail --silent http://localhost:8080/v3/api-docs
```

- [ ] 실제 schema에서 areas·세 가지 상태·nullable 숫자·지정과목 합계·일반 응답을 확인한다. 미배포 수동 PATCH와 최종 판정 필드가 없어야 한다. 실제 인증 GET도 익명화한 로컬 테스트 계정으로 검증한다.
- [ ] Wiki를 실제 동작과 일치하게 갱신하고 변경 커밋과 대상 문서를 기록한다.
- [ ] 관련 증거와 diff를 독립 Sol 검토에 제공한다. 성공 기준 차단사항을 수정하고 영향받는 검증을 재실행한다. 반복 실패 시 저장소 재검토 제한을 따른다.
- [ ] `git diff --check`, 변경 범위·문서 링크 검토 후 `341 docs: 편입생 이수 현황 API와 검증 결과 기록`으로 커밋한다.

## 성공 기준 추적

| 설계의 성공 기준 | Task |
| --- | --- |
| 적용 학번·전핵 전체 목록·전선 소수 정책 근거 | 1, 5 |
| 모든 실제 영역·전취 분리·표시 상태 구분 | 2, 4 |
| 성적·재수강·코드·학점 누락 정합성 | 3, 4 |
| 전핵 전체 과목 이수·전선 경계값 | 5 |
| 지정과목 중복·미수신·빈 목록·총학점 보존 | 3, 4, 7 |
| 기존 전체 판정·수동 입력 조정, V14 보존 | 1, 6 |
| 일반 학생·외국어·API 호환성과 실제 사용 검증 | 2, 6, 7 |

## 실행 기록

- 2026-09-06. `feat/341`로 체크아웃하고 설계를 노션 기준으로 갱신했다. 구현 Task는 아직 실행하지 않았다.
- 2026-09-07. Task 2~4와 Task 6 구현을 시작해 편입 영역 응답·유효 수강 정규화·지정과목 학점·최종 판정 및 수동 PATCH 제거를 반영했다. 교육과정 원천이 확인되지 않아 Task 5는 보류하고 전핵·전선을 `UNAVAILABLE`로 반환한다.
- 2026-09-07. Java 17 직접 Gradle로 `check`, 정책·DTO·서비스·컨트롤러·OpenAPI·편입 통합 테스트를 통과했다. 기본 Java 24의 `./gradlew test`는 Gradle Test task 생성 중 `Type T not present`로 실행되지 않아 Java 17 검증으로 재실행했다.
- 이번 구현에서 DB schema·migration은 변경하지 않았고 API 배포 확인, GitHub 이슈 수정과 Wiki 갱신은 수행하지 않았다.
- 문서와 코드 검증은 `git diff --check`와 Java 17 기준 전체 `check` 통과로 확인했다. 계획의 체크 상태와 실제 구현·보류 범위를 대조했다.
- 교육과정 원천 확인, 로컬 앱의 실제 OpenAPI 조회, Wiki 갱신과 독립 리뷰는 다음 단계로 남아 있다.
