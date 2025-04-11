package com.seniorcare.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "guardians")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guardian {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;
    
    @ManyToOne
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;
    
    @Column(name = "relationship")
    private String relationship;
    
    @Column(name = "is_primary")
    private Boolean isPrimary;
    
    @Column(name = "can_view_health_data")
    private Boolean canViewHealthData;
    
    @Column(name = "can_view_location")
    private Boolean canViewLocation;
    
    @Column(name = "can_view_medications")
    private Boolean canViewMedications;
    
    @Column(name = "emergency_contact_priority")
    private Integer emergencyContactPriority;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Default permissions
        if (canViewHealthData == null) canViewHealthData = true;
        if (canViewLocation == null) canViewLocation = true;
        if (canViewMedications == null) canViewMedications = true;
        if (isPrimary == null) isPrimary = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
