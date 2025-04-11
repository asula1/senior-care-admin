package com.seniorcare.api.repository;

import com.seniorcare.api.model.User;
import com.seniorcare.api.model.VisitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitScheduleRepository extends JpaRepository<VisitSchedule, Long> {
    
    List<VisitSchedule> findBySenior(User senior);
    
    List<VisitSchedule> findBySocialWorker(User socialWorker);
    
    List<VisitSchedule> findBySeniorAndVisitStatus(User senior, VisitSchedule.VisitStatus status);
    
    List<VisitSchedule> findBySocialWorkerAndVisitStatus(User socialWorker, VisitSchedule.VisitStatus status);
    
    @Query("SELECT v FROM VisitSchedule v WHERE v.senior = :senior AND " +
           "v.scheduledStartTime BETWEEN :startTime AND :endTime")
    List<VisitSchedule> findBySeniorAndTimeRange(User senior, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT v FROM VisitSchedule v WHERE v.socialWorker = :socialWorker AND " +
           "v.scheduledStartTime BETWEEN :startTime AND :endTime")
    List<VisitSchedule> findBySocialWorkerAndTimeRange(
            User socialWorker, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT v FROM VisitSchedule v WHERE v.scheduledStartTime > :now AND " +
           "v.visitStatus IN ('SCHEDULED', 'CONFIRMED') ORDER BY v.scheduledStartTime ASC")
    List<VisitSchedule> findUpcomingVisits(LocalDateTime now);
    
    @Query("SELECT v FROM VisitSchedule v WHERE v.senior = :senior AND " +
           "v.scheduledStartTime > :now AND v.visitStatus IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY v.scheduledStartTime ASC")
    List<VisitSchedule> findUpcomingVisitsBySenior(User senior, LocalDateTime now);
    
    Long countByVisitStatus(VisitSchedule.VisitStatus status);
    
    Long countByVisitStatusIn(List<VisitSchedule.VisitStatus> statuses);
}
