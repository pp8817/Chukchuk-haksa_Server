package com.chukchuk.haksa.global.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoOidcPublicKeyService {
    private static final String KAKAO_JWKS_URL = "https://kauth.kakao.com/.well-known/jwks.json";

    public JsonNode getKakaoOidcPublicKey() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(KAKAO_JWKS_URL, JsonNode.class);
    }
}
