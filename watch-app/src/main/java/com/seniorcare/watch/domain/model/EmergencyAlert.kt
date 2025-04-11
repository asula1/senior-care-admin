package com.seniorcare.watch.domain.model

import java.util.Date

/**
 * 비상 알림 데이터 모델
 */
data class EmergencyAlert(
    val id: Long,
    val type: AlertType,
    val message: String,
    val location: Location? = null,
    val heartRate: Int? = null,
    val triggeredAt: Date,
    val status: AlertStatus
) {
    enum class AlertType {
        SOS,
        FALL_DETECTED,
        ABNORMAL_HEART_RATE,
        ABNORMAL_ACTIVITY,
        LEAVING_SAFE_ZONE,
        INACTIVITY
    }
    
    enum class AlertStatus {
        ACTIVE,
        ACKNOWLEDGED,
        RESOLVED,
        CANCELED
    }
}

/**
 * 위치 정보 데이터 모델
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val address: String? = null
)
