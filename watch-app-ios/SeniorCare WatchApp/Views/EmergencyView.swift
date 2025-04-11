import SwiftUI

struct EmergencyView: View {
    @EnvironmentObject var locationManager: LocationManager
    @State private var isEmergencyActive = false
    @State private var emergencyCountdown = 3
    @State private var timer: Timer? = nil
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                // SOS 버튼
                VStack {
                    Button(action: {
                        if isEmergencyActive {
                            cancelEmergency()
                        } else {
                            startEmergencyCountdown()
                        }
                    }) {
                        ZStack {
                            Circle()
                                .fill(isEmergencyActive ? Color.red : Color.red.opacity(0.2))
                                .frame(width: 150, height: 150)
                            
                            if isEmergencyActive {
                                Text("\(emergencyCountdown)")
                                    .font(.system(size: 60, weight: .bold))
                                    .foregroundColor(.white)
                            } else {
                                Text("SOS")
                                    .font(.system(size: 40, weight: .bold))
                                    .foregroundColor(.white)
                            }
                        }
                    }
                    
                    if isEmergencyActive {
                        Text("취소하려면 다시 터치하세요")
                            .font(.caption)
                            .foregroundColor(.gray)
                            .padding(.top, 8)
                    } else {
                        Text("긴급 상황 시 길게 누르세요")
                            .font(.caption)
                            .foregroundColor(.gray)
                            .padding(.top, 8)
                    }
                }
                .padding(.vertical, 30)
                
                Divider()
                
                // 긴급 연락처
                VStack(alignment: .leading, spacing: 15) {
                    Text("긴급 연락처")
                        .font(.headline)
                        .padding(.horizontal)
                    
                    EmergencyContactRow(name: "보호자", phoneNumber: "010-1234-5678")
                    EmergencyContactRow(name: "담당 의사", phoneNumber: "010-9876-5432")
                    EmergencyContactRow(name: "119 응급센터", phoneNumber: "119")
                }
                
                Spacer(minLength: 40)
                
                // 낙상 감지 기능 토글
                Toggle(isOn: .constant(true)) {
                    Label("낙상 감지 기능", systemImage: "person.fill.checkmark")
                }
                .padding()
                .background(Color.black.opacity(0.2))
                .cornerRadius(10)
            }
            .padding()
        }
        .navigationTitle("비상 연락")
    }
    
    private func startEmergencyCountdown() {
        isEmergencyActive = true
        emergencyCountdown = 3
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            if emergencyCountdown > 1 {
                emergencyCountdown -= 1
            } else {
                sendEmergencyAlert()
                timer?.invalidate()
                timer = nil
            }
        }
    }
    
    private func cancelEmergency() {
        isEmergencyActive = false
        timer?.invalidate()
        timer = nil
    }
    
    private func sendEmergencyAlert() {
        // 위치 정보 확인
        locationManager.requestLocation()
        
        // 여기서 실제 비상 알림 전송 로직을 구현합니다
        // 1. 서버에 SOS 신호 전송
        // 2. 보호자에게 알림 전송
        // 3. 위치 정보 공유
        
        // 실제 앱에서는 비상 연락 후에도 상태를 유지해야 하지만 미리보기를 위해 리셋
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            self.isEmergencyActive = false
        }
    }
}

struct EmergencyContactRow: View {
    let name: String
    let phoneNumber: String
    
    var body: some View {
        Button(action: {
            // 전화 걸기 액션 (실제 앱에서는 이 부분에 전화 걸기 코드 구현)
            if let url = URL(string: "tel://\(phoneNumber.filter { "0123456789".contains($0) })") {
                UIApplication.shared.open(url)
            }
        }) {
            HStack {
                VStack(alignment: .leading) {
                    Text(name)
                        .font(.headline)
                        .foregroundColor(.white)
                    
                    Text(phoneNumber)
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
                
                Spacer()
                
                Image(systemName: "phone.fill")
                    .foregroundColor(.green)
            }
            .padding()
            .background(Color.black.opacity(0.2))
            .cornerRadius(10)
        }
    }
}

#Preview {
    EmergencyView()
        .environmentObject(LocationManager())
}
