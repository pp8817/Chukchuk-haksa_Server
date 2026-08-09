package com.chukchuk.haksa.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/** 척척학사의 스크래핑 결과 store 애플리케이션 설정을 제공한다. */
@Configuration
@RequiredArgsConstructor
public class ScrapeResultStoreConfig {

  private final ScrapingProperties scrapingProperties;

  /**
   * 스크래핑 결과 저장소에 접근할 S3 클라이언트를 생성한다.
   *
   * @return S3 클라이언트 결과
   */
  @Bean
  public S3Client scrapeResultStoreS3Client() {
    ScrapingProperties.ResultStore store = scrapingProperties.getResultStore();
    return S3Client.builder()
        .region(Region.of(store.getRegion()))
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .apiCallTimeout(java.time.Duration.ofSeconds(store.getApiCallTimeoutSeconds()))
                .apiCallAttemptTimeout(
                    java.time.Duration.ofSeconds(store.getApiCallAttemptTimeoutSeconds()))
                .build())
        .build();
  }
}
