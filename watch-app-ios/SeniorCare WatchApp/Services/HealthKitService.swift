import Foundation
import HealthKit

class HealthKitService {
    private let healthStore = HKHealthStore()
    private let apiService = ApiService()
    
    // 지원되는 건강 데이터 타입
    private let supportedTypes: Set<HKObjectType> = [
        HKObjectType.quantityType(forIdentifier: .heartRate)!,
        HKObjectType.quantityType(forIdentifier: .stepCount)!,
        HKObjectType.categoryType(forIdentifier: .sleepAnalysis)!,
        HKObjectType.quantityType(forIdentifier: .bloodPressureSystolic)!,
        HKObjectType.quantityType(forIdentifier: .bloodPressureDiastolic)!,
        HKObjectType.quantityType(forIdentifier: .bloodGlucose)!
    ]
    
    init() {
        requestAuthorization()
    }
    
    // HealthKit 접근 권한 요청
    func requestAuthorization() {
        guard HKHealthStore.isHealthDataAvailable() else {
            print("HealthKit을 사용할 수 없습니다.")
            return
        }
        
        healthStore.requestAuthorization(toShare: [], read: supportedTypes) { success, error in
            if let error = error {
                print("HealthKit 권한 요청 실패: \(error.localizedDescription)")
                return
            }
            
            if success {
                print("HealthKit 권한 요청 성공")
            } else {
                print("HealthKit 권한 거부됨")
            }
        }
    }
    
    // 최근 심박수 데이터 가져오기
    func fetchLatestHeartRate(completion: @escaping (Double?) -> Void) {
        guard let heartRateType = HKObjectType.quantityType(forIdentifier: .heartRate) else {
            completion(nil)
            return
        }
        
        let predicate = HKQuery.predicateForSamples(withStart: Date().addingTimeInterval(-3600), end: nil)
        let sortDescriptor = NSSortDescriptor(key: HKSampleSortIdentifierEndDate, ascending: false)
        
        let query = HKSampleQuery(
            sampleType: heartRateType,
            predicate: predicate,
            limit: 1,
            sortDescriptors: [sortDescriptor]
        ) { (_, samples, error) in
            guard error == nil, let sample = samples?.first as? HKQuantitySample else {
                completion(nil)
                return
            }
            
            let heartRate = sample.quantity.doubleValue(for: HKUnit.count().unitDivided(by: .minute()))
            completion(heartRate)
        }
        
        healthStore.execute(query)
    }
    
    // 오늘의 걸음 수 가져오기
    func fetchTodayStepCount(completion: @escaping (Int?) -> Void) {
        guard let stepCountType = HKObjectType.quantityType(forIdentifier: .stepCount) else {
            completion(nil)
            return
        }
        
        let calendar = Calendar.current
        let now = Date()
        guard let startOfDay = calendar.date(bySettingHour: 0, minute: 0, second: 0, of: now) else {
            completion(nil)
            return
        }
        
        let predicate = HKQuery.predicateForSamples(withStart: startOfDay, end: now, options: .strictStartDate)
        
        let query = HKStatisticsQuery(
            quantityType: stepCountType,
            quantitySamplePredicate: predicate,
            options: .cumulativeSum
        ) { (_, result, error) in
            guard error == nil, let result = result, let sum = result.sumQuantity() else {
                completion(nil)
                return
            }
            
            let steps = Int(sum.doubleValue(for: HKUnit.count()))
            completion(steps)
        }
        
        healthStore.execute(query)
    }
    
    // 수면 시간 가져오기 (지난 24시간)
    func fetchSleepHours(completion: @escaping (Double?) -> Void) {
        guard let sleepType = HKObjectType.categoryType(forIdentifier: .sleepAnalysis) else {
            completion(nil)
            return
        }
        
        let yesterday = Calendar.current.date(byAdding: .day, value: -1, to: Date())!
        let predicate = HKQuery.predicateForSamples(withStart: yesterday, end: Date(), options: .strictStartDate)
        
        let query = HKSampleQuery(
            sampleType: sleepType,
            predicate: predicate,
            limit: HKObjectQueryNoLimit,
            sortDescriptors: nil
        ) { (_, samples, error) in
            guard error == nil, let samples = samples as? [HKCategorySample] else {
                completion(nil)
                return
            }
            
            // inBed 상태의 샘플만 필터링
            let sleepSamples = samples.filter { $0.value == HKCategoryValueSleepAnalysis.inBed.rawValue }
            
            // 총 수면 시간 계산 (시간 단위)
            let totalSleepTime = sleepSamples.reduce(0) { total, sample in
                return total + sample.endDate.timeIntervalSince(sample.startDate)
            } / 3600 // 초를 시간으로 변환
            
            completion(totalSleepTime)
        }
        
        healthStore.execute(query)
    }
    
