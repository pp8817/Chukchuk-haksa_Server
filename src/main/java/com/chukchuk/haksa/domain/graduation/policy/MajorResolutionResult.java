package com.chukchuk.haksa.domain.graduation.policy;

/**
 * 전공 resolution 결과 데이터를 전달한다.
 *
 * @param primaryMajorId 주전공 식별자
 * @param secondaryMajorId 복수전공 식별자
 */
public record MajorResolutionResult(Long primaryMajorId, Long secondaryMajorId) {}
