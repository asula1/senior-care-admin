package com.seniorcare.api.dto.visit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitRequest {
    
    @NotNull(message = "노인 ID를 입력해주세요")
    private Long seniorId;
    
    @NotNull(message = "복지사 ID를 입력해주세요")
    private Long socialWorkerId;
    
    @NotNull(message = "방문 시작 시간을 입력해주세요")
    private LocalDateTime startTime;
    
    @NotNull(message = "방문 종료 시간을 입력해주세요")
    private LocalDateTime endTime;
    
    private String visitPurpose;
    
    private String notes;
}
