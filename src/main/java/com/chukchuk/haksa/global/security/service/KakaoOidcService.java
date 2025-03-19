package com.chukchuk.haksa.global.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KakaoOidcService {
    private final KakaoOidcPublicKeyService publicKeyService;
    // test

    public Claims verifyIdToken(String idToken) throws Exception {
        JsonNode jwks = publicKeyService.getKakaoOidcPublicKey();

        // ID 토큰의 헤더에서 kid(Key ID) 추출
        String[] parts = idToken.split("\\.");
        String headerJson = new String(Base64.getDecoder().decode(parts[0]));
        String kid = new ObjectMapper().readTree(headerJson).get("kid").asText();

        // kid에 해당하는 공개 키 찾기
        JsonNode keyNode = findMatchingKey(jwks, kid);
        if (keyNode == null) {
            throw new IllegalArgumentException("일치하는 공개키가 없습니다.");
        }

        // 공개 키 생성
        PublicKey publicKey = createPublicKey(keyNode);

        // 토큰 검증 및 파싱
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken)
                .getBody();

    }

    private JsonNode findMatchingKey(JsonNode jwks, String kid) {
        // 일치하는 공개키가 있다면 반환
        for (JsonNode key : jwks.get("keys")) {
            if (key.get("kid").asText().equals(kid)) {
                return key;
            }
        }
        return null;
    }

    private PublicKey createPublicKey(JsonNode keyNode) throws Exception{
        byte[] decodedKey = Base64.getDecoder().decode(keyNode.get("n").asText());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return java.security.KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

}
