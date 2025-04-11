package com.seniorcare.api.dto.medication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationStatisticsDto {
    private Long userId;
    private String userName;
    
    private Long totalMedications;
    private Long activeMedications;
    
    private Long totalReminders;
    private Long takenReminders;
    private Long missedReminders;
    private Long pendingReminders;
    
    private Double adherenceRate; // 복약 준수율 (%)
    
    private Map<String, Long> medicationsByName; // 약물별 개수
    private Map<String, Double> adherenceByMedication; // 약물별 복약 준수율
    private Map<String, Long> missedByTimeOfDay; // 시간대별 미복용 횟수
}