package com.seniorcare.api.config;

import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import com.seniorcare.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정이 이미 존재하는지 확인
        if (!userRepository.existsByUsername("admin")) {
            log.info("관리자 계정을 생성합니다...");
            
            // 관리자 계정 생성
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@seniorcare.com")
                    .fullName("시스템 관리자")
                    .roles(new HashSet<>(Collections.singletonList(UserRole.ROLE_ADMIN)))
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            userRepository.save(adminUser);
            log.info("관리자 계정이 성공적으로 생성되었습니다.");
        } else {
            log.info("관리자 계정이 이미 존재합니다.");
        }
    }
}