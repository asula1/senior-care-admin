package com.seniorcare.api.repository;

import com.seniorcare.api.model.Medication;
import com.seniorcare.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    
    List<Medication> findByUser(User user);
    
    List<Medication> findByUserAndStartDateBeforeAndEndDateAfterOrEndDateIsNull(
            User user, LocalDate currentDate, LocalDate currentDate2);
    
    @Query("SELECT m FROM Medication m WHERE m.user = :user AND " +
           "(:currentDate BETWEEN m.startDate AND m.endDate OR " +
           "(m.startDate <= :currentDate AND m.endDate IS NULL))")
    List<Medication> findActiveByUser(User user, LocalDate currentDate);
    
    @Query("SELECT COUNT(m) FROM Medication m WHERE " +
           "(:currentDate BETWEEN m.startDate AND m.endDate OR " +
           "(m.startDate <= :currentDate AND m.endDate IS NULL))")
    Long countActiveByDate(LocalDate currentDate);
    
    List<Medication> findByUserAndMedicationNameContaining(User user, String medicationName);
    
    @Query("SELECT m FROM Medication m WHERE m.prescriptionId = :prescriptionId")
    List<Medication> findByPrescriptionId(String prescriptionId);
}
