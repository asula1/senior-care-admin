package com.seniorcare.api.controller;

import com.seniorcare.api.dto.ApiResponse;
import com.seniorcare.api.dto.medication.MedicationDto;
import com.seniorcare.api.dto.medication.MedicationReminderDto;
import com.seniorcare.api.dto.medication.MedicationRequest;
import com.seniorcare.api.dto.medication.MedicationStatisticsDto;
import com.seniorcare.api.dto.medication.ReminderRequest;
import com.seniorcare.api.security.UserPrincipal;
import com.seniorcare.api.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Medication", description = "약물 관리 API")
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<MedicationDto>> getUserMedications(@PathVariable Long userId) {
        List<MedicationDto> medications = medicationService.getUserMedications(userId);
        return ResponseEntity.ok(medications);
    }

    @GetMapping("/user/{userId}/active")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<MedicationDto>> getActiveMedications(@PathVariable Long userId) {
        List<MedicationDto> medications = medicationService.getActiveMedications(userId);
        return ResponseEntity.ok(medications);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<MedicationDto> addMedication(
            @PathVariable Long userId,
            @Valid @RequestBody MedicationRequest request) {
        MedicationDto medication = medicationService.addMedication(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medication);
    }

    @PutMapping("/{medicationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDICAL')")
    public ResponseEntity<MedicationDto> updateMedication(
            @PathVariable Long medicationId,
            @Valid @RequestBody MedicationRequest request) {
        MedicationDto medication = medicationService.updateMedication(medicationId, request);
        return ResponseEntity.ok(medication);
    }

    @DeleteMapping("/{medicationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDICAL')")
    public ResponseEntity<ApiResponse> deleteMedication(@PathVariable Long medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.ok(new ApiResponse(true, "약물 정보가 삭제되었습니다"));
    }

    @PostMapping("/reminders")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDICAL', 'ROLE_SENIOR', 'ROLE_GUARDIAN')")
    public ResponseEntity<MedicationReminderDto> addReminder(@Valid @RequestBody ReminderRequest request) {
        MedicationReminderDto reminder = medicationService.addReminder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reminder);
    }

    @GetMapping("/reminders/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MEDICAL') or #userId == authentication.principal.id")
    public ResponseEntity<List<MedicationReminderDto>> getUserReminders(@PathVariable Long userId) {
        List<MedicationReminderDto> reminders = medicationService.getUserReminders(userId);
        return ResponseEntity.ok(reminders);
    }

    @PutMapping("/reminders/{reminderId}/taken")
    public ResponseEntity<MedicationReminderDto> markReminderAsTaken(
            @PathVariable Long reminderId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        MedicationReminderDto reminder = medicationService.markReminderAsTaken(reminderId);
        return ResponseEntity.ok(reminder);
    }

    @PutMapping("/reminders/{reminderId}/missed")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDICAL', 'ROLE_GUARDIAN')")
    public ResponseEntity<MedicationReminderDto> markReminderAsMissed(@PathVariable Long reminderId) {
        MedicationReminderDto reminder = medicationService.markReminderAsMissed(reminderId);
        return ResponseEntity.ok(reminder);
    }
    
    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "사용자의 약물 복용 통계 조회", description = "사용자의 약물 복용 통계 및 복약 준수율을 조회합니다")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDICAL', 'ROLE_GUARDIAN') or #userId == authentication.principal.id")
    public ResponseEntity<MedicationStatisticsDto> getMedicationStatistics(@PathVariable Long userId) {
        MedicationStatisticsDto statistics = medicationService.getMedicationStatistics(userId);
        return ResponseEntity.ok(statistics);
    }
}
