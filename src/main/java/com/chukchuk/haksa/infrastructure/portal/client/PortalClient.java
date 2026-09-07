package com.chukchuk.haksa.infrastructure.portal.client;

import com.chukchuk.haksa.global.exception.code.ErrorCode;
import com.chukchuk.haksa.global.logging.annotation.LogTime;
import com.chukchuk.haksa.infrastructure.portal.dto.raw.RawPortalData;
import com.chukchuk.haksa.infrastructure.portal.exception.PortalScrapeException;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/** 학교 포털 인증과 학사 데이터 조회를 수행한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortalClient {
  private static final Duration LOGIN_REQUEST_TIMEOUT = Duration.ofSeconds(90);

  @Value("${crawler.base-url}")
  private String baseUrl;

  @Value("${crawler.internal-auth-token:}")
  private String internalAuthToken;

  private final RestTemplate restTemplate;
  private final RestTemplate loginRestTemplate = new RestTemplate(loginRequestFactory());

  /**
   * 포털 로그인 endpoint에 자격 증명을 제출해 실제 로그인 가능 여부를 확인한다.
   *
   * @param username 포털 로그인 아이디
   * @param password 포털 비밀번호
   * @throws PortalScrapeException 자격 증명이 틀렸거나 계정이 잠겼거나 포털 요청에 실패한 경우
   */
  public void validateLogin(String username, String password) {
    String uri = "/login";
    long t0 = LogTime.start();

    try {
      RequestEntity<LoginRequest> request =
          RequestEntity.post(URI.create(baseUrl + uri))
              .contentType(MediaType.APPLICATION_JSON)
              .header("X-Scraper-Internal-Token", internalAuthToken)
              .body(new LoginRequest(username, password));

      loginRestTemplate.exchange(request, Void.class);

    } catch (RestClientResponseException e) {
      logHttpError(uri, t0, e);
      throw new PortalScrapeException(mapHttpStatus(e.getStatusCode()), e);

    } catch (Exception e) {
      long tookMs = LogTime.elapsedMs(t0);
      log.warn("[EXT] method=POST uri={} unexpected_error took_ms={}", uri, tookMs, e);
      throw new PortalScrapeException(ErrorCode.PORTAL_SCRAPE_FAILED, e);
    }
  }

  /**
   * 포털에 로그인해 전체 학사 데이터를 조회한다.
   *
   * @param username 포털 로그인 아이디
   * @param password 포털 비밀번호
   * @return 포털이 반환한 학생·학기별 과목·성적 원본 데이터
   * @throws PortalScrapeException 응답 본문이 없거나 포털 요청에 실패한 경우
   */
  public RawPortalData scrapeAll(String username, String password) {
    String uri = "/scrape";
    long t0 = LogTime.start();

    try {
      RequestEntity<LoginRequest> request =
          RequestEntity.post(URI.create(baseUrl + uri))
              .contentType(MediaType.APPLICATION_JSON)
              .body(new LoginRequest(username, password));

      RawPortalData body = restTemplate.exchange(request, RawPortalData.class).getBody();
      if (body == null) {
        throw new PortalScrapeException(ErrorCode.PORTAL_SCRAPE_FAILED);
      }
      return body;
    } catch (RestClientResponseException e) {
      logHttpError(uri, t0, e);
      throw new PortalScrapeException(mapHttpStatus(e.getStatusCode()), e);

    } catch (Exception e) {
      long tookMs = LogTime.elapsedMs(t0);
      log.warn("[EXT] method=POST uri={} unexpected_error took_ms={}", uri, tookMs, e);
      throw new PortalScrapeException(ErrorCode.PORTAL_SCRAPE_FAILED, e);
    }
  }

  private record LoginRequest(String username, String password) {}

  private static SimpleClientHttpRequestFactory loginRequestFactory() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout((int) LOGIN_REQUEST_TIMEOUT.toMillis());
    return requestFactory;
  }

  private void logHttpError(String uri, long t0, RestClientResponseException e) {
    long tookMs = LogTime.elapsedMs(t0);
    int status = e.getStatusCode().value();

    if (status >= 500) {
      log.warn("[EXT] method=POST uri={} status={} took_ms={}", uri, status, tookMs);
    } else {
      log.warn("[EXT] method=POST uri={} status={} took_ms={}", uri, status, tookMs);
    }
  }

  private ErrorCode mapHttpStatus(HttpStatusCode status) {
    if (status == null) {
      return ErrorCode.PORTAL_SCRAPE_FAILED;
    }

    return switch (status.value()) {
      case 401 -> ErrorCode.PORTAL_LOGIN_FAILED;
      case 423 -> ErrorCode.PORTAL_ACCOUNT_LOCKED;
      default -> ErrorCode.PORTAL_SCRAPE_FAILED;
    };
  }
}
