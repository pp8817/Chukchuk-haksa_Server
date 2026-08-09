package com.chukchuk.haksa.application.portal;

import static com.chukchuk.haksa.global.logging.config.LoggingThresholds.SLOW_MS;
import static com.chukchuk.haksa.infrastructure.portal.model.PortalConnectionResult.failure;
import static com.chukchuk.haksa.infrastructure.portal.model.PortalConnectionResult.success;

import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.repository.UserPortalConnectionRepository;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.logging.annotation.LogTime;
import com.chukchuk.haksa.infrastructure.portal.model.PortalConnectionResult;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.model.PortalStudentInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* 포털 연동 초기화 유스케이스 실행 */
/** 척척학사의 initialize 포털 연동 비즈니스 흐름을 처리한다. */
@Service
@RequiredArgsConstructor
@Slf4j
public class InitializePortalConnectionService {

  private final UserPortalConnectionRepository userPortalConnectionRepository;
  private final UserService userService;
  private final PortalStudentDataMapper portalStudentDataMapper;

  /**
   * 척척학사의 execute with 포털 data 대상을 처리한다.
   *
   * @param userId 사용자 식별자
   * @param portalData 포털 학사 데이터
   * @return 포털 연동 결과
   */
  @Transactional
  public PortalConnectionResult executeWithPortalData(UUID userId, PortalData portalData) {
    long t0 = LogTime.start();
    try {
      // 사용자 조회 및 포털 연동 여부 확인
      User user = userService.getUserById(userId);
      if (user.getPortalConnected()) {
        log.warn("[BIZ] portal.init.skipped userId={} reason=already_connected", userId);
        return failure("이미 포털 계정과 연동된 사용자입니다.");
      }

      if (portalData == null || portalData.student() == null) {
        log.warn("[BIZ] portal.init.fail userId={} reason=portal_data_null", userId);
        return failure("포털 데이터가 존재하지 않습니다.");
      }

      PortalStudentInfo raw = portalData.student();
      PortalStudentDataMapper.PortalStudentData portalStudentData =
          portalStudentDataMapper.toStudentData(raw);
      if (portalStudentData == null) {
        log.warn("[BIZ] portal.init.fail userId={} reason=student_data_mapping_failed", userId);
        return failure("포털 학생 정보 초기화 실패");
      }

      // 포털 연동 초기화
      userPortalConnectionRepository.initializePortalConnection(
          user, portalStudentData.studentData());

      long tookMs = LogTime.elapsedMs(t0);
      if (tookMs >= SLOW_MS) {
        log.info("[BIZ] portal.init.done userId={} took_ms={}", userId, tookMs);
      }
      return success(raw.studentCode(), portalStudentData.studentInfo());
    } catch (Exception e) {
      log.warn("[BIZ] portal.init.ex userId={} ex={}", userId, e.getClass().getSimpleName(), e);
      throw new RuntimeException("포털 연동 중 오류가 발생했습니다.", e);
    }
  }
}
