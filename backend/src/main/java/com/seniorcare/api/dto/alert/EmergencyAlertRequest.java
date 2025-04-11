package com.seniorcare.api.dto.alert;

import com.seniorcare.api.model.EmergencyAlert;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyAlertRequest {
    
    @NotNull(message = "알림 유형을 입력해주세요")
    private EmergencyAlert.AlertType alertType;
    
    private String alertMessage;
    private Double locationLatitude;
    private Double locationLongitude;
    private String locationAddress;
    private Integer heartRate;
}
