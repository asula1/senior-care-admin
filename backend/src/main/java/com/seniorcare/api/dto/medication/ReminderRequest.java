package com.seniorcare.api.dto.medication;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReminderRequest {
    
    @NotNull(message = "약물 ID를 입력해주세요")
    private Long medicationId;
    
    @NotNull(message = "알림 시간을 입력해주세요")
    private LocalTime reminderTime;
}
