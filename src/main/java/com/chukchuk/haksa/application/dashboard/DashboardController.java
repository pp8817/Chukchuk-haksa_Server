package com.chukchuk.haksa.application.dashboard;

import com.chukchuk.haksa.application.dashboard.dto.DashboardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "사용자 대시보드 관련 API")
public class DashboardController {

    private final DashboardService dashboardService;

    /* 사용자 대시보드 정보 조회 API */
    @GetMapping
    @Operation(summary = "사용자 대시보드 정보 조회", description = "로그인된 사용자의 대시보드 정보를 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails
            ) {

        String email = userDetails.getUsername();
        DashboardResponse dashboard = dashboardService.getDashboard(email);

        return ResponseEntity.ok(dashboard);
    }
}
