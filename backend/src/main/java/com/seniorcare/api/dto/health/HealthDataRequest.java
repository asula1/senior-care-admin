package com.seniorcare.api.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthDataRequest {
    
    private Integer heartRate;
    private Integer stepsCount;
    private Double caloriesBurned;
    private Integer sleepDurationMinutes;
    private String sleepQuality;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Double bloodOxygen;
    private Double bodyTemperature;
    private String deviceId;
}
