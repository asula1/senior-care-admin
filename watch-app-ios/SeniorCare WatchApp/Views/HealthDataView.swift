import SwiftUI

struct HealthDataView: View {
    @EnvironmentObject var healthManager: HealthManager
    
    var body: some View {
        ScrollView {
            VStack(spacing: 15) {
                // 심박수
                HealthMetricCard(
                    title: "심박수",
                    value: "\(healthManager.heartRate, specifier: "%.0f")",
                    unit: "bpm",
                    icon: "heart.fill",
                    color: .red
                )
                
                // 걸음 수
                HealthMetricCard(
                    title: "걸음 수",
                    value: "\(healthManager.stepCount)",
                    unit: "걸음",
                    icon: "figure.walk",
                    color: .green
                )
                
                // 수면 시간
                HealthMetricCard(
                    title: "수면 시간",
                    value: "\(healthManager.sleepHours, specifier: "%.1f")",
                    unit: "시간",
                    icon: "bed.double.fill",
                    color: .purple
                )
                
                // 혈압
                HealthMetricCard(
                    title: "혈압",
                    value: "\(healthManager.systolicPressure)/\(healthManager.diastolicPressure)",
                    unit: "mmHg",
                    icon: "waveform.path.ecg",
                    color: .orange
                )
                
                // 혈당
                HealthMetricCard(
                    title: "혈당",
                    value: "\(healthManager.bloodGlucose, specifier: "%.0f")",
                    unit: "mg/dL",
                    icon: "drop.fill",
                    color: .blue
                )
                
                // 데이터 갱신 버튼
                Button(action: {
                    healthManager.refreshData()
                }) {
                    Text("데이터 갱신")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
                .padding(.top)
            }
            .padding()
        }
        .navigationTitle("건강 데이터")
    }
}

struct HealthMetricCard: View {
    let title: String
    let value: String
    let unit: String
    let icon: String
    let color: Color
    
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Image(systemName: icon)
                    .font(.title3)
                    .foregroundColor(color)
                
                Text(title)
                    .font(.headline)
                    .foregroundColor(.white)
            }
            
            HStack(alignment: .firstTextBaseline) {
                Text(value)
                    .font(.system(size: 32, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
                
                Text(unit)
                    .font(.callout)
                    .foregroundColor(.gray)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(Color.black.opacity(0.2))
        .cornerRadius(10)
    }
}

#Preview {
    HealthDataView()
        .environmentObject(HealthManager())
}
