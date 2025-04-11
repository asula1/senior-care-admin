package com.seniorcare.api.service;

import com.seniorcare.api.dto.auth.LoginRequest;
import com.seniorcare.api.dto.auth.LoginResponse;
import com.seniorcare.api.dto.auth.SignupRequest;
import com.seniorcare.api.exception.BadRequestException;
import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import com.seniorcare.api.repository.UserRepository;
import com.seniorcare.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @Transactional
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        // 마지막 로그인 시간 업데이트
        userService.updateLastLogin(loginRequest.getUsername());
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다"));

        return LoginResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    @Transactional
    public Long registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BadRequestException("이미 사용 중인 아이디입니다");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("이미 사용 중인 이메일입니다");
        }

        // 기본 역할을 ROLE_SENIOR로 설정 (역할이 지정되지 않은 경우)
        if (signupRequest.getRoles() == null || signupRequest.getRoles().isEmpty()) {
            signupRequest.setRoles(new HashSet<>(Collections.singletonList(UserRole.ROLE_SENIOR)));
        }

        User user = User.builder()
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .email(signupRequest.getEmail())
                .fullName(signupRequest.getFullName())
                .phoneNumber(signupRequest.getPhoneNumber())
                .dateOfBirth(signupRequest.getDateOfBirth())
                .roles(signupRequest.getRoles())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }
}
