import SwiftUI

struct ContentView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Image(systemName: "heart.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 100, height: 100)
                    .foregroundColor(.red)
                
                Text("노인 케어 앱")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                
                Text("Apple Watch를 통해 건강 데이터를 모니터링하고 관리합니다.")
                    .font(.headline)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                
                Spacer().frame(height: 40)
                
                Text("이 앱은 Apple Watch와 페어링하여 사용됩니다.\n워치를 확인하여 기능을 이용하세요.")
                    .multilineTextAlignment(.center)
                    .padding()
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(10)
                
                Spacer()
                
                // 앱 정보
                Text("노인 케어 시스템")
                    .font(.caption)
                    .foregroundColor(.gray)
                Text("버전 1.0")
                    .font(.caption)
                    .foregroundColor(.gray)
            }
            .padding()
            .navigationTitle("노인 케어")
        }
    }
}

#Preview {
    ContentView()
}
