package com.seniorcare.api.controller;

import com.seniorcare.api.dto.dashboard.AlertSummaryDto;
import com.seniorcare.api.dto.dashboard.DashboardSummaryDto;
import com.seniorcare.api.dto.dashboard.SeniorHealthStatusDto;
import com.seniorcare.api.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "모니터링 대시보드 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "대시보드 요약 정보 조회", description = "시스템 전체 통계 요약 정보를 조회합니다")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL')")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/seniors/health")
    @Operation(summary = "모든 노인 건강 상태 조회", description = "모든 노인 사용자의 건강 상태 정보를 조회합니다")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL')")
    public ResponseEntity<List<SeniorHealthStatusDto>> getAllSeniorsHealthStatus() {
        return ResponseEntity.ok(dashboardService.getAllSeniorsHealthStatus());
    }

    @GetMapping("/seniors/{seniorId}/health")
    @Operation(summary = "특정 노인 건강 상태 조회", description = "특정 노인 사용자의 건강 상태 정보를 조회합니다")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL', 'ROLE_GUARDIAN') or #seniorId == authentication.principal.id")
    public ResponseEntity<SeniorHealthStatusDto> getSeniorHealthStatus(@PathVariable Long seniorId) {
        return ResponseEntity.ok(dashboardService.getSeniorHealthStatus(seniorId));
    }

    @GetMapping("/alerts/summary")
    @Operation(summary = "알림 요약 정보 조회", description = "시스템의 알림 통계 및 요약 정보를 조회합니다")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL')")
    public ResponseEntity<AlertSummaryDto> getAlertSummary() {
        return ResponseEntity.ok(dashboardService.getAlertSummary());
    }
}