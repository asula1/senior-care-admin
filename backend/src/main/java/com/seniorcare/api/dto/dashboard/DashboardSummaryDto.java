package com.seniorcare.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private Long totalSeniors;
    private Long totalGuardians;
    private Long totalSocialWorkers;
    
    private Long activeAlerts;
    private Long resolvedAlerts;
    private Long totalAlerts;
    
    private Long scheduledVisits;
    private Long completedVisits;
    private Long missedVisits;
    
    private Long activeMedications;
    private Long medicationReminders;
    private Long missedMedications;
}