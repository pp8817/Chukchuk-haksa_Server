package com.chukchuk.haksa.infrastructure.oidc;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** OIDC 제공자의 JWKS를 조회하고 서명 키를 선택한다. */
@Component
@RequiredArgsConstructor
public class OidcJwksClient {

  private static final String CACHE_NAME = "oidcKeys";

  private final RestTemplate restTemplate;
  private final CacheManager cacheManager;

  /**
   * 요청 조건에 맞는 데이터를 조회한다.
   *
   * @param cacheKey 캐시 key 값
   * @param url url 값
   * @return 조회
   */
  @Cacheable(cacheNames = CACHE_NAME, key = "#cacheKey")
  public JsonNode fetchKeys(String cacheKey, String url) {
    return restTemplate.getForObject(url, JsonNode.class);
  }

  /**
   * 현재 상태를 요청 내용에 맞게 갱신한다.
   *
   * @param cacheKey 캐시 key 값
   * @param url url 값
   * @return json node 결과
   */
  public JsonNode refreshKeys(String cacheKey, String url) {
    JsonNode keys = restTemplate.getForObject(url, JsonNode.class);
    Cache cache = cacheManager.getCache(CACHE_NAME);
    if (cache != null) {
      cache.put(cacheKey, keys);
    }
    return keys;
  }
}
