package com.seniorcare.api.repository;

import com.seniorcare.api.model.EmergencyAlert;
import com.seniorcare.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmergencyAlertRepository extends JpaRepository<EmergencyAlert, Long> {
    
    List<EmergencyAlert> findByUser(User user);
    
    List<EmergencyAlert> findByUserAndAlertStatus(User user, EmergencyAlert.AlertStatus status);
    
    List<EmergencyAlert> findByAlertStatus(EmergencyAlert.AlertStatus status);
    
    List<EmergencyAlert> findByAlertStatusIn(List<EmergencyAlert.AlertStatus> statuses);
    
    List<EmergencyAlert> findByUserAndTriggeredAtBetween(
            User user, LocalDateTime startTime, LocalDateTime endTime);
    
    List<EmergencyAlert> findByTriggeredAtBetween(
            LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT e FROM EmergencyAlert e WHERE e.alertStatus = :status " +
           "ORDER BY e.triggeredAt DESC")
    List<EmergencyAlert> findRecentByStatus(EmergencyAlert.AlertStatus status);
    
    @Query("SELECT COUNT(e) FROM EmergencyAlert e WHERE e.user = :user AND " +
           "e.triggeredAt BETWEEN :startTime AND :endTime")
    Long countAlertsInTimeRange(User user, LocalDateTime startTime, LocalDateTime endTime);
    
    List<EmergencyAlert> findByAlertType(EmergencyAlert.AlertType alertType);
    
    Long countByAlertStatus(EmergencyAlert.AlertStatus status);
    
    Long countByAlertStatusIn(List<EmergencyAlert.AlertStatus> statuses);
    
    Long countByAlertType(EmergencyAlert.AlertType alertType);
    
    Long countByUserAndAlertStatus(User user, EmergencyAlert.AlertStatus status);
}
