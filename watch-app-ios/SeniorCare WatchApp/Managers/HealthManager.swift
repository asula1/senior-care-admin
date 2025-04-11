import Foundation
import Combine
import HealthKit

class HealthManager: ObservableObject {
    private let healthKitService = HealthKitService()
    private let apiService = ApiService()
    
    // 발행할 건강 데이터
    @Published var heartRate: Double = 72
    @Published var stepCount: Int = 0
    @Published var sleepHours: Double = 0
    @Published var systolicPressure: Int = 120
    @Published var diastolicPressure: Int = 80
    @Published var bloodGlucose: Double = 95
    
    @Published var lastUpdated: Date = Date()
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    
    private var timerCancellable: AnyCancellable?
    
    init() {
        // 시작시 전체 데이터 로드
        refreshData()
    }
    
    // 건강 데이터 전체 갱신
    func refreshData() {
        isLoading = true
        errorMessage = nil
        
        healthKitService.fetchAllHealthData { [weak self] healthData in
            DispatchQueue.main.async {
                guard let self = self else { return }
                self.isLoading = false
                
                if let healthData = healthData {
                    self.heartRate = healthData.heartRate
                    self.stepCount = healthData.stepCount
                    self.sleepHours = healthData.sleepHours
                    self.systolicPressure = healthData.systolicPressure
                    self.diastolicPressure = healthData.diastolicPressure
                    self.bloodGlucose = healthData.bloodGlucose
                    self.lastUpdated = Date()
                    
                    // 서버에 데이터 업로드
                    self.uploadHealthData(healthData)
                } else {
                    self.errorMessage = "건강 데이터를 가져오지 못했습니다."
                    
                    // 데이터가 없으면 예시 데이터 사용
                    let sampleData = HealthData.sample
                    self.heartRate = sampleData.heartRate
                    self.stepCount = sampleData.stepCount
                    self.sleepHours = sampleData.sleepHours
                    self.systolicPressure = sampleData.systolicPressure
                    self.diastolicPressure = sampleData.diastolicPressure
                    self.bloodGlucose = sampleData.bloodGlucose
                }
            }
        }
    }
    
    // 건강 모니터링 시작
    func startMonitoring() {
        // 심박수 실시간 모니터링
        healthKitService.startHeartRateMonitoring { [weak self] newHeartRate in
            DispatchQueue.main.async {
                self?.heartRate = newHeartRate
            }
        }
        
        // 정기적인 데이터 업데이트 (15분마다)
        timerCancellable = Timer.publish(every: 900, on: .main, in: .common)
            .autoconnect()
            .sink { [weak self] _ in
                self?.refreshData()
            }
    }
    
    // 서버에 건강 데이터 업로드
    private func uploadHealthData(_ healthData: HealthData) {
        apiService.uploadHealthData(healthData) { result in
            switch result {
            case .success:
                print("건강 데이터 업로드 성공")
            case .failure(let error):
                print("건강 데이터 업로드 실패: \(error.localizedDescription)")
            }
        }
    }
    
    // 비정상 건강 상태 체크
    func checkAbnormalStatus() -> (isAbnormal: Bool, message: String?) {
        // 심박수 이상 체크 (너무 높거나 낮음)
        if heartRate > 120 {
            return (true, "심박수가 매우 높습니다: \(Int(heartRate)) bpm")
        } else if heartRate < 50 {
            return (true, "심박수가 매우 낮습니다: \(Int(heartRate)) bpm")
        }
        
        // 혈압 이상 체크
        if systolicPressure > 160 || diastolicPressure > 100 {
            return (true, "혈압이 매우 높습니다: \(systolicPressure)/\(diastolicPressure) mmHg")
        }
        
        // 혈당 이상 체크
        if bloodGlucose > 200 {
            return (true, "혈당이 매우 높습니다: \(Int(bloodGlucose)) mg/dL")
        } else if bloodGlucose < 70 {
            return (true, "혈당이 매우 낮습니다: \(Int(bloodGlucose)) mg/dL")
        }
        
        return (false, nil)
    }
    
    deinit {
        timerCancellable?.cancel()
    }
}
