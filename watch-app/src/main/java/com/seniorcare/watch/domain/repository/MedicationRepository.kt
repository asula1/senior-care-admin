package com.seniorcare.watch.domain.repository

import com.seniorcare.watch.domain.model.Medication
import com.seniorcare.watch.domain.model.MedicationReminder
import java.util.Date

/**
 * 약물 관리 관련 레포지토리 인터페이스
 */
interface MedicationRepository {
    
    /**
     * 모든 약물 정보 가져오기
     * @return 약물 정보 목록
     */
    suspend fun getAllMedications(): List<Medication>
    
    /**
     * 현재 활성화된 약물 정보 가져오기
     * @return 활성화된 약물 정보 목록
     */
    suspend fun getActiveMedications(): List<Medication>
    
    /**
     * 특정 약물 정보 가져오기
     * @param medicationId 약물 ID
     * @return 약물 정보
     */
    suspend fun getMedicationById(medicationId: Long): Medication?
    
    /**
     * 약물 정보 동기화
     * @return 동기화 성공 여부
     */
    suspend fun syncMedications(): Boolean
    
    /**
     * 오늘의 약물 복용 알림 가져오기
     * @return 오늘 복용해야 할 약물 알림 목록
     */
    suspend fun getTodayReminders(): List<MedicationReminder>
    
    /**
     * 일정 기간 약물 복용 알림 가져오기
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 약물 알림 목록
     */
    suspend fun getRemindersByDateRange(startDate: Date, endDate: Date): List<MedicationReminder>
    
    /**
     * 약물 복용 처리
     * @param reminderId 알림 ID
     * @return 업데이트된 알림 정보
     */
    suspend fun markReminderAsTaken(reminderId: Long): MedicationReminder
    
    /**
     * 약물 미복용 처리
     * @param reminderId 알림 ID
     * @return 업데이트된 알림 정보
     */
    suspend fun markReminderAsMissed(reminderId: Long): MedicationReminder
    
    /**
     * 다음 약물 복용 알림 시간 확인
     * @return 다음 알림 시간
     */
    suspend fun getNextReminderTime(): Date?
}
