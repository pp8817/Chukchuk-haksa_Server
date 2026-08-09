package com.chukchuk.haksa.infrastructure.portal.model;

/**
 * 계층 간 전달할 ranking 데이터를 표현한다.
 *
 * @param rank rank 값
 * @param total total 값
 */
public record Ranking(int rank, int total) {}
