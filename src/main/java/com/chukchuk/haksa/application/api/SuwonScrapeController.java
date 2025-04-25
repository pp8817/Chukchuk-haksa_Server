package com.chukchuk.haksa.application.api;

import com.chukchuk.haksa.application.academic.dto.SyncAcademicRecordResult;
import com.chukchuk.haksa.application.api.dto.PortalLoginMessageResponse;
import com.chukchuk.haksa.application.api.dto.RefreshScrapingResponse;
import com.chukchuk.haksa.application.api.dto.StartScrapingResponse;
import com.chukchuk.haksa.application.api.wrapper.PortalLoginApiResponse;
import com.chukchuk.haksa.application.api.wrapper.RefreshScrapingApiResponse;
import com.chukchuk.haksa.application.api.wrapper.StartScrapingApiResponse;
import com.chukchuk.haksa.application.portal.InitializePortalConnectionService;
import com.chukchuk.haksa.application.portal.RefreshPortalConnectionService;
import com.chukchuk.haksa.application.portal.SyncAcademicRecordService;
import com.chukchuk.haksa.infrastructure.portal.exception.PortalLoginException;
import com.chukchuk.haksa.infrastructure.portal.model.InitializePortalConnectionResult;
import com.chukchuk.haksa.infrastructure.portal.model.PortalData;
import com.chukchuk.haksa.infrastructure.portal.repository.PortalRepository;
import com.chukchuk.haksa.infrastructure.redis.RedisPortalCredentialStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
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

    @PostMapping("/login")
    @Operation(
            summary = "포털 로그인",
            description = "수원대학교 포털 로그인 후 Redis에 계정 정보를 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = PortalLoginApiResponse.class)))
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> login(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @Parameter(description = "포털 로그인 ID") String username,
            @RequestParam @Parameter(description = "포털 로그인 비밀번호") String password
    ) {

        try {
            portalRepository.login(username, password);
            String userId = userDetails.getUsername();

            redisStore.save(userId, username, password);

            return ResponseEntity.ok(com.chukchuk.haksa.global.common.response.ApiResponse.success(new PortalLoginMessageResponse("로그인 성공")));
        } catch (PortalLoginException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/start")
    @Operation(
            summary = "수원대학교 포털 데이터 크롤링 및 동기화",
            description = "Redis에 저장된 포털 로그인 정보를 사용하여 데이터를 크롤링합니다.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "동기화 성공",
                            content = @Content(schema = @Schema(implementation = StartScrapingApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> startScraping(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String taskId = UUID.randomUUID().toString();

        try {
            String userId = userDetails.getUsername();
            String username = redisStore.getUsername(userId);
            String password = redisStore.getPassword(userId);

            if (username == null || password == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            // 1. 포털 데이터 fetch
            PortalData portalData = portalRepository.fetchPortalData(username, password);

            // 2. 포털 초기화
            UUID uuUserId = UUID.fromString(userId);
            InitializePortalConnectionResult initResult = initializePortalConnectionService.executeWithPortalData(uuUserId, portalData);
            if (!initResult.isSuccess()) {
                throw new RuntimeException(initResult.error());
            }

            // 3. 학업 이력 동기화
            SyncAcademicRecordResult syncResult = syncAcademicRecordService.executeWithPortalData(uuUserId, portalData);
            if (!syncResult.isSuccess()) {
                throw new RuntimeException(syncResult.getError());
            }

            log.info("syncAcademicRecordUseCase Completed");

            // 4. 성공 결과 반환
            StartScrapingResponse response = new StartScrapingResponse(taskId, initResult.studentInfo());

            // 세션(포털 로그인 정보) 삭제
            redisStore.clear(userId);

            return ResponseEntity.accepted().body(com.chukchuk.haksa.global.common.response.ApiResponse.success(response));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("taskId", taskId);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "포털 정보 재연동 및 동기화",
            description = "포털 정보를 재연동하고 학업 이력을 동기화합니다.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "동기화 성공",
                            content = @Content(schema = @Schema(implementation = RefreshScrapingApiResponse.class))),
                    @ApiResponse(responseCode = "401", description = "로그인 필요"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> refreshAndSync(@AuthenticationPrincipal UserDetails userDetails) {
        String taskId = UUID.randomUUID().toString();

        try {
            String userId = userDetails.getUsername();
            String username = redisStore.getUsername(userId);
            String password = redisStore.getPassword(userId);

            if (username == null || password == null) {
                return ResponseEntity.status(401).body("로그인이 필요합니다.");
            }

            // 1. 포털 데이터 크롤링
            PortalData portalData = portalRepository.fetchPortalData(username, password);

            // 2. 포털 정보 재연동
            UUID uuUserId = UUID.fromString(userId);
            boolean refreshed = refreshPortalConnectionService.executeWithPortalData(uuUserId, portalData);
            if (!refreshed) {
                throw new RuntimeException("포털 정보 재연동 실패");
            }

            // 3. 학업 이력 동기화
            SyncAcademicRecordResult syncResult = syncAcademicRecordService.executeWithPortalData(uuUserId, portalData);
            if (!syncResult.isSuccess()) {
                throw new RuntimeException(syncResult.getError());
            }

            // 4. 결과 반환
            RefreshScrapingResponse response = new RefreshScrapingResponse(taskId, "포털 재연동 및 학업 이력 동기화 완료");

            // Redis 로그인 정보 삭제
            redisStore.clear(userId);

            return ResponseEntity.accepted().body(com.chukchuk.haksa.global.common.response.ApiResponse.success(response));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("taskId", taskId);
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
