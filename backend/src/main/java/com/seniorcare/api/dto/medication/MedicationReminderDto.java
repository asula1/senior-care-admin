package com.seniorcare.api.dto.medication;

import com.seniorcare.api.model.MedicationReminder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicationReminderDto {
    
    private Long id;
    private Long medicationId;
    private String medicationName;
    private String dosage;
    private LocalTime reminderTime;
    private MedicationReminder.ReminderStatus reminderStatus;
    private LocalDateTime takenAt;
    private LocalDateTime createdAt;
    private Long userId;
}
