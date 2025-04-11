import Foundation

struct Constants {
    // API 관련 상수
    struct API {
        static let baseURL = "https://seniorcare-api.example.com/api"
        static let timeout: TimeInterval = 30
    }
    
    // 알림 관련 상수
    struct Notifications {
        static let medicationCategory = "MEDICATION"
        static let emergencyCategory = "EMERGENCY"
        
        static let markAsTakenAction = "MARK_AS_TAKEN"
        static let cancelAction = "CANCEL"
    }
    
    // 건강 데이터 관련 상수
    struct Health {
        static let normalHeartRateMin: Double = 60
        static let normalHeartRateMax: Double = 100
        
        static let normalBloodPressureSystolicMin: Int = 90
        static let normalBloodPressureSystolicMax: Int = 130
        
        static let normalBloodPressureDiastolicMin: Int = 60
        static let normalBloodPressureDiastolicMax: Int = 80
        
        static let normalBloodGlucoseMin: Double = 70
        static let normalBloodGlucoseMax: Double = 140
    }
    
    // 앱 관련 상수
    struct App {
        static let appName = "SeniorCare"
        static let version = "1.0"
    }
}
