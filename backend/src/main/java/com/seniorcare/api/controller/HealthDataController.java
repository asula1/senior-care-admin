package com.seniorcare.api.controller;

import com.seniorcare.api.dto.health.HealthDataDto;
import com.seniorcare.api.dto.health.HealthDataRequest;
import com.seniorcare.api.security.UserPrincipal;
import com.seniorcare.api.service.HealthDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<HealthDataDto>> getUserHealthData(@PathVariable Long userId) {
        List<HealthDataDto> healthData = healthDataService.getUserHealthData(userId);
        return ResponseEntity.ok(healthData);
    }

    @GetMapping("/user/{userId}/range")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<HealthDataDto>> getUserHealthDataByTimeRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<HealthDataDto> healthData = healthDataService.getUserHealthDataByTimeRange(userId, startTime, endTime);
        return ResponseEntity.ok(healthData);
    }

    @GetMapping("/user/{userId}/latest")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<HealthDataDto> getLatestHealthData(@PathVariable Long userId) {
        HealthDataDto healthData = healthDataService.getLatestHealthData(userId);
        return ResponseEntity.ok(healthData);
    }

    @PostMapping("/user/{userId}/record")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<HealthDataDto> recordHealthData(
            @PathVariable Long userId,
            @Valid @RequestBody HealthDataRequest request) {
        HealthDataDto savedData = healthDataService.recordHealthData(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }

    @PostMapping("/me/record")
    public ResponseEntity<HealthDataDto> recordMyHealthData(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody HealthDataRequest request) {
        HealthDataDto savedData = healthDataService.recordHealthData(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedData);
    }
}
