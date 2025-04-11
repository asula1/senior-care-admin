package com.seniorcare.api.service;

import com.seniorcare.api.dto.alert.EmergencyAlertRequest;
import com.seniorcare.api.dto.health.HealthDataDto;
import com.seniorcare.api.dto.health.HealthDataRequest;
import com.seniorcare.api.exception.ResourceNotFoundException;
import com.seniorcare.api.model.EmergencyAlert;
import com.seniorcare.api.model.HealthData;
import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import com.seniorcare.api.repository.HealthDataRepository;
import com.seniorcare.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthDataService {

    private final HealthDataRepository healthDataRepository;
    private final UserRepository userRepository;
    private final EmergencyAlertService emergencyAlertService;

    @Transactional(readOnly = true)
    public List<HealthDataDto> getUserHealthData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return healthDataRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HealthDataDto> getUserHealthDataByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return healthDataRepository.findByUserAndRecordedAtBetweenOrderByRecordedAt(user, startTime, endTime).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HealthDataDto getLatestHealthData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return healthDataRepository.findLatestByUser(user)
                .map(this::convertToDto)
                .orElse(null);
    }

    @Transactional
    public HealthDataDto recordHealthData(Long userId, HealthDataRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        HealthData healthData = HealthData.builder()
                .user(user)
                .heartRate(request.getHeartRate())
                .stepsCount(request.getStepsCount())
                .caloriesBurned(request.getCaloriesBurned())
                .sleepDurationMinutes(request.getSleepDurationMinutes())
                .sleepQuality(request.getSleepQuality())
                .bloodPressureSystolic(request.getBloodPressureSystolic())
                .bloodPressureDiastolic(request.getBloodPressureDiastolic())
                .bloodOxygen(request.getBloodOxygen())
                .bodyTemperature(request.getBodyTemperature())
                .recordedAt(LocalDateTime.now())
                .deviceId(request.getDeviceId())
                .dataSource("WATCH") // 스마트워치를 기본 데이터 소스로 설정
                .build();
        
        HealthData savedData = healthDataRepository.save(healthData);
        
        // 건강 데이터 저장 후 비정상 상태 체크
        checkAbnormalHealthData(user, savedData);
        
        return convertToDto(savedData);
    }

    /**
     * 모든 노인 사용자의 최신 건강 데이터를 주기적으로 확인하여 비정상 상태를 감지
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void scheduledHealthDataCheck() {
        log.info("Running scheduled health data check");
        List<User> seniors = userRepository.findByRolesContaining(UserRole.ROLE_SENIOR);
        
        for (User senior : seniors) {
            try {
                checkAbnormalHealthData(senior.getId());
            } catch (Exception e) {
                log.error("Error checking health data for senior ID {}: {}", senior.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void checkAbnormalHealthData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 최근 데이터 가져오기
        HealthData latestData = healthDataRepository.findLatestByUser(user).orElse(null);
        
        if (latestData != null) {
            checkAbnormalHealthData(user, latestData);
        }
    }
    
    private void checkAbnormalHealthData(User user, HealthData healthData) {
        // 비정상 심박수 체크 (예: 심박수가 120 이상이거나 40 이하인 경우)
        if (healthData.getHeartRate() != null && 
                (healthData.getHeartRate() > 120 || healthData.getHeartRate() < 40)) {
            log.info("Abnormal heart rate detected for user {}: {}", user.getId(), healthData.getHeartRate());
            emergencyAlertService.createAbnormalHeartRateAlert(user.getId(), healthData.getHeartRate());
        }
        
        // 혈압 비정상 체크
        if (healthData.getBloodPressureSystolic() != null && healthData.getBloodPressureDiastolic() != null) {
            if (healthData.getBloodPressureSystolic() > 180 || 
                    healthData.getBloodPressureDiastolic() > 120 ||
                    healthData.getBloodPressureSystolic() < 90 ||
                    healthData.getBloodPressureDiastolic() < 60) {
                log.info("Abnormal blood pressure detected for user {}: {}/{}", 
                        user.getId(), 
                        healthData.getBloodPressureSystolic(), 
                        healthData.getBloodPressureDiastolic());
                
                String alertMessage = String.format("비정상 혈압이 감지되었습니다: %d/%d", 
                        healthData.getBloodPressureSystolic(), 
                        healthData.getBloodPressureDiastolic());
                
                emergencyAlertService.createAlert(user.getId(), 
                        EmergencyAlertRequest.builder()
                                .alertType(EmergencyAlert.AlertType.ABNORMAL_ACTIVITY)
                                .alertMessage(alertMessage)
                                .build());
            }
        }
        
        // 산소 포화도 비정상 체크
        if (healthData.getBloodOxygen() != null && healthData.getBloodOxygen() < 90) {
            log.info("Low blood oxygen detected for user {}: {}", user.getId(), healthData.getBloodOxygen());
            
            String alertMessage = String.format("낮은 산소포화도가 감지되었습니다: %.1f%%", healthData.getBloodOxygen());
            
            emergencyAlertService.createAlert(user.getId(), 
                    EmergencyAlertRequest.builder()
                            .alertType(EmergencyAlert.AlertType.ABNORMAL_ACTIVITY)
                            .alertMessage(alertMessage)
                            .build());
        }
        
        // 체온 비정상 체크
        if (healthData.getBodyTemperature() != null && 
                (healthData.getBodyTemperature() > 38.5 || healthData.getBodyTemperature() < 35.0)) {
            log.info("Abnormal body temperature detected for user {}: {}", user.getId(), healthData.getBodyTemperature());
            
            String alertMessage = String.format("비정상 체온이 감지되었습니다: %.1f°C", healthData.getBodyTemperature());
            
            emergencyAlertService.createAlert(user.getId(), 
                    EmergencyAlertRequest.builder()
                            .alertType(EmergencyAlert.AlertType.ABNORMAL_ACTIVITY)
                            .alertMessage(alertMessage)
                            .build());
        }
    }

    // HealthData 엔티티를 DTO로 변환
    private HealthDataDto convertToDto(HealthData healthData) {
        return HealthDataDto.builder()
                .id(healthData.getId())
                .userId(healthData.getUser().getId())
                .heartRate(healthData.getHeartRate())
                .stepsCount(healthData.getStepsCount())
                .caloriesBurned(healthData.getCaloriesBurned())
                .sleepDurationMinutes(healthData.getSleepDurationMinutes())
                .sleepQuality(healthData.getSleepQuality())
                .bloodPressureSystolic(healthData.getBloodPressureSystolic())
                .bloodPressureDiastolic(healthData.getBloodPressureDiastolic())
                .bloodOxygen(healthData.getBloodOxygen())
                .bodyTemperature(healthData.getBodyTemperature())
                .recordedAt(healthData.getRecordedAt())
                .deviceId(healthData.getDeviceId())
                .build();
    }
}
