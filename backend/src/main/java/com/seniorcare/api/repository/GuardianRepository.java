package com.seniorcare.api.repository;

import com.seniorcare.api.model.Guardian;
import com.seniorcare.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {
    
    List<Guardian> findBySenior(User senior);
    
    List<Guardian> findByGuardian(User guardian);
    
    Optional<Guardian> findBySeniorAndGuardian(User senior, User guardian);
    
    @Query("SELECT g FROM Guardian g WHERE g.senior = :senior AND g.isPrimary = true")
    Optional<Guardian> findPrimaryGuardianBySenior(User senior);
    
    @Query("SELECT g FROM Guardian g WHERE g.senior = :senior ORDER BY g.emergencyContactPriority ASC NULLS LAST")
    List<Guardian> findGuardiansBySeniorOrderByPriority(User senior);
    
    @Query("SELECT g.senior FROM Guardian g WHERE g.guardian = :guardian")
    List<User> findSeniorsByGuardian(User guardian);
    
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Guardian g WHERE g.senior = :senior AND g.guardian = :guardian")
    boolean existsGuardianRelation(User senior, User guardian);
}
