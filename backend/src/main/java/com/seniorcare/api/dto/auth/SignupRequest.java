package com.seniorcare.api.dto.auth;

import com.seniorcare.api.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    
    @NotBlank(message = "사용자 아이디를 입력해주세요")
    @Size(min = 3, max = 20, message = "아이디는 3~20자 사이여야 합니다")
    private String username;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 6, max = 40, message = "비밀번호는 6~40자 사이여야 합니다")
    private String password;
    
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
    
    @NotBlank(message = "이름을 입력해주세요")
    private String fullName;
    
    private String phoneNumber;
    
    private String dateOfBirth;
    
    private Set<UserRole> roles;
}
