import Foundation
import UserNotifications
import WatchKit

class NotificationManager: NSObject, ObservableObject, UNUserNotificationCenterDelegate {
    static let shared = NotificationManager()
    
    // 발행할 상태 값
    @Published var isAuthorized: Bool = false
    @Published var pendingNotifications: [UNNotificationRequest] = []
    
    private let notificationCenter = UNUserNotificationCenter.current()
    
    override init() {
        super.init()
        notificationCenter.delegate = self
        checkAuthorizationStatus()
    }
    
    // 알림 권한 요청
    func requestPermissions() {
        notificationCenter.requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            DispatchQueue.main.async {
                self.isAuthorized = granted
                if let error = error {
                    print("알림 권한 요청 오류: \(error.localizedDescription)")
                }
            }
        }
    }
    
    // 현재 권한 상태 확인
    private func checkAuthorizationStatus() {
        notificationCenter.getNotificationSettings { settings in
            DispatchQueue.main.async {
                self.isAuthorized = settings.authorizationStatus == .authorized
            }
        }
    }
    
    // 로컬 알림 전송
    func sendLocalNotification(title: String, body: String, delay: TimeInterval = 0) {
        guard isAuthorized else {
            print("알림 권한이 없습니다.")
            return
        }
        
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = UNNotificationSound.default
        
        // 필요한 경우 진동 패턴 설정
        WKInterfaceDevice.current().play(.notification)
        
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: max(0.1, delay), repeats: false)
        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)
        
        notificationCenter.add(request) { error in
            if let error = error {
                print("알림 예약 실패: \(error.localizedDescription)")
            }
        }
    }
    
    // 복약 알림 예약
    func scheduleMedicationReminder(medicationName: String, time: String, identifier: String) {
        guard isAuthorized else {
            print("알림 권한이 없습니다.")
            return
        }
        
        // 시간 문자열 파싱 (HH:mm 형식)
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "HH:mm"
        
        guard let reminderDate = dateFormatter.date(from: time) else {
            print("잘못된 시간 형식입니다: \(time)")
            return
        }
        
        // 현재 날짜의 해당 시간으로 설정
        let calendar = Calendar.current
        let now = Date()
        
        var dateComponents = calendar.dateComponents([.year, .month, .day], from: now)
        let timeComponents = calendar.dateComponents([.hour, .minute], from: reminderDate)
        
        dateComponents.hour = timeComponents.hour
        dateComponents.minute = timeComponents.minute
        dateComponents.second = 0
        
        // 이미 지난 시간이면 다음 날로 설정
        if let scheduledDate = calendar.date(from: dateComponents), scheduledDate < now {
            dateComponents.day! += 1
        }
        
        // 알림 콘텐츠 설정
        let content = UNMutableNotificationContent()
        content.title = "복약 알림"
        content.body = "\(medicationName) 복용 시간입니다."
        content.sound = UNNotificationSound.default
        content.categoryIdentifier = "MEDICATION"
        
        // 지정된 시간에 알림 트리거
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: "medication_\(identifier)", content: content, trigger: trigger)
        
        notificationCenter.add(request) { error in
            if let error = error {
                print("복약 알림 예약 실패: \(error.localizedDescription)")
            }
        }
    }
    
    // 예약된 알림 목록 가져오기
    func fetchPendingNotifications() {
        notificationCenter.getPendingNotificationRequests { requests in
            DispatchQueue.main.async {
                self.pendingNotifications = requests
            }
        }
    }
    
    // 특정 알림 취소
    func cancelNotification(identifier: String) {
        notificationCenter.removePendingNotificationRequests(withIdentifiers: [identifier])
        fetchPendingNotifications()
    }
    
    // 모든 알림 취소
    func cancelAllNotifications() {
        notificationCenter.removeAllPendingNotificationRequests()
        pendingNotifications = []
    }
    
    // UNUserNotificationCenterDelegate 메서드
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        // 앱이 포그라운드에 있을 때도 알림 표시
        completionHandler([.banner, .sound])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // 알림 응답 처리
        let identifier = response.notification.request.identifier
        let userInfo = response.notification.request.content.userInfo
        
        // 복약 알림인 경우 처리
        if identifier.starts(with: "medication_"), let medicationId = identifier.split(separator: "_").last {
            if response.actionIdentifier == UNNotificationDefaultActionIdentifier {
                // 알림 탭 - 해당 화면으로 이동
                NotificationCenter.default.post(name: Notification.Name("OpenMedicationScreen"), object: nil)
            } else if response.actionIdentifier == "MARK_AS_TAKEN" {
                // 복용 완료 액션
                if let id = Int(medicationId) {
                    NotificationCenter.default.post(name: Notification.Name("MedicationTaken"), object: nil, userInfo: ["medicationId": id])
                }
            }
        }
        
        completionHandler()
    }
}
