package com.chukchuk.haksa.global.logging.annotation;

import java.time.Duration;

/** 메서드 실행 시간 로깅 대상을 표시한다. */
public final class LogTime {
  private LogTime() {}

  /** 현재 시각 (ns). */
  public static long start() {
    return System.nanoTime();
  }

  /** 경과 시간 (ms). */
  public static long elapsedMs(long startNanos) {
    return Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
  }
}
