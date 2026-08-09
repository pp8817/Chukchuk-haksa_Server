# 졸업요건 Sentry 문맥 누락 Hotfix 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 모든 `G02` 발생 경로에 개인정보를 노출하지 않는 졸업요건 식별 태그를 남긴다.

**Architecture:** Resolver 실패 경로와 Resolver 성공 후 진행률 조회 실패 경로가 동일한 MDC 태그 규칙을 사용한다. 서비스는 최종 전공 ID로 진행률을 조회하다 `G02`가 발생한 경우 문맥을 주입하고, Resolver는 전공 조합을 찾지 못한 경우 문맥을 주입한다.

**Tech Stack:** Java 17, Spring Boot 3.2.5, SLF4J MDC, Sentry Java, JUnit 5, Mockito, AssertJ.

## Global Constraints

- `origin/main`에서 생성한 `hotfix/328`에서 작업한다.
- 원문 학번은 기록하지 않고 `HashUtil.sha256Short` 결과만 사용한다.
- 사용자별 태그는 Sentry fingerprint에 포함하지 않는다.
- API 응답과 졸업요건 계산 결과는 변경하지 않는다.

---

### Task 1: Sentry 태그 개인정보 정규화

**Files:**
- Modify: `src/main/java/com/chukchuk/haksa/global/logging/sentry/SentryMdcTagBinder.java`
- Modify: `src/main/resources/logback-spring.xml`
- Test: `src/test/java/com/chukchuk/haksa/global/logging/sentry/SentryMdcTagBinderTest.java`

- [x] `studentCodeHash`, `admissionYear`, `departmentId`, `secondaryDepartmentId`, `majorType`를 요구하는 실패 테스트를 작성한다.
- [x] 테스트가 기존 snake_case 태그 때문에 실패하는지 확인한다.
- [x] 바인더와 Logback Sentry appender의 태그 이름을 정규화한다.
- [x] 집중 테스트를 통과시키고 커밋한다.

### Task 2: 모든 G02 경로에 졸업요건 문맥 주입

**Files:**
- Modify: `src/main/java/com/chukchuk/haksa/domain/graduation/service/GraduationService.java`
- Modify: `src/main/java/com/chukchuk/haksa/domain/graduation/policy/GraduationMajorResolver.java`
- Test: `src/test/java/com/chukchuk/haksa/domain/graduation/service/GraduationServiceTests.java`
- Test: `src/test/java/com/chukchuk/haksa/domain/graduation/policy/GraduationMajorResolverTest.java`

- [x] 단일전공 진행률이 비어 있을 때 최종 학과 문맥이 남는 실패 테스트를 작성한다.
- [x] 복수전공 Resolver 성공 후 Repository에서 `G02`가 발생할 때 문맥이 남는 실패 테스트를 작성한다.
- [x] Resolver 자체 실패 시 해시 학번과 원래 학과 문맥이 남는 실패 테스트를 작성한다.
- [x] 서비스에서 최종 전공 ID를 사용한 진행률 조회가 실패하면 MDC 문맥을 주입한다.
- [x] Resolver 실패 경로의 원문 학번을 해시로 교체한다.
- [x] 집중 테스트를 통과시키고 논리 단위로 커밋한다.

### Task 3: 통합 검증

**Files:**
- Modify only if verification exposes a #328 requirement defect.

- [x] 졸업요건 및 Sentry 집중 테스트를 실행한다.
- [x] `./gradlew test --stacktrace --no-daemon`을 실행한다.
- [x] `git diff --check origin/main...HEAD`를 실행한다.
- [x] 원문 학번 MDC 키가 운영 코드와 Logback 설정에 남지 않았는지 확인한다.
- [x] 변경 범위와 커밋 단위를 검토한다.

## Verification

- 집중 테스트: `BUILD SUCCESSFUL`.
- 전체 테스트: `BUILD SUCCESSFUL`.
- 요청 종료 시 `MdcCleanupFilter`의 `MDC.clear()`로 신규 문맥을 정리한다.
- 성공 요청에는 졸업요건 문맥을 추가하지 않고 `G02` 발생 시에만 주입한다.
