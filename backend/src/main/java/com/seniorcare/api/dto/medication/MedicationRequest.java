package com.seniorcare.api.dto.medication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicationRequest {
    
    @NotBlank(message = "약물 이름을 입력해주세요")
    private String medicationName;
    
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String instructions;
    private String prescriptionId;
    private String pharmacyName;
    private String prescriberName;
}
