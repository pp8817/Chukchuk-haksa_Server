package com.chukchuk.haksa.global.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/** 척척학사의 scheduling 애플리케이션 설정을 제공한다. */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "scraping.scheduler", name = "enabled", havingValue = "true")
public class SchedulingConfig {

  /**
   * 예약 작업에 사용할 스레드 풀 스케줄러를 생성한다.
   *
   * @return thread pool task scheduler 결과
   */
  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(2);
    taskScheduler.setThreadNamePrefix("scheduler-");
    taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
    taskScheduler.setAwaitTerminationSeconds(30);
    return taskScheduler;
  }
}
