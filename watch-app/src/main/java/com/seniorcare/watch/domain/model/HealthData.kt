package com.seniorcare.watch.domain.model

import java.util.Date

/**
 * 건강 데이터 모델
 */
data class HealthData(
    val id: Long? = null,
    val heartRate: Int? = null,
    val stepsCount: Int? = null,
    val caloriesBurned: Double? = null,
    val sleepDurationMinutes: Int? = null,
    val sleepQuality: String? = null,
    val bloodPressureSystolic: Int? = null,
    val bloodPressureDiastolic: Int? = null,
    val bloodOxygen: Double? = null,
    val bodyTemperature: Double? = null,
    val recordedAt: Date = Date(),
    val deviceId: String? = null,
    val dataSource: DataSource = DataSource.WATCH
) {
    enum class DataSource {
        WATCH,
        PHONE,
        MANUAL,
        EXTERNAL_DEVICE
    }
}

/**
 * 건강 데이터 요약 모델
 */
data class HealthSummary(
    val date: Date,
    val averageHeartRate: Int? = null,
    val minHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val totalSteps: Int? = null,
    val totalCaloriesBurned: Double? = null,
    val totalSleepMinutes: Int? = null,
    val averageBloodPressureSystolic: Int? = null,
    val averageBloodPressureDiastolic: Int? = null,
    val averageBloodOxygen: Double? = null
)
