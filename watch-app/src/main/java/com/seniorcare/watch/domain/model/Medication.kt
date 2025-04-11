package com.seniorcare.watch.domain.model

import java.util.Date

/**
 * 약물 정보 모델
 */
data class Medication(
    val id: Long,
    val medicationName: String,
    val dosage: String,
    val frequency: String,
    val startDate: Date,
    val endDate: Date? = null,
    val instructions: String? = null,
    val prescriptionId: String? = null,
    val pharmacyName: String? = null,
    val prescriberName: String? = null,
    val createdAt: Date = Date()
)

/**
 * 약물 복용 알림 모델
 */
data class MedicationReminder(
    val id: Long,
    val medicationId: Long,
    val medicationName: String,
    val dosage: String,
    val reminderTime: Date,
    val reminderStatus: ReminderStatus,
    val takenAt: Date? = null,
    val createdAt: Date = Date()
) {
    enum class ReminderStatus {
        SCHEDULED,  // 예정됨
        SENT,       // 알림 전송됨
        TAKEN,      // 복용함
        MISSED,     // 복용 안함
        CANCELED    // 취소됨
    }
}
