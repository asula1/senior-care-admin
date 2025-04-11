package com.seniorcare.api.service;

import com.seniorcare.api.dto.medication.MedicationDto;
import com.seniorcare.api.dto.medication.MedicationReminderDto;
import com.seniorcare.api.dto.medication.MedicationRequest;
import com.seniorcare.api.dto.medication.MedicationStatisticsDto;
import com.seniorcare.api.dto.medication.ReminderRequest;
import com.seniorcare.api.exception.ResourceNotFoundException;
import com.seniorcare.api.model.Medication;
import com.seniorcare.api.model.MedicationReminder;
import com.seniorcare.api.model.User;
import com.seniorcare.api.repository.MedicationReminderRepository;
import com.seniorcare.api.repository.MedicationRepository;
import com.seniorcare.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final MedicationReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<MedicationDto> getUserMedications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return medicationRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicationDto> getActiveMedications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return medicationRepository.findActiveByUser(user, LocalDate.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicationDto addMedication(Long userId, MedicationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Medication medication = Medication.builder()
                .user(user)
                .medicationName(request.getMedicationName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .endDate(request.getEndDate())
                .instructions(request.getInstructions())
                .prescriptionId(request.getPrescriptionId())
                .pharmacyName(request.getPharmacyName())
                .prescriberName(request.getPrescriberName())
                .build();
        
        Medication savedMedication = medicationRepository.save(medication);
        return convertToDto(savedMedication);
    }

    @Transactional
    public MedicationDto updateMedication(Long medicationId, MedicationRequest request) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", medicationId));
        
        medication.setMedicationName(request.getMedicationName());
        medication.setDosage(request.getDosage());
        medication.setFrequency(request.getFrequency());
        medication.setStartDate(request.getStartDate());
        medication.setEndDate(request.getEndDate());
        medication.setInstructions(request.getInstructions());
        medication.setPrescriptionId(request.getPrescriptionId());
        medication.setPharmacyName(request.getPharmacyName());
        medication.setPrescriberName(request.getPrescriberName());
        
        Medication updatedMedication = medicationRepository.save(medication);
        return convertToDto(updatedMedication);
    }

    @Transactional
    public void deleteMedication(Long medicationId) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", medicationId));
        
        // 약물에 연결된 알림도 함께 삭제
        List<MedicationReminder> reminders = reminderRepository.findByMedication(medication);
        reminderRepository.deleteAll(reminders);
        
        medicationRepository.delete(medication);
    }

    @Transactional
    public MedicationReminderDto addReminder(ReminderRequest request) {
        Medication medication = medicationRepository.findById(request.getMedicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", request.getMedicationId()));
        
        MedicationReminder reminder = MedicationReminder.builder()
                .medication(medication)
                .reminderTime(request.getReminderTime())
                .reminderStatus(MedicationReminder.ReminderStatus.SCHEDULED)
                .build();
        
        MedicationReminder savedReminder = reminderRepository.save(reminder);
        return convertToReminderDto(savedReminder);
    }

    @Transactional
    public List<MedicationReminderDto> getUserReminders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return reminderRepository.findByUserAndStatus(user, MedicationReminder.ReminderStatus.SCHEDULED).stream()
                .map(this::convertToReminderDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicationReminderDto markReminderAsTaken(Long reminderId) {
        MedicationReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicationReminder", "id", reminderId));
        
        reminder.setReminderStatus(MedicationReminder.ReminderStatus.TAKEN);
        reminder.setTakenAt(LocalDateTime.now());
        
        MedicationReminder updatedReminder = reminderRepository.save(reminder);
        return convertToReminderDto(updatedReminder);
    }

    @Transactional
    public MedicationReminderDto markReminderAsMissed(Long reminderId) {
        MedicationReminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicationReminder", "id", reminderId));
        
        reminder.setReminderStatus(MedicationReminder.ReminderStatus.MISSED);
        
        MedicationReminder updatedReminder = reminderRepository.save(reminder);
        return convertToReminderDto(updatedReminder);
    }
    
    @Scheduled(cron = "0 * * * * ?") // 매 분마다 실행
    @Transactional
    public void sendReminders() {
        LocalTime now = LocalTime.now();
        LocalTime fiveMinutesLater = now.plusMinutes(5);
        
        // 현재 시간부터 5분 내에 예정된 알림 찾기
        List<MedicationReminder> upcomingReminders = reminderRepository.findPendingRemindersBeforeTime(
                MedicationReminder.ReminderStatus.SCHEDULED, fiveMinutesLater);
        
        for (MedicationReminder reminder : upcomingReminders) {
            reminder.setReminderStatus(MedicationReminder.ReminderStatus.SENT);
            reminderRepository.save(reminder);
            
            // WebSocket을 통해 사용자에게 알림 전송
            messagingTemplate.convertAndSendToUser(
                    reminder.getMedication().getUser().getUsername(),
                    "/queue/reminders",
                    convertToReminderDto(reminder)
            );
        }
    }

    @Scheduled(cron = "0 0 * * * ?") // 매 시간마다 실행
    @Transactional
    public void checkMissedReminders() {
        LocalTime oneHourAgo = LocalTime.now().minusHours(1);
        
        // 1시간 전에 전송됐지만 복용 확인되지 않은 알림 찾기
        List<MedicationReminder> sentReminders = reminderRepository.findPendingRemindersBeforeTime(
                MedicationReminder.ReminderStatus.SENT, oneHourAgo);
        
        for (MedicationReminder reminder : sentReminders) {
            reminder.setReminderStatus(MedicationReminder.ReminderStatus.MISSED);
            reminderRepository.save(reminder);
            
            // 미복용 알림 전송 로직 추가 가능
        }
    }

    // Medication 엔티티를 DTO로 변환
    private MedicationDto convertToDto(Medication medication) {
        return MedicationDto.builder()
                .id(medication.getId())
                .userId(medication.getUser().getId())
                .medicationName(medication.getMedicationName())
                .dosage(medication.getDosage())
                .frequency(medication.getFrequency())
                .startDate(medication.getStartDate())
                .endDate(medication.getEndDate())
                .instructions(medication.getInstructions())
                .prescriptionId(medication.getPrescriptionId())
                .pharmacyName(medication.getPharmacyName())
                .prescriberName(medication.getPrescriberName())
                .createdAt(medication.getCreatedAt())
                .build();
    }

    // MedicationReminder 엔티티를 DTO로 변환
    private MedicationReminderDto convertToReminderDto(MedicationReminder reminder) {
        return MedicationReminderDto.builder()
                .id(reminder.getId())
                .medicationId(reminder.getMedication().getId())
                .medicationName(reminder.getMedication().getMedicationName())
                .dosage(reminder.getMedication().getDosage())
                .reminderTime(reminder.getReminderTime())
                .reminderStatus(reminder.getReminderStatus())
                .takenAt(reminder.getTakenAt())
                .createdAt(reminder.getCreatedAt())
                .build();
    }
    
    /**
     * 사용자의 약물 복용 통계를 계산
     */
    @Transactional(readOnly = true)
    public MedicationStatisticsDto getMedicationStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        LocalDate today = LocalDate.now();
        
        // 약물 통계
        List<Medication> allMedications = medicationRepository.findByUser(user);
        List<Medication> activeMedications = medicationRepository.findActiveByUser(user, today);
        
        // 알림 통계
        List<MedicationReminder> allReminders = new ArrayList<>();
        for (Medication medication : allMedications) {
            allReminders.addAll(reminderRepository.findByMedication(medication));
        }
        
        long takenReminders = allReminders.stream()
                .filter(r -> r.getReminderStatus() == MedicationReminder.ReminderStatus.TAKEN)
                .count();
        
        long missedReminders = allReminders.stream()
                .filter(r -> r.getReminderStatus() == MedicationReminder.ReminderStatus.MISSED)
                .count();
        
        long pendingReminders = allReminders.stream()
                .filter(r -> r.getReminderStatus() == MedicationReminder.ReminderStatus.SCHEDULED || 
                             r.getReminderStatus() == MedicationReminder.ReminderStatus.SENT)
                .count();
        
        // 복약 준수율 계산
        double adherenceRate = 0.0;
        if (takenReminders + missedReminders > 0) {
            adherenceRate = (double) takenReminders / (takenReminders + missedReminders) * 100.0;
        }
        
        // 약물별 통계
        Map<String, Long> medicationsByName = new HashMap<>();
        for (Medication medication : allMedications) {
            String name = medication.getMedicationName();
            medicationsByName.put(name, medicationsByName.getOrDefault(name, 0L) + 1);
        }
        
        // 약물별 복약 준수율
        Map<String, Double> adherenceByMedication = new HashMap<>();
        for (Medication medication : allMedications) {
            List<MedicationReminder> reminders = reminderRepository.findByMedication(medication);
            
            long taken = reminders.stream()
                    .filter(r -> r.getReminderStatus() == MedicationReminder.ReminderStatus.TAKEN)
                    .count();
            
            long missed = reminders.stream()
                    .filter(r -> r.getReminderStatus() == MedicationReminder.ReminderStatus.MISSED)
                    .count();
            
            if (taken + missed > 0) {
                double rate = (double) taken / (taken + missed) * 100.0;
                adherenceByMedication.put(medication.getMedicationName(), rate);
            }
        }
        
        // 시간대별 미복용 통계
        Map<String, Long> missedByTimeOfDay = new HashMap<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH");
        
        for (MedicationReminder reminder : allReminders) {
            if (reminder.getReminderStatus() == MedicationReminder.ReminderStatus.MISSED) {
                String hour = reminder.getReminderTime().format(timeFormatter);
                missedByTimeOfDay.put(hour, missedByTimeOfDay.getOrDefault(hour, 0L) + 1);
            }
        }
        
        return MedicationStatisticsDto.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .totalMedications((long) allMedications.size())
                .activeMedications((long) activeMedications.size())
                .totalReminders((long) allReminders.size())
                .takenReminders(takenReminders)
                .missedReminders(missedReminders)
                .pendingReminders(pendingReminders)
                .adherenceRate(adherenceRate)
                .medicationsByName(medicationsByName)
                .adherenceByMedication(adherenceByMedication)
                .missedByTimeOfDay(missedByTimeOfDay)
                .build();
    }
}
