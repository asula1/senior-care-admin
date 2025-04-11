import SwiftUI

struct HomeView: View {
    @EnvironmentObject var healthManager: HealthManager
    @State private var currentTime = Date()
    
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        ScrollView {
            VStack(spacing: 15) {
                // 현재 시간 표시
                Text(timeString())
                    .font(.system(size: 36, weight: .semibold, design: .rounded))
                    .foregroundColor(.white)
                    .onReceive(timer) { _ in
                        currentTime = Date()
                    }
                
                Divider()
                
                // 건강 상태 요약
                HStack {
                    VStack(alignment: .leading) {
                        Label("\(healthManager.heartRate, specifier: "%.0f")", systemImage: "heart.fill")
                            .foregroundColor(.red)
                        Text("심박수")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .trailing) {
                        Label("\(healthManager.stepCount)", systemImage: "figure.walk")
                            .foregroundColor(.green)
                        Text("오늘 걸음수")
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
                .padding(.horizontal)
                
                Divider()
                
                // 메뉴 버튼
                NavigationLink(destination: HealthDataView()) {
                    MenuButton(title: "건강 데이터", icon: "heart.text.square.fill", color: .blue)
                }
                
                NavigationLink(destination: MedicationView()) {
                    MenuButton(title: "복약 관리", icon: "pills.fill", color: .orange)
                }
                
                NavigationLink(destination: EmergencyView()) {
                    MenuButton(title: "비상 연락", icon: "exclamationmark.triangle.fill", color: .red)
                }
            }
            .padding()
        }
        .navigationTitle("노인 케어")
    }
    
    private func timeString() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "HH:mm"
        return formatter.string(from: currentTime)
    }
}

struct MenuButton: View {
    let title: String
    let icon: String
    let color: Color
    
    var body: some View {
        HStack {
            Image(systemName: icon)
                .font(.title3)
                .foregroundColor(color)
            
            Text(title)
                .font(.body)
                .foregroundColor(.white)
            
            Spacer()
            
            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.gray)
        }
        .padding()
        .background(Color.black.opacity(0.2))
        .cornerRadius(10)
    }
}

#Preview {
    HomeView()
        .environmentObject(HealthManager())
}
