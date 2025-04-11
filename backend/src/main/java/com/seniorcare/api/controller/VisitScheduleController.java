package com.seniorcare.api.controller;

import com.seniorcare.api.dto.ApiResponse;
import com.seniorcare.api.dto.visit.VisitRequest;
import com.seniorcare.api.dto.visit.VisitScheduleDto;
import com.seniorcare.api.dto.visit.VisitStatusUpdateRequest;
import com.seniorcare.api.model.VisitSchedule;
import com.seniorcare.api.service.VisitScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitScheduleController {

    private final VisitScheduleService visitService;

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER')")
    public ResponseEntity<List<VisitScheduleDto>> getAllUpcomingVisits() {
        List<VisitScheduleDto> visits = visitService.getAllUpcomingVisits();
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/senior/{seniorId}/upcoming")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SOCIAL_WORKER') or #seniorId == authentication.principal.id")
    public ResponseEntity<List<VisitScheduleDto>> getUpcomingVisitsBySenior(@PathVariable Long seniorId) {
        List<VisitScheduleDto> visits = visitService.getUpcomingVisitsBySenior(seniorId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/senior/{seniorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SOCIAL_WORKER') or #seniorId == authentication.principal.id")
    public ResponseEntity<List<VisitScheduleDto>> getVisitsBySenior(@PathVariable Long seniorId) {
        List<VisitScheduleDto> visits = visitService.getVisitsBySenior(seniorId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/social-worker/{socialWorkerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #socialWorkerId == authentication.principal.id")
    public ResponseEntity<List<VisitScheduleDto>> getVisitsBySocialWorker(@PathVariable Long socialWorkerId) {
        List<VisitScheduleDto> visits = visitService.getVisitsBySocialWorker(socialWorkerId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/range")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_SENIOR')")
    public ResponseEntity<List<VisitScheduleDto>> getVisitsByDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<VisitScheduleDto> visits = visitService.getVisitsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(visits);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER')")
    public ResponseEntity<VisitScheduleDto> scheduleVisit(@Valid @RequestBody VisitRequest request) {
        VisitScheduleDto visit = visitService.scheduleVisit(
                request.getSeniorId(),
                request.getSocialWorkerId(),
                request.getStartTime(),
                request.getEndTime(),
                request.getVisitPurpose(),
                request.getNotes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(visit);
    }

    @PutMapping("/{visitId}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER')")
    public ResponseEntity<VisitScheduleDto> updateVisitStatus(
            @PathVariable Long visitId,
            @Valid @RequestBody VisitStatusUpdateRequest request) {
        VisitScheduleDto visit = visitService.updateVisitStatus(visitId, request.getStatus(), request.getNotes());
        return ResponseEntity.ok(visit);
    }

    @PutMapping("/{visitId}/reschedule")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER')")
    public ResponseEntity<VisitScheduleDto> rescheduleVisit(
            @PathVariable Long visitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {
        VisitScheduleDto visit = visitService.rescheduleVisit(visitId, newStartTime, newEndTime);
        return ResponseEntity.ok(visit);
    }

    @PutMapping("/{visitId}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SOCIAL_WORKER', 'ROLE_SENIOR')")
    public ResponseEntity<ApiResponse> cancelVisit(
            @PathVariable Long visitId,
            @RequestParam(required = false) String reason) {
        visitService.cancelVisit(visitId, reason);
        return ResponseEntity.ok(new ApiResponse(true, "방문이 취소되었습니다"));
    }
}
