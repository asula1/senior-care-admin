package com.seniorcare.api.dto.user;

import com.seniorcare.api.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Set<UserRole> roles;
    private Boolean isActive;
}
