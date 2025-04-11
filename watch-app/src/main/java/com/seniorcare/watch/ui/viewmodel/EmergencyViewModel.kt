package com.seniorcare.watch.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seniorcare.watch.domain.model.EmergencyAlert
import com.seniorcare.watch.domain.model.Location
import com.seniorcare.watch.domain.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * 비상 알림 관련 ViewModel
 */
@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val emergencyRepository: EmergencyRepository
) : ViewModel() {
    
    // 비상 알림 상태
    private val _emergencyState = MutableStateFlow<EmergencyState>(EmergencyState.Idle)
    val emergencyState: StateFlow<EmergencyState> = _emergencyState.asStateFlow()
    
    // 활성 알림 목록
    private val _activeAlerts = MutableStateFlow<List<EmergencyAlert>>(emptyList())
    val activeAlerts: StateFlow<List<EmergencyAlert>> = _activeAlerts.asStateFlow()
    
    /**
     * SOS 알림 전송
     */
    fun sendSosAlert(location: Location? = null) {
        _emergencyState.value = EmergencyState.Sending
        
        viewModelScope.launch {
            try {
                val result = emergencyRepository.sendSosAlert(location)
                Timber.d("SOS 알림 전송 성공: $result")
                _emergencyState.value = EmergencyState.Sent(result)
                refreshActiveAlerts()
            } catch (e: Exception) {
                Timber.e(e, "SOS 알림 전송 실패")
                _emergencyState.value = EmergencyState.Error("SOS 알림 전송에 실패했습니다: ${e.message}")
            }
        }
    }
    
    /**
     * 낙상 감지 알림 전송
     */
    fun sendFallDetectionAlert(location: Location? = null) {
        _emergencyState.value = EmergencyState.Sending
        
        viewModelScope.launch {
            try {
                val result = emergencyRepository.sendFallDetectionAlert(location)
                Timber.d("낙상 감지 알림 전송 성공: $result")
                _emergencyState.value = EmergencyState.Sent(result)
                refreshActiveAlerts()
            } catch (e: Exception) {
                Timber.e(e, "낙상 감지 알림 전송 실패")
                _emergencyState.value = EmergencyState.Error("낙상 감지 알림 전송에 실패했습니다: ${e.message}")
            }
        }
    }
    
    /**
     * 비정상 심박수 알림 전송
     */
    fun sendAbnormalHeartRateAlert(heartRate: Int, location: Location? = null) {
        _emergencyState.value = EmergencyState.Sending
        
        viewModelScope.launch {
            try {
                val result = emergencyRepository.sendAbnormalHeartRateAlert(heartRate, location)
                Timber.d("비정상 심박수 알림 전송 성공: $result")
                _emergencyState.value = EmergencyState.Sent(result)
                refreshActiveAlerts()
            } catch (e: Exception) {
                Timber.e(e, "비정상 심박수 알림 전송 실패")
                _emergencyState.value = EmergencyState.Error("비정상 심박수 알림 전송에 실패했습니다: ${e.message}")
            }
        }
    }
    
    /**
     * 활성 알림 목록 새로고침
     */
    fun refreshActiveAlerts() {
        viewModelScope.launch {
            try {
                val alerts = emergencyRepository.getActiveAlerts()
                _activeAlerts.value = alerts
            } catch (e: Exception) {
                Timber.e(e, "활성 알림 목록 조회 실패")
            }
        }
    }
    
    /**
     * 알림 취소
     */
    fun cancelAlert(alertId: Long) {
        viewModelScope.launch {
            try {
                val success = emergencyRepository.cancelAlert(alertId)
                if (success) {
                    Timber.d("알림 취소 성공: ID=$alertId")
                    refreshActiveAlerts()
                } else {
                    Timber.d("알림 취소 실패: ID=$alertId")
                }
            } catch (e: Exception) {
                Timber.e(e, "알림 취소 중 오류 발생")
            }
        }
    }
    
    /**
     * 상태 초기화
     */
    fun resetState() {
        _emergencyState.value = EmergencyState.Idle
    }
    
    /**
     * 비상 알림 상태
     */
    sealed class EmergencyState {
        object Idle : EmergencyState()
        object Sending : EmergencyState()
        data class Sent(val alert: EmergencyAlert) : EmergencyState()
        data class Error(val message: String) : EmergencyState()
    }
}