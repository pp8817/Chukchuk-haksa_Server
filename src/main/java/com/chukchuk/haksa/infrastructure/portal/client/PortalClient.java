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

  private final RestTemplate restTemplate;
  private final RestTemplate loginRestTemplate = new RestTemplate(loginRequestFactory());

  /**
   * 입력 값과 업무 처리 조건을 검증한다.
   *
   * @param username user이름
   * @param password 포털 비밀번호
   */
  public void validateLogin(String username, String password) {
    String uri = "/login";
    long t0 = LogTime.start();

    try {
      RequestEntity<LoginRequest> request =
          RequestEntity.post(URI.create(baseUrl + uri))
              .contentType(MediaType.APPLICATION_JSON)
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
   * @param username user이름
   * @param password 포털 비밀번호
   * @return raw 포털 data 결과
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
