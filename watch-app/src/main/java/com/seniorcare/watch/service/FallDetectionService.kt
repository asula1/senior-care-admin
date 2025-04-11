package com.seniorcare.watch.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.seniorcare.watch.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class FallDetectionService : Service(), SensorEventListener {
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "FallDetectionChannel"
        
        // 낙상 감지 알고리즘 파라미터
        private const val FALL_THRESHOLD = 19.6f // 약 2g (중력 가속도의 2배)
        private const val IMPACT_WINDOW_MS = 500L // 충격 감지 윈도우 (밀리초)
        private const val INACTIVITY_THRESHOLD_MS = 2000L // 낙상 후 비활동 시간 (밀리초)
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // 센서 관련 변수
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    
    // 낙상 감지 관련 변수
    private var lastFallTimestamp = 0L
    private var potentialFallDetected = false
    private var inactivityCheckJob: Job? = null
    
    // 알림 및 진동 관련
    private lateinit var notificationManager: NotificationManager
    private lateinit var vibrator: Vibrator
    
    // 웨이크락
    private lateinit var wakeLock: PowerManager.WakeLock
    
    // 낙상 감지 상태
    private val _fallDetectionState = MutableStateFlow(FallDetectionState.MONITORING)
    val fallDetectionState = _fallDetectionState.asStateFlow()
    
    // 취소 카운트다운 작업
    private var countdownJob: Job? = null
    private val _countdownSeconds = MutableStateFlow(10)
    val countdownSeconds = _countdownSeconds.asStateFlow()
    
    @Inject
    lateinit var emergencyRepository: EmergencyRepository
    
    override fun onCreate() {
        super.onCreate()
        
        // 센서 매니저 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        // 알림 및 진동 초기화
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        // 웨이크락 초기화
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SeniorCare:FallDetectionWakeLock"
        )
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("낙상 감지 서비스 시작")
        
        // 포그라운드 서비스 시작
        startForeground(NOTIFICATION_ID, createNotification("낙상 감지 모니터링 중..."))
        
        // 웨이크락 획득 - 타임아웃 30분으로 설정
        wakeLock.acquire(TimeUnit.MINUTES.toMillis(30))
        
        // 가속도계 리스너 등록
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        
        _fallDetectionState.value = FallDetectionState.MONITORING
        
        return START_STICKY
    }
    
    override fun onDestroy() {
        Timber.d("낙상 감지 서비스 종료")
        
        // 리스너 등록 해제
        sensorManager.unregisterListener(this)
        
        // 코루틴 작업 취소
        countdownJob?.cancel()
        inactivityCheckJob?.cancel()
        serviceScope.cancel()
        
        // 웨이크락 해제
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            
            // 가속도 크기 계산 (중력 포함)
            val acceleration = sqrt(x * x + y * y + z * z)
            
            // 현재 상태가 모니터링 중인 경우에만 낙상 감지
            if (_fallDetectionState.value == FallDetectionState.MONITORING) {
                detectFall(acceleration)
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 필요 시 구현
    }
    
    private fun detectFall(acceleration: Float) {
        val currentTime = System.currentTimeMillis()
        
        // 충격 감지 (최소 기준치 이상)
        if (acceleration > FALL_THRESHOLD) {
            Timber.d("강한 충격 감지: $acceleration")
            
            if (!potentialFallDetected) {
                potentialFallDetected = true
                lastFallTimestamp = currentTime
                
                // 비활성 체크 작업 시작
                inactivityCheckJob?.cancel()
                inactivityCheckJob = serviceScope.launch {
                    delay(INACTIVITY_THRESHOLD_MS)
                    
                    // 비활성 기간 후 낙상으로 확정
                    if (potentialFallDetected) {
                        Timber.d("낙상 감지!")
                        handleFallDetection()
                    }
                }
            }
        } else if (potentialFallDetected && 
                 currentTime - lastFallTimestamp > IMPACT_WINDOW_MS) {
            // 충격 후 일정 시간 비활성 상태 확인
            // 이 코드는 충격 후 활동이 다시 감지된 경우 실행 안됨
        }
    }
    
    private fun handleFallDetection() {
        // 낙상 경고 알림 상태로 변경
        _fallDetectionState.value = FallDetectionState.WARNING
        
        // 진동 알림
        vibrateAlert()
        
        // 알림 업데이트
        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification("낙상이 감지되었습니다! 괜찮으신가요?")
        )
        
        // 10초 카운트다운 시작 (사용자가 취소할 수 있는 시간)
        startCancelCountdown()
    }
    
    private fun startCancelCountdown() {
        _countdownSeconds.value = 10
        
        countdownJob?.cancel()
        countdownJob = serviceScope.launch {
            for (i in 10 downTo 1) {
                _countdownSeconds.value = i
                delay(1000)
            }
            
            // 카운트다운 완료 후 비상 알림 발송
            sendEmergencyAlert()
        }
    }
    
    // 비상 알림 취소
    fun cancelEmergencyAlert() {
        countdownJob?.cancel()
        potentialFallDetected = false
        _fallDetectionState.value = FallDetectionState.MONITORING
        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification("낙상 감지 모니터링 중...")
        )
    }
    
    // 비상 알림 즉시 발송
    fun sendEmergencyAlertNow() {
        countdownJob?.cancel()
        sendEmergencyAlert()
    }
    
    private fun sendEmergencyAlert() {
        _fallDetectionState.value = FallDetectionState.ALERT_SENT
        
        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification("비상 알림이 전송되었습니다")
        )
        
        // 실제 비상 알림 전송 로직
        serviceScope.launch {
            try {
                emergencyRepository.sendFallDetectionAlert()
                
                // 5초 후 모니터링 상태로 복귀
                delay(5000)
                potentialFallDetected = false
                _fallDetectionState.value = FallDetectionState.MONITORING
                
                notificationManager.notify(
                    NOTIFICATION_ID,
                    createNotification("낙상 감지 모니터링 중...")
                )
            } catch (e: Exception) {
                Timber.e(e, "비상 알림 전송 실패")
                _fallDetectionState.value = FallDetectionState.ERROR
            }
        }
    }
    
    private fun vibrateAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 500, 500, 500, 500),
                    intArrayOf(0, 255, 0, 255, 0, 255),
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 500, 500, 500, 500, 500), -1)
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "낙상 감지",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "낙상 감지 알림 채널"
                enableVibration(true)
            }
            
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(message: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("노인 케어")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .build()
    }
    
    enum class FallDetectionState {
        MONITORING,
        WARNING,
        ALERT_SENT,
        ERROR
    }
}
