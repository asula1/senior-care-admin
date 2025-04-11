package com.seniorcare.api.controller;

import com.seniorcare.api.dto.ApiResponse;
import com.seniorcare.api.dto.auth.LoginRequest;
import com.seniorcare.api.dto.auth.LoginResponse;
import com.seniorcare.api.dto.auth.SignupRequest;
import com.seniorcare.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        Long userId = authService.registerUser(signupRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "사용자 등록이 완료되었습니다", userId));
    }
}
