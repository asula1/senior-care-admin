import Foundation

struct MedicationData: Identifiable {
    var id: Int
    var name: String
    var time: String
    var taken: Bool
    var notes: String = ""
    
    static var samples: [MedicationData] {
        [
            MedicationData(id: 1, name: "고혈압약", time: "08:00", taken: true),
            MedicationData(id: 2, name: "당뇨약", time: "08:00", taken: true),
            MedicationData(id: 3, name: "콜레스테롤약", time: "20:00", taken: false),
            MedicationData(id: 4, name: "비타민", time: "08:00", taken: false)
        ]
    }
}
