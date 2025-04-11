import SwiftUI

struct MedicationView: View {
    @State private var medications = [
        MedicationData(id: 1, name: "고혈압약", time: "08:00", taken: true),
        MedicationData(id: 2, name: "당뇨약", time: "08:00", taken: true),
        MedicationData(id: 3, name: "콜레스테롤약", time: "20:00", taken: false),
        MedicationData(id: 4, name: "비타민", time: "08:00", taken: false)
    ]
    
    var body: some View {
        ScrollView {
            VStack(spacing: 15) {
                // 오늘의 복약 현황
                HStack {
                    Text("오늘의 복약 현황")
                        .font(.headline)
                    
                    Spacer()
                    
                    Text("\(medications.filter { $0.taken }.count)/\(medications.count)")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }
                .padding(.horizontal)
                
                // 복약 목록
                ForEach(medications) { medication in
                    MedicationRow(medication: medication) { id in
                        // 복약 상태 토글하는 클로저
                        if let index = medications.firstIndex(where: { $0.id == id }) {
                            medications[index].taken.toggle()
                        }
                    }
                }
                
                Spacer(minLength: 20)
                
                // 복약 알림 설정
                Button(action: {
                    // 복약 알림 설정 액션
                }) {
                    Text("복약 알림 설정")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
            }
            .padding()
        }
        .navigationTitle("복약 관리")
    }
}

struct MedicationRow: View {
    let medication: MedicationData
    let toggleAction: (Int) -> Void
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(medication.name)
                    .font(.headline)
                    .foregroundColor(.white)
                
                Text(medication.time)
                    .font(.subheadline)
                    .foregroundColor(.gray)
            }
            
            Spacer()
            
            Button(action: {
                toggleAction(medication.id)
            }) {
                Image(systemName: medication.taken ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(medication.taken ? .green : .gray)
                    .font(.title2)
            }
        }
        .padding()
        .background(Color.black.opacity(0.2))
        .cornerRadius(10)
    }
}

#Preview {
    MedicationView()
}
