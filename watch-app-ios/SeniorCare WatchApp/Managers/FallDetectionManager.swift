import Foundation
import CoreMotion
import Combine

class FallDetectionManager: ObservableObject {
    private let motionManager = CMMotionManager()
    private let apiService = ApiService()
    private let locationManager = LocationManager()
    
    // 발행할 상태 값
    @Published var isFallDetected: Bool = false
    @Published var isMonitoring: Bool = false
    
    // 민감도 설정 (높을수록 덜 민감)
    private let fallThreshold: Double = 3.0 // g (중력 가속도 단위)
    
    // 알림 간격 제한 (초)
    private let notificationInterval: TimeInterval = 60
    private var lastNotificationTime: Date?
    
    init() {
        // 초기화시 필요한 설정
    }
    
    // 낙상 감지 모니터링 시작
    func startMonitoring() {
        guard motionManager.isAccelerometerAvailable else {
            print("가속도계를 사용할 수 없습니다.")
            return
        }
        
        motionManager.accelerometerUpdateInterval = 0.1 // 100ms마다 업데이트
        
        motionManager.startAccelerometerUpdates(to: .main) { [weak self] (data, error) in
            guard let self = self, let data = data, error == nil else {
                return
            }
            
            // 중력 가속도 크기 계산 (3축 벡터 크기)
            let acceleration = sqrt(pow(data.acceleration.x, 2) + pow(data.acceleration.y, 2) + pow(data.acceleration.z, 2))
            
            // 임계값 초과 확인 (낙상 감지)
            if acceleration > self.fallThreshold {
                self.handleFallDetection()
            }
        }
        
        isMonitoring = true
    }
    
    // 낙상 감지 모니터링 중지
    func stopMonitoring() {
        motionManager.stopAccelerometerUpdates()
        isMonitoring = false
    }
    
    // 낙상 감지시 처리
    private func handleFallDetection() {
        // 알림 간격 확인 (너무 잦은 알림 방지)
        if let lastTime = lastNotificationTime, Date().timeIntervalSince(lastTime) < notificationInterval {
            return
        }
        
        lastNotificationTime = Date()
        isFallDetected = true
        
        // 다른 메니저에 알림
        NotificationCenter.default.post(name: Notification.Name("FallDetected"), object: nil)
        
        // 사용자에게 확인 요청 (5초 대기)
        DispatchQueue.main.asyncAfter(deadline: .now() + 5) { [weak self] in
            guard let self = self, self.isFallDetected else { return }
            
            // 사용자가 취소하지 않았으면 응급 알림 전송
            self.sendEmergencyAlert()
        }
    }
    
    // 낙상 감지 취소 (사용자가 알림 확인)
    func cancelFallDetection() {
        isFallDetected = false
    }
    
    // 응급 알림 전송
    private func sendEmergencyAlert() {
        // 위치 정보 요청
        locationManager.requestLocation()
        
        // 위치 정보와 함께 알림 전송
        let location = locationManager.isAuthorized ? 
            (latitude: locationManager.lastLocation?.coordinate.latitude, 
             longitude: locationManager.lastLocation?.coordinate.longitude) : nil
        
        apiService.sendEmergencyAlert(location: location.map { ($0!, $1!) }) { result in
            switch result {
            case .success:
                print("응급 알림 전송 성공")
            case .failure(let error):
                print("응급 알림 전송 실패: \(error.localizedDescription)")
            }
        }
        
        // 로컬 알림 발생
        NotificationManager.shared.sendLocalNotification(
            title: "낙상 감지",
            body: "낙상이 감지되어 보호자에게 알림이 전송되었습니다."
        )
        
        // 상태 초기화
        isFallDetected = false
    }
}
