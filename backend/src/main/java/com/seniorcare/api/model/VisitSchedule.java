package com.seniorcare.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;
    
    @ManyToOne
    @JoinColumn(name = "social_worker_id", nullable = false)
    private User socialWorker;
    
    @Column(name = "visit_purpose")
    private String visitPurpose;
    
    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "scheduled_end_time", nullable = false)
    private LocalDateTime scheduledEndTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status")
    private VisitStatus visitStatus;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        visitStatus = VisitStatus.SCHEDULED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum VisitStatus {
        SCHEDULED,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        MISSED,
        RESCHEDULED
    }
}
