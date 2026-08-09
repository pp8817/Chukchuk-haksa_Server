package com.chukchuk.haksa.global.config;

import com.chukchuk.haksa.domain.user.service.OidcService;
import com.chukchuk.haksa.global.security.service.OidcProvider;
import com.chukchuk.haksa.infrastructure.oidc.AppleOidcService;
import com.chukchuk.haksa.infrastructure.oidc.KakaoOidcService;
import java.util.Map;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OIDCConfig {

  @Bean
  public Map<OidcProvider, OidcService> oidcServices(
      KakaoOidcService kakaoOidcService, AppleOidcService appleOidcService) {
    return Map.of(
        OidcProvider.KAKAO, kakaoOidcService,
        OidcProvider.APPLE, appleOidcService);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }
}
