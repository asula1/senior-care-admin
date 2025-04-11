package com.seniorcare.api.repository;

import com.seniorcare.api.model.Medication;
import com.seniorcare.api.model.MedicationReminder;
import com.seniorcare.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MedicationReminderRepository extends JpaRepository<MedicationReminder, Long> {
    
    List<MedicationReminder> findByMedication(Medication medication);
    
    List<MedicationReminder> findByMedicationAndReminderStatus(
            Medication medication, MedicationReminder.ReminderStatus status);
    
    @Query("SELECT r FROM MedicationReminder r WHERE r.medication.user = :user AND r.reminderStatus = :status")
    List<MedicationReminder> findByUserAndStatus(User user, MedicationReminder.ReminderStatus status);
    
    @Query("SELECT r FROM MedicationReminder r WHERE r.medication.user = :user AND " +
           "r.reminderTime BETWEEN :startTime AND :endTime AND r.reminderStatus = :status")
    List<MedicationReminder> findUpcomingReminders(
            User user, LocalTime startTime, LocalTime endTime, MedicationReminder.ReminderStatus status);
    
    @Query("SELECT r FROM MedicationReminder r WHERE r.reminderStatus = :status AND " +
           "r.reminderTime <= :currentTime")
    List<MedicationReminder> findPendingRemindersBeforeTime(
            MedicationReminder.ReminderStatus status, LocalTime currentTime);
    
    Long countByReminderStatus(MedicationReminder.ReminderStatus status);
    
    @Query("SELECT COUNT(r) FROM MedicationReminder r WHERE r.medication.user = :user AND r.reminderStatus = 'MISSED'")
    Long countMissedByUser(User user);
}
