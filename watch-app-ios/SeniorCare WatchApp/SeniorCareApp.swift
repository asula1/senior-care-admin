import SwiftUI
import HealthKit

@main
struct SeniorCareApp: App {
    @StateObject private var healthManager = HealthManager()
    @StateObject private var locationManager = LocationManager()
    @StateObject private var fallDetectionManager = FallDetectionManager()
    @StateObject private var notificationManager = NotificationManager()
    
    // HealthKit 권한 요청
    init() {
        // 앱이 처음 시작될 때 HealthKit 권한 설정
        if HKHealthStore.isHealthDataAvailable() {
            let healthStore = HKHealthStore()
            let typesToRead: Set<HKObjectType> = [
                HKObjectType.quantityType(forIdentifier: .heartRate)!,
                HKObjectType.quantityType(forIdentifier: .stepCount)!,
                HKObjectType.categoryType(forIdentifier: .sleepAnalysis)!,
                HKObjectType.quantityType(forIdentifier: .bloodPressureSystolic)!,
                HKObjectType.quantityType(forIdentifier: .bloodPressureDiastolic)!,
                HKObjectType.quantityType(forIdentifier: .bloodGlucose)!
            ]
            
            healthStore.requestAuthorization(toShare: [], read: typesToRead) { success, error in
                if let error = error {
                    print("HealthKit 권한 요청 실패: \(error.localizedDescription)")
                }
            }
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(healthManager)
                .environmentObject(locationManager)
                .environmentObject(fallDetectionManager)
                .environmentObject(notificationManager)
                .onAppear {
                    // 앱 시작 시 설정
                    notificationManager.requestPermissions()
                    fallDetectionManager.startMonitoring()
                    healthManager.startMonitoring()
                }
        }
    }
}
