import Foundation
import CoreLocation
import Combine

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    
    // 발행할 상태 값
    @Published var lastLocation: CLLocation?
    @Published var isAuthorized: Bool = false
    @Published var errorMessage: String?
    
    override init() {
        super.init()
        
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
    }
    
    // 위치 정보 요청
    func requestLocation() {
        locationManager.requestLocation()
    }
    
    // 위치 정보 모니터링 시작
    func startLocationUpdates() {
        locationManager.startUpdatingLocation()
    }
    
    // 위치 정보 모니터링 중지
    func stopLocationUpdates() {
        locationManager.stopUpdatingLocation()
    }
    
    // 위치 정보를 문자열로 변환
    func locationString() -> String {
        guard let location = lastLocation else {
            return "위치 정보 없음"
        }
        
        // 위치 정보 문자열로 반환 (위도, 경도)
        return String(format: "위도: %.6f, 경도: %.6f", location.coordinate.latitude, location.coordinate.longitude)
    }
    
    // CLLocationManagerDelegate 메서드
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        
        lastLocation = location
        errorMessage = nil
    }
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        errorMessage = error.localizedDescription
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            isAuthorized = true
            locationManager.requestLocation()
        case .denied, .restricted:
            isAuthorized = false
            errorMessage = "위치 정보 접근이 거부되었습니다. 설정에서 권한을 허용해주세요."
        case .notDetermined:
            isAuthorized = false
        @unknown default:
            isAuthorized = false
        }
    }
}
