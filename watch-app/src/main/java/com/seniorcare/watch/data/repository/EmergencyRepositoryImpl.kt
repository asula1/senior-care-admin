package com.seniorcare.watch.data.repository

import com.seniorcare.watch.domain.model.EmergencyAlert
import com.seniorcare.watch.domain.model.Location
import com.seniorcare.watch.domain.repository.EmergencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EmergencyRepository 구현체
 * 
 * 현재는 백엔드 연동 없이 테스트용으로 구현되어 있습니다.
 * 실제 구현 시 Retrofit을 사용하여 백엔드 API와 통신해야 합니다.
 */
@Singleton
class EmergencyRepositoryImpl @Inject constructor() : EmergencyRepository {
    
    // 테스트용 임시 알림 저장소
    private val activeAlerts = mutableListOf<EmergencyAlert>()
    private var nextId = 1L
    
    override suspend fun sendSosAlert(location: Location?): EmergencyAlert = withContext(Dispatchers.IO) {
        Timber.d("SOS 알림 전송 시도")
        
        // 네트워크 지연 시뮬레이션
        delay(1000)
        
        val alert = EmergencyAlert(
            id = nextId++,
            type = EmergencyAlert.AlertType.SOS,
            message = "SOS 비상 버튼이 눌렸습니다",
            location = location,
            heartRate = null,
            triggeredAt = Date(),
            status = EmergencyAlert.AlertStatus.ACTIVE
        )
        
        activeAlerts.add(alert)
        Timber.d("SOS 알림 전송 성공: $alert")
        
        return@withContext alert
    }
    
    override suspend fun sendFallDetectionAlert(location: Location?): EmergencyAlert = withContext(Dispatchers.IO) {
        Timber.d("낙상 감지 알림 전송 시도")
        
        // 네트워크 지연 시뮬레이션
        delay(1000)
        
        val alert = EmergencyAlert(
            id = nextId++,
            type = EmergencyAlert.AlertType.FALL_DETECTED,
            message = "낙상이 감지되었습니다",
            location = location,
            heartRate = null,
            triggeredAt = Date(),
            status = EmergencyAlert.AlertStatus.ACTIVE
        )
        
        activeAlerts.add(alert)
        Timber.d("낙상 감지 알림 전송 성공: $alert")
        
        return@withContext alert
    }
    
    override suspend fun sendAbnormalHeartRateAlert(heartRate: Int, location: Location?): EmergencyAlert = withContext(Dispatchers.IO) {
        Timber.d("비정상 심박수 알림 전송 시도: $heartRate BPM")
        
        // 네트워크 지연 시뮬레이션
        delay(1000)
        
        val alert = EmergencyAlert(
            id = nextId++,
            type = EmergencyAlert.AlertType.ABNORMAL_HEART_RATE,
            message = "비정상 심박수가 감지되었습니다: $heartRate BPM",
            location = location,
            heartRate = heartRate,
            triggeredAt = Date(),
            status = EmergencyAlert.AlertStatus.ACTIVE
        )
        
        activeAlerts.add(alert)
        Timber.d("비정상 심박수 알림 전송 성공: $alert")
        
        return@withContext alert
    }
    
    override suspend fun getActiveAlerts(): List<EmergencyAlert> = withContext(Dispatchers.IO) {
        Timber.d("활성 알림 목록 조회")
        return@withContext activeAlerts.filter { it.status == EmergencyAlert.AlertStatus.ACTIVE }
    }
    
    override suspend fun cancelAlert(alertId: Long): Boolean = withContext(Dispatchers.IO) {
        Timber.d("알림 취소 시도: ID=$alertId")
        
        // 네트워크 지연 시뮬레이션
        delay(500)
        
        val alertIndex = activeAlerts.indexOfFirst { it.id == alertId }
        if (alertIndex != -1) {
            val alert = activeAlerts[alertIndex]
            val updatedAlert = alert.copy(status = EmergencyAlert.AlertStatus.CANCELED)
            activeAlerts[alertIndex] = updatedAlert
            Timber.d("알림 취소 성공: $updatedAlert")
            return@withContext true
        }
        
        Timber.d("알림 취소 실패: 알림을 찾을 수 없음")
        return@withContext false
    }
}