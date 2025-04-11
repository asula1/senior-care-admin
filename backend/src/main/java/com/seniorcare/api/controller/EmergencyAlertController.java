package com.seniorcare.api.controller;

import com.seniorcare.api.dto.ApiResponse;
import com.seniorcare.api.dto.alert.AlertResolutionRequest;
import com.seniorcare.api.dto.alert.EmergencyAlertDto;
import com.seniorcare.api.dto.alert.EmergencyAlertRequest;
import com.seniorcare.api.security.UserPrincipal;
import com.seniorcare.api.service.EmergencyAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class EmergencyAlertController {

    private final EmergencyAlertService alertService;

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL', 'ROLE_GUARDIAN')")
    public ResponseEntity<List<EmergencyAlertDto>> getActiveAlerts() {
        List<EmergencyAlertDto> alerts = alertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/resolved")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL', 'ROLE_GUARDIAN')")
    public ResponseEntity<List<EmergencyAlertDto>> getResolvedAlerts() {
        List<EmergencyAlertDto> alerts = alertService.getResolvedAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<EmergencyAlertDto>> getActiveAlertsByUser(@PathVariable Long userId) {
        List<EmergencyAlertDto> alerts = alertService.getActiveAlertsByUser(userId);
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<EmergencyAlertDto>> getAllAlertsByUser(@PathVariable Long userId) {
        List<EmergencyAlertDto> alerts = alertService.getAllAlertsByUser(userId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<EmergencyAlertDto> createAlert(
            @PathVariable Long userId,
            @Valid @RequestBody EmergencyAlertRequest request) {
        EmergencyAlertDto alert = alertService.createAlert(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @PostMapping("/me/sos")
    public ResponseEntity<EmergencyAlertDto> createSosAlert(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        EmergencyAlertDto alert = alertService.createSosAlert(currentUser.getId(), latitude, longitude);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @PostMapping("/me/fall")
    public ResponseEntity<EmergencyAlertDto> createFallAlert(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        EmergencyAlertDto alert = alertService.createFallDetectionAlert(currentUser.getId(), latitude, longitude);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @PutMapping("/{alertId}/resolve")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL', 'ROLE_GUARDIAN')")
    public ResponseEntity<EmergencyAlertDto> resolveAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AlertResolutionRequest request) {
        EmergencyAlertDto resolvedAlert = alertService.resolveAlert(alertId, currentUser.getId(), request);
        return ResponseEntity.ok(resolvedAlert);
    }

    @PutMapping("/{alertId}/acknowledge")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_MEDICAL', 'ROLE_GUARDIAN')")
    public ResponseEntity<EmergencyAlertDto> acknowledgeAlert(
            @PathVariable Long alertId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        EmergencyAlertDto acknowledgedAlert = alertService.acknowledgeAlert(alertId, currentUser.getId());
        return ResponseEntity.ok(acknowledgedAlert);
    }
}
