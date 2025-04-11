package com.seniorcare.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;
    
    @Column(name = "alert_message")
    private String alertMessage;
    
    @Column(name = "location_latitude")
    private Double locationLatitude;
    
    @Column(name = "location_longitude")
    private Double locationLongitude;
    
    @Column(name = "location_address")
    private String locationAddress;
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_status", nullable = false)
    private AlertStatus alertStatus;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolved_by_user_id")
    private Long resolvedByUserId;
    
    @Column(name = "resolution_notes")
    private String resolutionNotes;
    
    @PrePersist
    protected void onCreate() {
        triggeredAt = LocalDateTime.now();
        alertStatus = AlertStatus.ACTIVE;
    }
    
    public enum AlertType {
        FALL_DETECTED,
        SOS_BUTTON,
        ABNORMAL_HEART_RATE,
        ABNORMAL_ACTIVITY,
        MEDICATION_OVERDOSE,
        LEAVING_SAFE_ZONE,
        INACTIVITY,
        OTHER
    }
    
    public enum AlertStatus {
        ACTIVE,
        ACKNOWLEDGED,
        RESOLVED,
        FALSE_ALARM
    }
}
