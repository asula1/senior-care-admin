package com.seniorcare.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeniorHealthStatusDto {
    private Long userId;
    private String fullName;
    private Integer heartRate;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Double bloodOxygen;
    private Double bodyTemperature;
    private Integer stepsCount;
    private LocalDateTime lastUpdated;
    private String healthStatus; // NORMAL, WARNING, CRITICAL
    private Boolean hasActiveAlerts;
    private Long activeAlertsCount;
    private Long missedMedicationsCount;
}