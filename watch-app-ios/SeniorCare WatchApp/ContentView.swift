import SwiftUI

struct ContentView: View {
    @State private var selection: Tab = .home
    
    enum Tab {
        case home, health, medication, emergency
    }
    
    var body: some View {
        TabView(selection: $selection) {
            HomeView()
                .tag(Tab.home)
            
            HealthDataView()
                .tag(Tab.health)
            
            MedicationView()
                .tag(Tab.medication)
            
            EmergencyView()
                .tag(Tab.emergency)
        }
        .navigationBarBackButtonHidden(true)
        .tabViewStyle(PageTabViewStyle())
    }
}

#Preview {
    ContentView()
        .environmentObject(HealthManager())
        .environmentObject(LocationManager())
        .environmentObject(FallDetectionManager())
        .environmentObject(NotificationManager())
}
