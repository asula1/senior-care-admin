package com.seniorcare.api.repository;

import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findAllByRole(UserRole role);
    
    List<User> findByRolesContaining(UserRole role);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.isActive = true")
    List<User> findActiveUsersByRole(UserRole role);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    Long countByRolesContaining(UserRole role);
}
