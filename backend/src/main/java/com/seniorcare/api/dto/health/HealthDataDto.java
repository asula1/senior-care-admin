package com.seniorcare.api.dto.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthDataDto {
    
    private Long id;
    private Long userId;
    private Integer heartRate;
    private Integer stepsCount;
    private Double caloriesBurned;
    private Integer sleepDurationMinutes;
    private String sleepQuality;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Double bloodOxygen;
    private Double bodyTemperature;
    private LocalDateTime recordedAt;
    private String deviceId;
}
