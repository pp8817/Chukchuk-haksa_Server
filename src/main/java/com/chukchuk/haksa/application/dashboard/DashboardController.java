package com.chukchuk.haksa.application.dashboard;

import com.chukchuk.haksa.application.dashboard.dto.DashboardResponse;
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
public class DashboardController {

    private final DashboardService dashboardService;

    /* 사용자 대시보드 정보 조회 API */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails
            ) {

        String email = userDetails.getUsername();
        DashboardResponse dashboard = dashboardService.getDashboard(email);

        return ResponseEntity.ok(dashboard);
    }
}
