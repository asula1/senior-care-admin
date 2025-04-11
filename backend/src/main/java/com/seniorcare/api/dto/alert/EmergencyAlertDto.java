package com.seniorcare.api.dto.alert;

import com.seniorcare.api.model.EmergencyAlert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyAlertDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private EmergencyAlert.AlertType alertType;
    private String alertMessage;
    private Double locationLatitude;
    private Double locationLongitude;
    private String locationAddress;
    private Integer heartRate;
    private LocalDateTime triggeredAt;
    private EmergencyAlert.AlertStatus alertStatus;
    private LocalDateTime resolvedAt;
    private Long resolvedByUserId;
    private String resolvedByUserName;
    private String resolutionNotes;
}
