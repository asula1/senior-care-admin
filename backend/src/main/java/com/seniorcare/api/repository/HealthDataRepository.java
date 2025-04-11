package com.seniorcare.api.repository;

import com.seniorcare.api.model.HealthData;
import com.seniorcare.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    
    List<HealthData> findByUser(User user);
    
    List<HealthData> findByUserAndRecordedAtBetweenOrderByRecordedAt(
            User user, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT h FROM HealthData h WHERE h.user = :user AND h.recordedAt = (SELECT MAX(h2.recordedAt) FROM HealthData h2 WHERE h2.user = :user)")
    Optional<HealthData> findLatestByUser(User user);
    
    @Query("SELECT AVG(h.heartRate) FROM HealthData h WHERE h.user = :user AND h.recordedAt BETWEEN :startTime AND :endTime")
    Double findAverageHeartRateByUserAndTimeRange(User user, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT SUM(h.stepsCount) FROM HealthData h WHERE h.user = :user AND h.recordedAt BETWEEN :startTime AND :endTime")
    Integer findTotalStepsByUserAndTimeRange(User user, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT h FROM HealthData h WHERE h.user = :user AND h.heartRate > :threshold ORDER BY h.recordedAt DESC")
    List<HealthData> findByUserAndHeartRateAboveThreshold(User user, Integer threshold);
    
    @Query("SELECT h FROM HealthData h WHERE h.user = :user AND h.heartRate < :threshold ORDER BY h.recordedAt DESC")
    List<HealthData> findByUserAndHeartRateBelowThreshold(User user, Integer threshold);
}