    // 혈압 데이터 가져오기 (최근)
    func fetchLatestBloodPressure(completion: @escaping ((systolic: Int, diastolic: Int)?) -> Void) {
        guard let systolicType = HKObjectType.quantityType(forIdentifier: .bloodPressureSystolic),
              let diastolicType = HKObjectType.quantityType(forIdentifier: .bloodPressureDiastolic) else {
            completion(nil)
            return
        }
        
        let predicate = HKQuery.predicateForSamples(withStart: Date().addingTimeInterval(-86400), end: nil)
        let sortDescriptor = NSSortDescriptor(key: HKSampleSortIdentifierEndDate, ascending: false)
        
        // 수축기 혈압 쿼리
        let systolicQuery = HKSampleQuery(
            sampleType: systolicType,
            predicate: predicate,
            limit: 1,
            sortDescriptors: [sortDescriptor]
        ) { (_, samples, error) in
            guard error == nil, let systolicSample = samples?.first as? HKQuantitySample else {
                completion(nil)
                return
            }
            
            // 이완기 혈압 쿼리
            let diastolicQuery = HKSampleQuery(
                sampleType: diastolicType,
                predicate: predicate,
                limit: 1,
                sortDescriptors: [sortDescriptor]
            ) { (_, samples, error) in
                guard error == nil, let diastolicSample = samples?.first as? HKQuantitySample else {
                    completion(nil)
                    return
                }
                
                let systolic = Int(systolicSample.quantity.doubleValue(for: HKUnit.millimeterOfMercury()))
                let diastolic = Int(diastolicSample.quantity.doubleValue(for: HKUnit.millimeterOfMercury()))
                
                completion((systolic: systolic, diastolic: diastolic))
            }
            
            self.healthStore.execute(diastolicQuery)
        }
        
        healthStore.execute(systolicQuery)
    }
    
    // 혈당 데이터 가져오기 (최근)
    func fetchLatestBloodGlucose(completion: @escaping (Double?) -> Void) {
        guard let glucoseType = HKObjectType.quantityType(forIdentifier: .bloodGlucose) else {
            completion(nil)
            return
        }
        
        let predicate = HKQuery.predicateForSamples(withStart: Date().addingTimeInterval(-86400), end: nil)
        let sortDescriptor = NSSortDescriptor(key: HKSampleSortIdentifierEndDate, ascending: false)
        
        let query = HKSampleQuery(
            sampleType: glucoseType,
            predicate: predicate,
            limit: 1,
            sortDescriptors: [sortDescriptor]
        ) { (_, samples, error) in
            guard error == nil, let sample = samples?.first as? HKQuantitySample else {
                completion(nil)
                return
            }
            
            // mg/dL 단위로 변환
            let glucose = sample.quantity.doubleValue(for: HKUnit.gramUnit(with: .milli).unitDivided(by: .literUnit(with: .deci)))
            completion(glucose)
        }
        
        healthStore.execute(query)
    }
    
    // 모든 건강 데이터 한번에 가져오기
    func fetchAllHealthData(completion: @escaping (HealthData?) -> Void) {
        var healthData = HealthData(
            heartRate: 0,
            stepCount: 0,
            sleepHours: 0,
            systolicPressure: 0,
            diastolicPressure: 0,
            bloodGlucose: 0,
            timestamp: Date()
        )
        
        let group = DispatchGroup()
        
        // 심박수 가져오기
        group.enter()
        fetchLatestHeartRate { heartRate in
            if let heartRate = heartRate {
                healthData.heartRate = heartRate
            }
            group.leave()
        }
        
        // 걸음 수 가져오기
        group.enter()
        fetchTodayStepCount { steps in
            if let steps = steps {
                healthData.stepCount = steps
            }
            group.leave()
        }
        
        // 수면 시간 가져오기
        group.enter()
        fetchSleepHours { sleepHours in
            if let sleepHours = sleepHours {
                healthData.sleepHours = sleepHours
            }
            group.leave()
        }
        
        // 혈압 가져오기
        group.enter()
        fetchLatestBloodPressure { bloodPressure in
            if let bloodPressure = bloodPressure {
                healthData.systolicPressure = bloodPressure.systolic
                healthData.diastolicPressure = bloodPressure.diastolic
            }
            group.leave()
        }
        
        // 혈당 가져오기
        group.enter()
        fetchLatestBloodGlucose { glucose in
            if let glucose = glucose {
                healthData.bloodGlucose = glucose
            }
            group.leave()
        }
        
        group.notify(queue: .main) {
            completion(healthData)
        }
    }
    
    // 실시간 심박수 모니터링 시작
    func startHeartRateMonitoring(updateHandler: @escaping (Double) -> Void) {
        guard let heartRateType = HKObjectType.quantityType(forIdentifier: .heartRate) else {
            return
        }
        
        let query = HKObserverQuery(sampleType: heartRateType, predicate: nil) { [weak self] (query, completionHandler, error) in
            if let error = error {
                print("심박수 모니터링 에러: \(error.localizedDescription)")
                completionHandler()
                return
            }
            
            self?.fetchLatestHeartRate { heartRate in
                if let heartRate = heartRate {
                    DispatchQueue.main.async {
                        updateHandler(heartRate)
                    }
                }
                completionHandler()
            }
        }
        
        healthStore.execute(query)
        
        // 백그라운드에서도 업데이트 받기 위한 설정
        healthStore.enableBackgroundDelivery(for: heartRateType, frequency: .immediate) { success, error in
            if let error = error {
                print("백그라운드 업데이트 설정 실패: \(error.localizedDescription)")
            }
        }
    }
}
