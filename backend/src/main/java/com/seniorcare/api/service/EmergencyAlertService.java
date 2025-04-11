package com.seniorcare.api.service;

import com.seniorcare.api.dto.alert.AlertResolutionRequest;
import com.seniorcare.api.dto.alert.EmergencyAlertDto;
import com.seniorcare.api.dto.alert.EmergencyAlertRequest;
import com.seniorcare.api.exception.BadRequestException;
import com.seniorcare.api.exception.ResourceNotFoundException;
import com.seniorcare.api.model.EmergencyAlert;
import com.seniorcare.api.model.Guardian;
import com.seniorcare.api.model.User;
import com.seniorcare.api.repository.EmergencyAlertRepository;
import com.seniorcare.api.repository.GuardianRepository;
import com.seniorcare.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyAlertService {

    private final EmergencyAlertRepository alertRepository;
    private final UserRepository userRepository;
    private final GuardianRepository guardianRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public EmergencyAlertDto createAlert(Long userId, EmergencyAlertRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        EmergencyAlert alert = EmergencyAlert.builder()
                .user(user)
                .alertType(request.getAlertType())
                .alertMessage(request.getAlertMessage())
                .locationLatitude(request.getLocationLatitude())
                .locationLongitude(request.getLocationLongitude())
                .locationAddress(request.getLocationAddress())
                .heartRate(request.getHeartRate())
                .triggeredAt(LocalDateTime.now())
                .alertStatus(EmergencyAlert.AlertStatus.ACTIVE)
                .build();
        
        EmergencyAlert savedAlert = alertRepository.save(alert);
        
        // 보호자들에게 알림 전송
        notifyGuardians(user, savedAlert);
        
        return convertToDto(savedAlert);
    }

    @Transactional
    public EmergencyAlertDto createFallDetectionAlert(Long userId, Double latitude, Double longitude) {
        EmergencyAlertRequest request = EmergencyAlertRequest.builder()
                .alertType(EmergencyAlert.AlertType.FALL_DETECTED)
                .alertMessage("낙상이 감지되었습니다")
                .locationLatitude(latitude)
                .locationLongitude(longitude)
                .build();
        
        return createAlert(userId, request);
    }

    @Transactional
    public EmergencyAlertDto createSosAlert(Long userId, Double latitude, Double longitude) {
        EmergencyAlertRequest request = EmergencyAlertRequest.builder()
                .alertType(EmergencyAlert.AlertType.SOS_BUTTON)
                .alertMessage("SOS 버튼이 눌렸습니다")
                .locationLatitude(latitude)
                .locationLongitude(longitude)
                .build();
        
        return createAlert(userId, request);
    }

    @Transactional
    public EmergencyAlertDto createAbnormalHeartRateAlert(Long userId, Integer heartRate) {
        EmergencyAlertRequest request = EmergencyAlertRequest.builder()
                .alertType(EmergencyAlert.AlertType.ABNORMAL_HEART_RATE)
                .alertMessage("비정상적인 심박수가 감지되었습니다: " + heartRate)
                .heartRate(heartRate)
                .build();
        
        return createAlert(userId, request);
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlertDto> getActiveAlerts() {
        return alertRepository.findByAlertStatus(EmergencyAlert.AlertStatus.ACTIVE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EmergencyAlertDto> getResolvedAlerts() {
        List<EmergencyAlert.AlertStatus> resolvedStatuses = List.of(
            EmergencyAlert.AlertStatus.RESOLVED, 
            EmergencyAlert.AlertStatus.FALSE_ALARM
        );
        return alertRepository.findByAlertStatusIn(resolvedStatuses).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlertDto> getActiveAlertsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return alertRepository.findByUserAndAlertStatus(user, EmergencyAlert.AlertStatus.ACTIVE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmergencyAlertDto> getAllAlertsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return alertRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmergencyAlertDto resolveAlert(Long alertId, Long resolverId, AlertResolutionRequest request) {
        EmergencyAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyAlert", "id", alertId));
        
        User resolver = userRepository.findById(resolverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", resolverId));
        
        if (alert.getAlertStatus() != EmergencyAlert.AlertStatus.ACTIVE && 
            alert.getAlertStatus() != EmergencyAlert.AlertStatus.ACKNOWLEDGED) {
            throw new BadRequestException("이미 해결된 알림입니다");
        }
        
        alert.setAlertStatus(request.getAlertStatus());
        alert.setResolutionNotes(request.getResolutionNotes());
        alert.setResolvedByUserId(resolver.getId());
        alert.setResolvedAt(LocalDateTime.now());
        
        EmergencyAlert updatedAlert = alertRepository.save(alert);
        
        // WebSocket을 통해 실시간 업데이트
        messagingTemplate.convertAndSend("/topic/alerts", convertToDto(updatedAlert));
        
        return convertToDto(updatedAlert);
    }

    @Transactional
    public EmergencyAlertDto acknowledgeAlert(Long alertId, Long acknowledgedById) {
        EmergencyAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("EmergencyAlert", "id", alertId));
        
        if (alert.getAlertStatus() != EmergencyAlert.AlertStatus.ACTIVE) {
            throw new BadRequestException("이미 확인 처리된 알림입니다");
        }
        
        alert.setAlertStatus(EmergencyAlert.AlertStatus.ACKNOWLEDGED);
        EmergencyAlert updatedAlert = alertRepository.save(alert);
        
        // WebSocket을 통해 실시간 업데이트
        messagingTemplate.convertAndSend("/topic/alerts", convertToDto(updatedAlert));
        
        return convertToDto(updatedAlert);
    }

    // 보호자들에게 알림 전송
    private void notifyGuardians(User senior, EmergencyAlert alert) {
        List<Guardian> guardians = guardianRepository.findGuardiansBySeniorOrderByPriority(senior);
        
        EmergencyAlertDto alertDto = convertToDto(alert);
        
        // 전체 알림 채널에 메시지 전송
        messagingTemplate.convertAndSend("/topic/alerts", alertDto);
        
        // 각 보호자에게 개인 메시지 전송
        for (Guardian guardian : guardians) {
            messagingTemplate.convertAndSendToUser(
                    guardian.getGuardian().getUsername(),
                    "/queue/alerts",
                    alertDto
            );
            
            // 여기에 푸시 알림, SMS 등 추가 알림 로직을 구현할 수 있음
            log.info("Emergency alert sent to guardian: {}", guardian.getGuardian().getUsername());
        }
    }

    // EmergencyAlert 엔티티를 DTO로 변환
    private EmergencyAlertDto convertToDto(EmergencyAlert alert) {
        return EmergencyAlertDto.builder()
                .id(alert.getId())
                .userId(alert.getUser().getId())
                .userName(alert.getUser().getFullName())
                .alertType(alert.getAlertType())
                .alertMessage(alert.getAlertMessage())
                .locationLatitude(alert.getLocationLatitude())
                .locationLongitude(alert.getLocationLongitude())
                .locationAddress(alert.getLocationAddress())
                .heartRate(alert.getHeartRate())
                .triggeredAt(alert.getTriggeredAt())
                .alertStatus(alert.getAlertStatus())
                .resolvedAt(alert.getResolvedAt())
                .resolvedByUserId(alert.getResolvedByUserId())
                .resolutionNotes(alert.getResolutionNotes())
                .build();
    }
}
