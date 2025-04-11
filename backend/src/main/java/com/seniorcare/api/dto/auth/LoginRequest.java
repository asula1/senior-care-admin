package com.seniorcare.api.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "사용자 아이디를 입력해주세요")
    private String username;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
