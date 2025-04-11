package com.seniorcare.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "medication_reminders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationReminder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;
    
    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_status")
    private ReminderStatus reminderStatus;
    
    @Column(name = "taken_at")
    private LocalDateTime takenAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        reminderStatus = ReminderStatus.SCHEDULED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ReminderStatus {
        SCHEDULED,
        SENT,
        TAKEN,
        MISSED,
        CANCELED
    }
}
