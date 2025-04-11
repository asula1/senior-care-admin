package com.seniorcare.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "steps_count")
    private Integer stepsCount;
    
    @Column(name = "calories_burned")
    private Double caloriesBurned;
    
    @Column(name = "sleep_duration_minutes")
    private Integer sleepDurationMinutes;
    
    @Column(name = "sleep_quality")
    private String sleepQuality;
    
    @Column(name = "blood_pressure_systolic")
    private Integer bloodPressureSystolic;
    
    @Column(name = "blood_pressure_diastolic")
    private Integer bloodPressureDiastolic;
    
    @Column(name = "blood_oxygen")
    private Double bloodOxygen;
    
    @Column(name = "body_temperature")
    private Double bodyTemperature;
    
    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
    
    @Column(name = "device_id")
    private String deviceId;
    
    @Column(name = "data_source")
    private String dataSource;
}
