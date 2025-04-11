package com.seniorcare.api.service;

import com.seniorcare.api.dto.dashboard.AlertSummaryDto;
import com.seniorcare.api.dto.dashboard.DashboardSummaryDto;
import com.seniorcare.api.dto.dashboard.SeniorHealthStatusDto;
import com.seniorcare.api.model.EmergencyAlert;
import com.seniorcare.api.model.HealthData;
import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import com.seniorcare.api.model.VisitSchedule;
import com.seniorcare.api.repository.EmergencyAlertRepository;
import com.seniorcare.api.repository.HealthDataRepository;
import com.seniorcare.api.repository.MedicationReminderRepository;
import com.seniorcare.api.repository.MedicationRepository;
import com.seniorcare.api.repository.UserRepository;
import com.seniorcare.api.repository.VisitScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final EmergencyAlertRepository alertRepository;
    private final VisitScheduleRepository visitRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationReminderRepository reminderRepository;
    private final HealthDataRepository healthDataRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryDto getDashboardSummary() {
        // 사용자 통계
        Long totalSeniors = userRepository.countByRolesContaining(UserRole.ROLE_SENIOR);
        Long totalGuardians = userRepository.countByRolesContaining(UserRole.ROLE_GUARDIAN);
        Long totalSocialWorkers = userRepository.countByRolesContaining(UserRole.ROLE_SOCIAL_WORKER);
        
        // 알림 통계
        Long activeAlerts = alertRepository.countByAlertStatus(EmergencyAlert.AlertStatus.ACTIVE);
        Long resolvedAlerts = alertRepository.countByAlertStatusIn(
                List.of(EmergencyAlert.AlertStatus.RESOLVED, EmergencyAlert.AlertStatus.FALSE_ALARM));
        Long totalAlerts = alertRepository.count();
        
        // 방문 통계
        Long scheduledVisits = visitRepository.countByVisitStatusIn(
                List.of(VisitSchedule.VisitStatus.SCHEDULED, VisitSchedule.VisitStatus.CONFIRMED));
        Long completedVisits = visitRepository.countByVisitStatus(VisitSchedule.VisitStatus.COMPLETED);
        Long missedVisits = visitRepository.countByVisitStatus(VisitSchedule.VisitStatus.MISSED);
        
        // 약물 통계
        LocalDate today = LocalDate.now();
        Long activeMedications = medicationRepository.countActiveByDate(today);
        Long medicationReminders = reminderRepository.countByReminderStatus(
                com.seniorcare.api.model.MedicationReminder.ReminderStatus.SCHEDULED);
        Long missedMedications = reminderRepository.countByReminderStatus(
                com.seniorcare.api.model.MedicationReminder.ReminderStatus.MISSED);
        
        return DashboardSummaryDto.builder()
                .totalSeniors(totalSeniors)
                .totalGuardians(totalGuardians)
                .totalSocialWorkers(totalSocialWorkers)
                .activeAlerts(activeAlerts)
                .resolvedAlerts(resolvedAlerts)
                .totalAlerts(totalAlerts)
                .scheduledVisits(scheduledVisits)
                .completedVisits(completedVisits)
                .missedVisits(missedVisits)
                .activeMedications(activeMedications)
                .medicationReminders(medicationReminders)
                .missedMedications(missedMedications)
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<SeniorHealthStatusDto> getAllSeniorsHealthStatus() {
        List<User> seniors = userRepository.findByRolesContaining(UserRole.ROLE_SENIOR);
        List<SeniorHealthStatusDto> result = new ArrayList<>();
        
        for (User senior : seniors) {
            result.add(getSeniorHealthStatus(senior));
        }
        
        return result;
    }
    
    @Transactional(readOnly = true)
    public SeniorHealthStatusDto getSeniorHealthStatus(Long seniorId) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new com.seniorcare.api.exception.ResourceNotFoundException("User", "id", seniorId));
        
        return getSeniorHealthStatus(senior);
    }
    
    private SeniorHealthStatusDto getSeniorHealthStatus(User senior) {
        // 최근 건강 데이터 조회
        Optional<HealthData> latestHealthData = healthDataRepository.findLatestByUser(senior);
        
        // 활성 알림 개수 조회
        Long activeAlertsCount = alertRepository.countByUserAndAlertStatus(
                senior, EmergencyAlert.AlertStatus.ACTIVE);
        
        // 미복용 약물 개수 조회
        Long missedMedicationsCount = reminderRepository.countMissedByUser(senior);
        
        // 건강 상태 평가
        String healthStatus = "NORMAL";
        Integer heartRate = null;
        Integer bloodPressureSystolic = null;
        Integer bloodPressureDiastolic = null;
        Double bloodOxygen = null;
        Double bodyTemperature = null;
        Integer stepsCount = null;
        LocalDateTime lastUpdated = null;
        
        if (latestHealthData.isPresent()) {
            HealthData data = latestHealthData.get();
            heartRate = data.getHeartRate();
            bloodPressureSystolic = data.getBloodPressureSystolic();
            bloodPressureDiastolic = data.getBloodPressureDiastolic();
            bloodOxygen = data.getBloodOxygen();
            bodyTemperature = data.getBodyTemperature();
            stepsCount = data.getStepsCount();
            lastUpdated = data.getRecordedAt();
            
            // 건강 상태 평가 로직
            if (isHealthCritical(data)) {
                healthStatus = "CRITICAL";
            } else if (isHealthWarning(data)) {
                healthStatus = "WARNING";
            }
        }
        
        return SeniorHealthStatusDto.builder()
                .userId(senior.getId())
                .fullName(senior.getFullName())
                .heartRate(heartRate)
                .bloodPressureSystolic(bloodPressureSystolic)
                .bloodPressureDiastolic(bloodPressureDiastolic)
                .bloodOxygen(bloodOxygen)
                .bodyTemperature(bodyTemperature)
                .stepsCount(stepsCount)
                .lastUpdated(lastUpdated)
                .healthStatus(healthStatus)
                .hasActiveAlerts(activeAlertsCount > 0)
                .activeAlertsCount(activeAlertsCount)
                .missedMedicationsCount(missedMedicationsCount)
                .build();
    }
    
    private boolean isHealthWarning(HealthData data) {
        // 경고 수준의 건강 상태 평가 로직
        if (data.getHeartRate() != null && (data.getHeartRate() > 100 || data.getHeartRate() < 50)) {
            return true;
        }
        
        if (data.getBloodPressureSystolic() != null && data.getBloodPressureDiastolic() != null) {
            if (data.getBloodPressureSystolic() > 140 || data.getBloodPressureSystolic() < 90 ||
                data.getBloodPressureDiastolic() > 90 || data.getBloodPressureDiastolic() < 60) {
                return true;
            }
        }
        
        if (data.getBloodOxygen() != null && data.getBloodOxygen() < 95) {
            return true;
        }
        
        if (data.getBodyTemperature() != null && 
            (data.getBodyTemperature() > 37.5 || data.getBodyTemperature() < 36.0)) {
            return true;
        }
        
        return false;
    }
    
    private boolean isHealthCritical(HealthData data) {
        // 위험 수준의 건강 상태 평가 로직
        if (data.getHeartRate() != null && (data.getHeartRate() > 120 || data.getHeartRate() < 40)) {
            return true;
        }
        
        if (data.getBloodPressureSystolic() != null && data.getBloodPressureDiastolic() != null) {
            if (data.getBloodPressureSystolic() > 180 || data.getBloodPressureSystolic() < 80 ||
                data.getBloodPressureDiastolic() > 120 || data.getBloodPressureDiastolic() < 50) {
                return true;
            }
        }
        
        if (data.getBloodOxygen() != null && data.getBloodOxygen() < 90) {
            return true;
        }
        
        if (data.getBodyTemperature() != null && 
            (data.getBodyTemperature() > 38.5 || data.getBodyTemperature() < 35.0)) {
            return true;
        }
        
        return false;
    }
    
    @Transactional(readOnly = true)
    public AlertSummaryDto getAlertSummary() {
        // 알림 통계
        Long totalAlerts = alertRepository.count();
        Long activeAlerts = alertRepository.countByAlertStatus(EmergencyAlert.AlertStatus.ACTIVE);
        Long acknowledgedAlerts = alertRepository.countByAlertStatus(EmergencyAlert.AlertStatus.ACKNOWLEDGED);
        Long resolvedAlerts = alertRepository.countByAlertStatus(EmergencyAlert.AlertStatus.RESOLVED);
        Long falseAlarms = alertRepository.countByAlertStatus(EmergencyAlert.AlertStatus.FALSE_ALARM);
        
        // 알림 유형별 통계
        Map<EmergencyAlert.AlertType, Long> alertsByType = new HashMap<>();
        for (EmergencyAlert.AlertType type : EmergencyAlert.AlertType.values()) {
            alertsByType.put(type, alertRepository.countByAlertType(type));
        }
        
        // 가장 흔한 알림 유형 찾기
        EmergencyAlert.AlertType mostCommonType = null;
        Long maxCount = 0L;
        for (Map.Entry<EmergencyAlert.AlertType, Long> entry : alertsByType.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostCommonType = entry.getKey();
            }
        }
        
        // 시간대별 알림 통계 (최근 24시간)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        List<EmergencyAlert> recentAlerts = alertRepository.findByTriggeredAtBetween(yesterday, now);
        
        Map<String, Long> alertsByHour = new HashMap<>();
        Map<String, Long> alertsByDay = new HashMap<>();
        
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (EmergencyAlert alert : recentAlerts) {
            String hour = alert.getTriggeredAt().format(hourFormatter);
            String day = alert.getTriggeredAt().format(dayFormatter);
            
            alertsByHour.put(hour, alertsByHour.getOrDefault(hour, 0L) + 1);
            alertsByDay.put(day, alertsByDay.getOrDefault(day, 0L) + 1);
        }
        
        // 마지막 알림 시간
        LocalDateTime lastAlertTime = null;
        if (!recentAlerts.isEmpty()) {
            lastAlertTime = recentAlerts.stream()
                    .map(EmergencyAlert::getTriggeredAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }
        
        return AlertSummaryDto.builder()
                .totalAlerts(totalAlerts)
                .activeAlerts(activeAlerts)
                .acknowledgedAlerts(acknowledgedAlerts)
                .resolvedAlerts(resolvedAlerts)
                .falseAlarms(falseAlarms)
                .alertsByType(alertsByType)
                .alertsByHour(alertsByHour)
                .alertsByDay(alertsByDay)
                .lastAlertTime(lastAlertTime)
                .mostCommonAlertType(mostCommonType)
                .build();
    }
}