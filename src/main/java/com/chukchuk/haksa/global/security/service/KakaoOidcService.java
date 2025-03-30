package com.chukchuk.haksa.global.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOidcService {
    private final KakaoOidcPublicKeyService publicKeyService;

    @Value("${security.appKey}")
    private String appKey;

    public Claims verifyIdToken(String idToken, String expectedNonce) throws Exception {
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
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken)
                .getBody();

        validateClaims(expectedNonce, claims);

        return claims;
    }

    private PublicKey createPublicKey(JsonNode keyNode) throws Exception{
        // RSA 키 파라미터 추출
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(keyNode.get("n").asText()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(keyNode.get("e").asText()));

        // RSA 공개키 생성
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    private void validateClaims(String expectedNonce, Claims claims) throws Exception {

        Date expiration = claims.getExpiration();
        if (expiration == null || expiration.before(new Date())) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        if (!claims.getIssuer().equals("https://kauth.kakao.com")) {
            throw new IllegalArgumentException("유효하지 않은 iss 입니다.");
        }

        // aud가 배열로 오는 경우 대비
        Object audClaim = claims.get("aud");
        if (audClaim instanceof String) { // 배열이 아닌 경우
            if (!appKey.equals(audClaim)) {
                throw new IllegalArgumentException("유효하지 않은 aud 입니다.");
            }
        } else if (audClaim instanceof java.util.List) { // 배열인 경우
            if (!((java.util.List<?>) audClaim).contains(appKey)) {
                throw new IllegalArgumentException("유효하지 않은 aud 입니다.");
            }
        } else {
            throw new IllegalArgumentException("aud 클레임 형식이 올바르지 않습니다.");
        }

        String hashedNonce = hashSHA256(expectedNonce);

        String nonce = claims.get("nonce", String.class);

        if (!nonce.equals(hashedNonce)) {
            throw new IllegalArgumentException("유효하지 않은 nonce 입니다.");
        }
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

    private String hashSHA256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
