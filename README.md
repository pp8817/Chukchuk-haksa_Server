<div align="center">

<img src="https://github.com/user-attachments/assets/16764352-ab1e-4cfa-b30e-6287fafde803" width="600"/>

<h2>척척학사 Backend</h2>

</div>

---

## 📚 목차

1. [프로젝트 소개](#1-프로젝트-소개)
2. [주요 기능](#2-주요-기능)
3. [API 문서](#3-api-문서)
4. [기술 스택](#4-기술-스택)
5. [아키텍처](#5-아키텍처)
6. [ERD](#6-erd)
7. [커밋 컨벤션](#7-커밋-컨벤션)
8. [관련 블로그 게시글](#8-관련-블로그-게시글)

---

## 1. 📌 프로젝트 소개

**척척학사**는 학생들이 학교 포털과 연동하여 본인의 이수 현황과 졸업 요건을 손쉽게 확인하고 관리할 수 있도록 도와주는 서비스입니다.

> 기존에는 학생들이 직접 졸업 요건을 일일이 대조해야 했지만, 척척학사는 이를 자동화하여 효율적인 졸업 준비를 지원합니다.

- **600명 이상** 수원대 재학생 사용 중
- **학교 포털과 실시간 동기화**
- **졸업 요건 자동 분석**

---

## 2. 💡 주요 기능

<p align="center">
  <img src="https://github.com/user-attachments/assets/84cf31e3-6180-495f-a183-ead0d082b4fc" width="800" alt="IA 구조도" />
  <br/>
  <img src="https://github.com/user-attachments/assets/9fe3eb54-97be-48d9-8d60-c20da2a9e19d" width="800" alt="기능 소개1" />
  <br/>
  <img src="https://github.com/user-attachments/assets/1f464920-a223-45da-9cf1-4d8c2bd04cd6" width="800" alt="기능 소개2" />
</p>

- 포털 연동을 통한 학점·성적·커리큘럼 실시간 동기화
- 졸업 요건과 사용자 학사 정보 자동 비교
- 부족한 학점 및 조건 자동 분석

---

## 3. 🗂️ API 문서

- 👉 [Swagger API 문서 보기](https://dev.api.cchaksa.com/swagger-ui/index.html#/)

---

## 4. ⚙ 기술 스택

### 🔧 Back-end
- Java 17
- Spring Boot 3
- Spring Security, OAuth2
- JPA, Hibernate

### ☁️ Infra
- AWS EC2, ALB, Route53, ACM ...
- PostgreSQL (Supabase 연동)
- Redis (세션 관리 및 임시 저장소)

### 🛠 Tools
- Git, GitHub
- Swagger (OpenAPI)
- Gradle

---

## 5. 🛠 프로젝트 아키텍처

(아키텍처 다이어그램 추가 예정)

---

## 6. 🗃 ERD

> *(2025.04.30 기준)*

<img src="https://github.com/user-attachments/assets/7f44b492-5a63-4540-8319-ef45dd70a6ae" width="100%" alt="ERD"/>

---

## 7. 📝 커밋 컨벤션

### ✅ 기본 구조
```bash
type: subject

body (선택)
```

### ✅ type 종류
```
feat: 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
comment: 주석 추가/수정
docs: 문서 수정
test: 테스트 관련 코드
chore: 빌드/패키지 설정 변경
rename: 파일/폴더명 변경
remove: 파일 삭제
style: 포맷 변경
!BREAKING CHANGE!: 주요 API 변경
```

### ✅ 커밋 예시
```
feat: 로그인 기능 구현

Email 중복확인 API 개발

---

fix: 사용자 정보 누락 버그 해결

사용자 서비스 코드 수정
```

## 8. 📝 관련 블로그 게시글
- [척척학사: Supabase를 알아보자 With Java, Spring](https://velog.io/@pp8817/DB-Supabase%EB%A5%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90-With-Java-Spring)
- [척척학사: OIDC 인증, 인가 처리 With Kakao 소셜 로그인](https://velog.io/@pp8817/%EC%B2%99%EC%B2%99%ED%95%99%EC%82%AC-OIDC-%EC%9D%B8%EC%A6%9D-%EC%9D%B8%EA%B0%80-%EC%B2%98%EB%A6%AC-With-Kakao-%EC%86%8C%EC%85%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8)
- [척척학사: Entity 연관관계 분석 및 리팩토링](https://velog.io/@pp8817/%EC%B2%99%EC%B2%99%ED%95%99%EC%82%AC-Entity-%EC%97%B0%EA%B4%80%EA%B4%80%EA%B3%84-%EB%B6%84%EC%84%9D-%EB%B0%8F-%EB%A6%AC%ED%8C%A9%ED%86%A0%EB%A7%81)
- [척척학사: API 응답 모듈 분리 과정](https://velog.io/@pp8817/%EC%B2%99%EC%B2%99%ED%95%99%EC%82%AC-API-%EC%9D%91%EB%8B%B5-%EB%AA%A8%EB%93%88-%EB%B6%84%EB%A6%AC-%EA%B3%BC%EC%A0%95)
- [척척학사: 무중단 배포 전략 - 프로젝트에 적용하기](https://velog.io/@pp8817/%EC%B2%99%EC%B2%99%ED%95%99%EC%82%AC-%EB%AC%B4%EC%A4%91%EB%8B%A8-%EB%B0%B0%ED%8F%AC-%EC%A0%84%EB%9E%B5-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%EC%97%90-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0)
- [척척학사: Next.js 기반 서버를 Spring으로 갈아엎은 이유](https://velog.io/@pp8817/척척학사-Next.js-Spring-Boot-백엔드-마이그레이션-회고)
- [척척학사: 크롤링 로직 비동기 처리 대신 Redis 캐싱을 도입한 이유](https://velog.io/@pp8817/척척학사-크롤링-로직-비동기-처리-대신-Redis-캐싱을-도입한-이유)
- [척척학사: 로그 시스템 리빌드: ELK → Grafana Loki](https://velog.io/@pp8817/척척학사-ELK에서-Grafana-Loki-Stack으로-갈아탄-이유와-후기)
