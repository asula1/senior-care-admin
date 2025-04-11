package com.seniorcare.watch.domain.repository

import com.seniorcare.watch.domain.model.HealthData
import com.seniorcare.watch.domain.model.HealthSummary
import java.util.Date

/**
 * 건강 데이터 관련 레포지토리 인터페이스
 */
interface HealthRepository {
    
    /**
     * 건강 데이터 기록 저장
     * @param healthData 저장할 건강 데이터
     * @return 저장된 건강 데이터 (ID 포함)
     */
    suspend fun saveHealthData(healthData: HealthData): HealthData
    
    /**
     * 최근 건강 데이터 가져오기
     * @return 가장 최근 건강 데이터
     */
    suspend fun getLatestHealthData(): HealthData?
    
    /**
     * 특정 기간 건강 데이터 가져오기
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 건강 데이터 목록
     */
    suspend fun getHealthDataByDateRange(startDate: Date, endDate: Date): List<HealthData>
    
    /**
     * 특정 날짜 건강 데이터 요약 가져오기
     * @param date 요청 날짜
     * @return 해당 날짜 건강 데이터 요약
     */
    suspend fun getHealthSummaryByDate(date: Date): HealthSummary
    
    /**
     * 로컬 및 서버 건강 데이터 동기화
     * @return 동기화 성공 여부
     */
    suspend fun syncHealthData(): Boolean
    
    /**
     * 심박수 이상 체크
     * @param heartRate 체크할 심박수
     * @return 이상 여부 (true: 이상 있음)
     */
    fun checkAbnormalHeartRate(heartRate: Int): Boolean
    
    /**
     * 활동 패턴 이상 체크
     * @param recentSteps 최근 걸음 수
     * @param averageSteps 평균 걸음 수
     * @return 이상 여부 (true: 이상 있음)
     */
    fun checkAbnormalActivity(recentSteps: Int, averageSteps: Int): Boolean
}
