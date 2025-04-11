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
public class AlertResolutionRequest {
    
    @NotNull(message = "알림 상태를 입력해주세요")
    private EmergencyAlert.AlertStatus alertStatus;
    
    private String resolutionNotes;
}
