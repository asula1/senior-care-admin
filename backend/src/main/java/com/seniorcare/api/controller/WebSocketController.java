package com.seniorcare.api.controller;

import com.seniorcare.api.dto.alert.EmergencyAlertDto;
import com.seniorcare.api.dto.medication.MedicationReminderDto;
import com.seniorcare.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트가 새로운 비상 알림을 발송하는 엔드포인트
     */
    @MessageMapping("/emergency")
    @SendTo("/topic/alerts")
    public EmergencyAlertDto broadcastEmergencyAlert(@Payload EmergencyAlertDto alert, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        
        // 사용자 정보 추가
        if (user != null && alert != null) {
            alert.setUserName(user.getName());
        }
        
        return alert;
    }

    /**
     * 클라이언트가 특정 사용자에게 복약 알림을 발송하는 엔드포인트
     */
    @MessageMapping("/reminder")
    public void sendMedicationReminder(@Payload MedicationReminderDto reminder, SimpMessageHeaderAccessor headerAccessor) {
        // 대상 사용자에게 알림 전송
        if (reminder.getUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    reminder.getUserId().toString(),
                    "/queue/reminders",
                    reminder
            );
        }
    }

    /**
     * 클라이언트가 알림을 확인했음을 보내는 엔드포인트
     */
    @MessageMapping("/alert/acknowledge")
    public void acknowledgeAlert(@Payload Long alertId, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        
        if (user != null) {
            // 알림 처리 로직 (필요한 경우 서비스 호출)
            
            // 다른 사용자에게 업데이트 알림
            messagingTemplate.convertAndSend("/topic/alerts/updates", alertId);
        }
    }

    /**
     * 사용자 활동 상태 업데이트 (예: 온라인, 오프라인)
     */
    @MessageMapping("/user/status")
    public void updateUserStatus(@Payload String status, SimpMessageHeaderAccessor headerAccessor) {
        Principal user = headerAccessor.getUser();
        
        if (user != null) {
            messagingTemplate.convertAndSend("/topic/users/status", user.getName() + ":" + status);
        }
    }
}
