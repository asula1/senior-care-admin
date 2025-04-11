package com.seniorcare.watch.domain.repository

import com.seniorcare.watch.domain.model.EmergencyAlert
import com.seniorcare.watch.domain.model.Location

/**
 * 비상 알림 관련 레포지토리 인터페이스
 */
interface EmergencyRepository {
    
    /**
     * SOS 비상 알림 전송
     * @param location 현재 위치 정보 (선택)
     * @return 알림 전송 결과
     */
    suspend fun sendSosAlert(location: Location? = null): EmergencyAlert
    
    /**
     * 낙상 감지 알림 전송
     * @param location 현재 위치 정보 (선택)
     * @return 알림 전송 결과
     */
    suspend fun sendFallDetectionAlert(location: Location? = null): EmergencyAlert
    
    /**
     * 비정상 심박수 알림 전송
     * @param heartRate 감지된 심박수
     * @param location 현재 위치 정보 (선택)
     * @return 알림 전송 결과
     */
    suspend fun sendAbnormalHeartRateAlert(heartRate: Int, location: Location? = null): EmergencyAlert
    
    /**
     * 활성 알림 목록 가져오기
     * @return 활성 알림 목록
     */
    suspend fun getActiveAlerts(): List<EmergencyAlert>
    
    /**
     * 알림 취소하기
     * @param alertId 취소할 알림 ID
     */
    suspend fun cancelAlert(alertId: Long): Boolean
}
