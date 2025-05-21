package com.chukchuk.haksa.application.api;

import com.chukchuk.haksa.application.academic.dto.SyncAcademicRecordResult;
import com.chukchuk.haksa.application.api.dto.PortalLoginResponse;
import com.chukchuk.haksa.application.api.dto.ScrapingResponse;
import com.chukchuk.haksa.application.api.wrapper.PortalLoginApiResponse;
import com.chukchuk.haksa.application.api.wrapper.ScrapingApiResponse;
import com.chukchuk.haksa.application.portal.InitializePortalConnectionService;
import com.chukchuk.haksa.application.portal.RefreshPortalConnectionService;
import com.chukchuk.haksa.application.portal.SyncAcademicRecordService;
import com.chukchuk.haksa.domain.user.model.User;
import com.chukchuk.haksa.domain.user.service.UserService;
import com.chukchuk.haksa.global.common.response.SuccessResponse;
import com.chukchuk.haksa.global.common.response.wrapper.ErrorResponseWrapper;
import com.chukchuk.haksa.global.exception.CommonException;
import com.chukchuk.haksa.global.exception.ErrorCode;
import com.chukchuk.haksa.global.security.CustomUserDetails;
import com.chukchuk.haksa.infrastructure.portal.exception.PortalScrapeException;
import com.chukchuk.haksa.infrastructure.portal.model.PortalConnectionResult;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.repository.PortalRepository;
import com.chukchuk.haksa.infrastructure.redis.RedisPortalCredentialStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/suwon-scrape")
@RequiredArgsConstructor
@Tag(name = "Suwon Scraping", description = "수원대학교 포털 크롤링 관련 API")
@Slf4j
public class SuwonScrapeController {

    private final PortalRepository portalRepository;
    private final InitializePortalConnectionService initializePortalConnectionService;
    private final RefreshPortalConnectionService refreshPortalConnectionService;
    private final SyncAcademicRecordService syncAcademicRecordService;
    private final RedisPortalCredentialStore redisStore;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(
            summary = "포털 로그인",
            description = "수원대학교 포털 로그인 후 Redis에 계정 정보를 저장합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PortalLoginApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 실패 (ErrorCode: P01, 포털 로그인 실패)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류 (ErrorCode: INTERNAL_ERROR, 서버 오류)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class)))
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<PortalLoginResponse>> login(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @Parameter(description = "포털 로그인 ID", required = true) String username,
            @RequestParam @Parameter(description = "포털 로그인 비밀번호", required = true) String password
    ) {
//        portalRepository.login(username, password); // 포털 크롤링 시작 ('/api/suwon-scrape/start') 로직에서 담덩
        String userId = userDetails.getUsername();
        redisStore.save(userId, username, password);

        return ResponseEntity.ok(SuccessResponse.of(new PortalLoginResponse("로그인 성공")));
    }

    @PostMapping("/start")
    @Operation(
            summary = "포털 데이터 크롤링 및 동기화",
            description = "Redis에 저장된 포털 로그인 정보를 사용하여 데이터를 크롤링하고 초기화 및 학업 이력을 동기화합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "동기화 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScrapingApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요 (ErrorCode: C01, 세션 만료)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류 (ErrorCode: C02, 포털 크롤링 실패)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class)))
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<ScrapingResponse>> startScraping(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        log.info("[START] 포털 동기화 시작: userId={}", userId); // 요청 시작

        String[] credentials = loadPortalCredentials(userId);
        String username = credentials[0];
        String password = credentials[1];

        PortalData portalData;
        try {
            portalData = portalRepository.fetchPortalData(username, password);
            log.info("[PORTAL] 포털 데이터 크롤링 성공");
        } catch (Exception e) {
            log.error("[PORTAL] 포털 데이터 크롤링 실패", e);
            throw new PortalScrapeException(ErrorCode.SCRAPING_FAILED);
        }

        UUID uuUserId = UUID.fromString(userId);
        PortalConnectionResult portalConnectionResult = initializePortalConnectionService.executeWithPortalData(uuUserId, portalData);
        if (!portalConnectionResult.isSuccess()) {
            log.warn("[INIT] 포털 초기화 실패: userId={}, reason={}", userId, portalConnectionResult.error());
            throw new PortalScrapeException(ErrorCode.SCRAPING_FAILED);
        }

        SyncAcademicRecordResult syncResult = syncAcademicRecordService.executeWithPortalData(uuUserId, portalData);
        if (!syncResult.isSuccess()) {
            log.warn("[SYNC] 학업 동기화 실패: userId={}, reason={}", userId, syncResult.getError());
            throw new PortalScrapeException(ErrorCode.SCRAPING_FAILED);
        }

        redisStore.clear(userId);
        log.info("[COMPLETE] 동기화 완료: userId={}", userId); // 완료 로그

        // 사용자 포털 연결 정보 설정
        User user = userService.getUserById(uuUserId);
        user.markPortalConnected(Instant.now());
        userService.save(user);


        ScrapingResponse response = new ScrapingResponse(UUID.randomUUID().toString(), portalConnectionResult.studentInfo());
        return ResponseEntity.accepted().body(SuccessResponse.of(response));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "포털 정보 재연동 및 학업 이력 동기화",
            description = "포털 정보를 재연동하고 학업 이력을 다시 동기화합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "재연동 및 동기화 성공",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScrapingApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요 (ErrorCode: C01, 세션 만료)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류 (ErrorCode: C03, 재연동 실패)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseWrapper.class)))
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<ScrapingResponse>> refreshAndSync(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        log.info("[START] 포털 동기화 시작: userId={}", userId); // 요청 시작

        String[] credentials = loadPortalCredentials(userId);
        String username = credentials[0];
        String password = credentials[1];

        PortalData portalData;
        try {
            portalData = portalRepository.fetchPortalData(username, password);
            log.info("[PORTAL] 포털 데이터 크롤링 성공");
        } catch (Exception e) {
            log.error("[PORTAL] 포털 데이터 크롤링 실패", e);
            throw new PortalScrapeException(ErrorCode.SCRAPING_FAILED);
        }

        UUID uuUserId = UUID.fromString(userId);
        PortalConnectionResult portalConnectionResult = refreshPortalConnectionService.executeWithPortalData(uuUserId, portalData);
        if (!portalConnectionResult.isSuccess()) {
            log.warn("[INIT] 포털 초기화 실패: userId={}, reason={}", userId, portalConnectionResult.error());
            throw new PortalScrapeException(ErrorCode.SCRAPING_FAILED);
        }

        SyncAcademicRecordResult syncResult = syncAcademicRecordService.executeForRefreshPortalData(uuUserId, portalData);
        if (!syncResult.isSuccess()) {
            log.warn("[SYNC] 학업 동기화 실패: userId={}, reason={}", userId, syncResult.getError());
            throw new PortalScrapeException(ErrorCode.REFRESH_FAILED);
        }

        redisStore.clear(userId);
        log.info("[COMPLETE] 동기화 완료: userId={}", userId); // 완료 로그

        User user = userService.getUserById(uuUserId);
        user.updateLastSyncedAt(Instant.now());
        userService.save(user);

        ScrapingResponse response = new ScrapingResponse(UUID.randomUUID().toString(), portalConnectionResult.studentInfo());
        return ResponseEntity.accepted().body(SuccessResponse.of(response));
    }

    private String[] loadPortalCredentials(String userId) {
        String username = redisStore.getUsername(userId);
        String password = redisStore.getPassword(userId);
        if (username == null || password == null) {
            throw new CommonException(ErrorCode.SESSION_EXPIRED);
        }
        return new String[]{username, password};
    }
}