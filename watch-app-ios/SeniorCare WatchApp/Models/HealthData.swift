import Foundation

struct HealthData: Codable, Identifiable {
    var id = UUID()
    var heartRate: Double
    var stepCount: Int
    var sleepHours: Double
    var systolicPressure: Int
    var diastolicPressure: Int
    var bloodGlucose: Double
    var timestamp: Date
    
    static var sample: HealthData {
        HealthData(
            heartRate: 72,
            stepCount: 3850,
            sleepHours: 7.2,
            systolicPressure: 125,
            diastolicPressure: 80,
            bloodGlucose: 95,
            timestamp: Date()
        )
    }
}
