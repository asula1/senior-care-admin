package com.seniorcare.api.dto.visit;

import com.seniorcare.api.model.VisitSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitScheduleDto {
    
    private Long id;
    private Long seniorId;
    private String seniorName;
    private Long socialWorkerId;
    private String socialWorkerName;
    private String visitPurpose;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private VisitSchedule.VisitStatus visitStatus;
    private String notes;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private LocalDateTime createdAt;
}
