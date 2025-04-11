package com.seniorcare.api.dto.visit;

import com.seniorcare.api.model.VisitSchedule;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitStatusUpdateRequest {
    
    @NotNull(message = "방문 상태를 입력해주세요")
    private VisitSchedule.VisitStatus status;
    
    private String notes;
}
