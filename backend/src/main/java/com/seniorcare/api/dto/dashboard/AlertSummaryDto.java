package com.seniorcare.api.dto.dashboard;

import com.seniorcare.api.model.EmergencyAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSummaryDto {
    private Long totalAlerts;
    private Long activeAlerts;
    private Long acknowledgedAlerts;
    private Long resolvedAlerts;
    private Long falseAlarms;
    
    private Map<EmergencyAlert.AlertType, Long> alertsByType;
    private Map<String, Long> alertsByHour;
    private Map<String, Long> alertsByDay;
    
    private LocalDateTime lastAlertTime;
    private EmergencyAlert.AlertType mostCommonAlertType;
}