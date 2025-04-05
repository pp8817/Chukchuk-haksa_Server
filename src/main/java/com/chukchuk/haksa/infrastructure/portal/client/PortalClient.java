package com.chukchuk.haksa.infrastructure.portal.client;

import com.chukchuk.haksa.infrastructure.portal.dto.raw.RawPortalData;
import com.chukchuk.haksa.infrastructure.portal.exception.PortalLoginException;
import com.chukchuk.haksa.infrastructure.portal.exception.PortalScrapeException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class PortalClient {
    @Value("${crawler.base-url}")
    private String baseUrl;

    private final WebClient webClient = WebClient.builder().build();

    public void login(String username, String password) {
        try {
            webClient.post()
                    .uri(baseUrl + "/auth")
                    .header("Content-Type", "application/json")
                    .bodyValue(new LoginRequest(username, password))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            String message = switch (e.getStatusCode()) {
                case UNAUTHORIZED -> "아이디나 비밀번호가 일치하지 않습니다.";
                case LOCKED -> "계정이 잠겼습니다. 포털에서 비밀번호 재발급이 필요합니다.";
                default -> "로그인 중 오류가 발생했습니다.";
            };
            throw new PortalLoginException(message, e);
        }
    }

    public RawPortalData scrapeAll(String username, String password) {
        try {
            return webClient.post()
                    .uri(baseUrl + "/scrape")
                    .header("Content-Type", "application/json")
                    .bodyValue(new LoginRequest(username, password))
                    .retrieve()
                    .bodyToMono(RawPortalData.class)
                    .block();
        } catch (WebClientResponseException e) {
            String message = switch (e.getStatusCode()) {
                case UNAUTHORIZED -> "아이디나 비밀번호가 일치하지 않습니다.";
                case LOCKED -> "계정이 잠겼습니다. 포털에서 비밀번호 재발급이 필요합니다.";
                default -> "크롤링 중 오류가 발생했습니다.";
            };
            throw new PortalScrapeException(message, e);
        }
    }

    private record LoginRequest(String username, String password) {}
}
