package com.seniorcare.api.service;

import com.seniorcare.api.dto.visit.VisitScheduleDto;
import com.seniorcare.api.exception.BadRequestException;
import com.seniorcare.api.exception.ResourceNotFoundException;
import com.seniorcare.api.model.User;
import com.seniorcare.api.model.UserRole;
import com.seniorcare.api.model.VisitSchedule;
import com.seniorcare.api.repository.UserRepository;
import com.seniorcare.api.repository.VisitScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitScheduleService {

    private final VisitScheduleRepository visitRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<VisitScheduleDto> getAllUpcomingVisits() {
        return visitRepository.findUpcomingVisits(LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitScheduleDto> getUpcomingVisitsBySenior(Long seniorId) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", seniorId));
        
        return visitRepository.findUpcomingVisitsBySenior(senior, LocalDateTime.now()).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitScheduleDto> getVisitsBySenior(Long seniorId) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", seniorId));
        
        return visitRepository.findBySenior(senior).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitScheduleDto> getVisitsBySocialWorker(Long socialWorkerId) {
        User socialWorker = userRepository.findById(socialWorkerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", socialWorkerId));
        
        if (!socialWorker.getRoles().contains(UserRole.ROLE_SOCIAL_WORKER)) {
            throw new BadRequestException("사용자는 복지사가 아닙니다");
        }
        
        return visitRepository.findBySocialWorker(socialWorker).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VisitScheduleDto> getVisitsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        List<VisitSchedule> visits;
        if (user.getRoles().contains(UserRole.ROLE_SENIOR)) {
            visits = visitRepository.findBySeniorAndTimeRange(user, startDateTime, endDateTime);
        } else if (user.getRoles().contains(UserRole.ROLE_SOCIAL_WORKER)) {
            visits = visitRepository.findBySocialWorkerAndTimeRange(user, startDateTime, endDateTime);
        } else {
            throw new BadRequestException("사용자는 노인이나 복지사가 아닙니다");
        }
        
        return visits.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VisitScheduleDto scheduleVisit(Long seniorId, Long socialWorkerId, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       String purpose, String notes) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", seniorId));
        
        User socialWorker = userRepository.findById(socialWorkerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", socialWorkerId));
        
        if (!senior.getRoles().contains(UserRole.ROLE_SENIOR)) {
            throw new BadRequestException("선택한 사용자는 노인이 아닙니다");
        }
        
        if (!socialWorker.getRoles().contains(UserRole.ROLE_SOCIAL_WORKER)) {
            throw new BadRequestException("선택한 사용자는 복지사가 아닙니다");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new BadRequestException("방문 시작 시간은 종료 시간보다 이전이어야 합니다");
        }
        
        // 복지사의 방문 일정 중복 체크
        List<VisitSchedule> conflictingVisits = visitRepository.findBySocialWorkerAndTimeRange(
                socialWorker, startTime.minusMinutes(1), endTime.plusMinutes(1));
        
        if (!conflictingVisits.isEmpty()) {
            throw new BadRequestException("복지사가 해당 시간에 이미 다른 방문이 예정되어 있습니다");
        }
        
        VisitSchedule visitSchedule = VisitSchedule.builder()
                .senior(senior)
                .socialWorker(socialWorker)
                .visitPurpose(purpose)
                .scheduledStartTime(startTime)
                .scheduledEndTime(endTime)
                .notes(notes)
                .visitStatus(VisitSchedule.VisitStatus.SCHEDULED)
                .build();
        
        VisitSchedule savedVisit = visitRepository.save(visitSchedule);
        
        // 노인과 복지사에게 웹소켓 알림 전송
        VisitScheduleDto visitDto = convertToDto(savedVisit);
        messagingTemplate.convertAndSendToUser(
                senior.getUsername(),
                "/queue/visits",
                visitDto
        );
        messagingTemplate.convertAndSendToUser(
                socialWorker.getUsername(),
                "/queue/visits",
                visitDto
        );
        
        return visitDto;
    }

    @Transactional
    public VisitScheduleDto updateVisitStatus(Long visitId, VisitSchedule.VisitStatus newStatus, String notes) {
        VisitSchedule visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitSchedule", "id", visitId));
        
        visit.setVisitStatus(newStatus);
        
        if (notes != null && !notes.trim().isEmpty()) {
            visit.setNotes(visit.getNotes() != null ? visit.getNotes() + "\n" + notes : notes);
        }
        
        if (newStatus == VisitSchedule.VisitStatus.IN_PROGRESS) {
            visit.setActualStartTime(LocalDateTime.now());
        } else if (newStatus == VisitSchedule.VisitStatus.COMPLETED) {
            visit.setActualEndTime(LocalDateTime.now());
        }
        
        VisitSchedule updatedVisit = visitRepository.save(visit);
        
        // 상태 변경 알림 전송
        VisitScheduleDto visitDto = convertToDto(updatedVisit);
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSenior().getUsername(),
                "/queue/visits",
                visitDto
        );
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSocialWorker().getUsername(),
                "/queue/visits",
                visitDto
        );
        
        return visitDto;
    }

    @Transactional
    public VisitScheduleDto rescheduleVisit(Long visitId, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        VisitSchedule visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitSchedule", "id", visitId));
        
        if (newStartTime.isAfter(newEndTime)) {
            throw new BadRequestException("방문 시작 시간은 종료 시간보다 이전이어야 합니다");
        }
        
        // 복지사의 방문 일정 중복 체크 (현재 방문 제외)
        List<VisitSchedule> conflictingVisits = visitRepository.findBySocialWorkerAndTimeRange(
                visit.getSocialWorker(), newStartTime.minusMinutes(1), newEndTime.plusMinutes(1));
        
        conflictingVisits.removeIf(v -> v.getId().equals(visitId));
        
        if (!conflictingVisits.isEmpty()) {
            throw new BadRequestException("복지사가 해당 시간에 이미 다른 방문이 예정되어 있습니다");
        }
        
        visit.setScheduledStartTime(newStartTime);
        visit.setScheduledEndTime(newEndTime);
        visit.setVisitStatus(VisitSchedule.VisitStatus.RESCHEDULED);
        
        VisitSchedule updatedVisit = visitRepository.save(visit);
        
        // 일정 변경 알림 전송
        VisitScheduleDto visitDto = convertToDto(updatedVisit);
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSenior().getUsername(),
                "/queue/visits",
                visitDto
        );
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSocialWorker().getUsername(),
                "/queue/visits",
                visitDto
        );
        
        return visitDto;
    }

    @Transactional
    public void cancelVisit(Long visitId, String reason) {
        VisitSchedule visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("VisitSchedule", "id", visitId));
        
        visit.setVisitStatus(VisitSchedule.VisitStatus.CANCELLED);
        
        if (reason != null && !reason.trim().isEmpty()) {
            visit.setNotes(visit.getNotes() != null ? visit.getNotes() + "\n취소 사유: " + reason : "취소 사유: " + reason);
        }
        
        VisitSchedule updatedVisit = visitRepository.save(visit);
        
        // 취소 알림 전송
        VisitScheduleDto visitDto = convertToDto(updatedVisit);
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSenior().getUsername(),
                "/queue/visits",
                visitDto
        );
        
        messagingTemplate.convertAndSendToUser(
                updatedVisit.getSocialWorker().getUsername(),
                "/queue/visits",
                visitDto
        );
    }

    // VisitSchedule 엔티티를 DTO로 변환
    private VisitScheduleDto convertToDto(VisitSchedule visit) {
        return VisitScheduleDto.builder()
                .id(visit.getId())
                .seniorId(visit.getSenior().getId())
                .seniorName(visit.getSenior().getFullName())
                .socialWorkerId(visit.getSocialWorker().getId())
                .socialWorkerName(visit.getSocialWorker().getFullName())
                .visitPurpose(visit.getVisitPurpose())
                .scheduledStartTime(visit.getScheduledStartTime())
                .scheduledEndTime(visit.getScheduledEndTime())
                .visitStatus(visit.getVisitStatus())
                .notes(visit.getNotes())
                .actualStartTime(visit.getActualStartTime())
                .actualEndTime(visit.getActualEndTime())
                .createdAt(visit.getCreatedAt())
                .build();
    }
}
