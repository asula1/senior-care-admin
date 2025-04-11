import Foundation

class ApiService {
    private let baseURL = "https://seniorcare-api.example.com/api"
    
    // JSON 디코딩 헬퍼
    private let decoder: JSONDecoder = {
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601
        return decoder
    }()
    
    // JSON 인코딩 헬퍼
    private let encoder: JSONEncoder = {
        let encoder = JSONEncoder()
        encoder.dateEncodingStrategy = .iso8601
        return encoder
    }()
    
    // 건강 데이터 업로드
    func uploadHealthData(_ healthData: HealthData, completion: @escaping (Result<Bool, Error>) -> Void) {
        guard let url = URL(string: "\(baseURL)/health-data") else {
            completion(.failure(ApiError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try encoder.encode(healthData)
        } catch {
            completion(.failure(error))
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                completion(.failure(ApiError.invalidResponse))
                return
            }
            
            if (200...299).contains(httpResponse.statusCode) {
                completion(.success(true))
            } else {
                completion(.failure(ApiError.serverError(httpResponse.statusCode)))
            }
        }.resume()
    }
    
    // 비상 알림 전송
    func sendEmergencyAlert(location: (latitude: Double, longitude: Double)?, completion: @escaping (Result<Bool, Error>) -> Void) {
        guard let url = URL(string: "\(baseURL)/emergency") else {
            completion(.failure(ApiError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        var payload: [String: Any] = [
            "timestamp": ISO8601DateFormatter().string(from: Date()),
            "type": "SOS"
        ]
        
        if let location = location {
            payload["location"] = [
                "latitude": location.latitude,
                "longitude": location.longitude
            ]
        }
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: payload)
        } catch {
            completion(.failure(error))
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                completion(.failure(ApiError.invalidResponse))
                return
            }
            
            if (200...299).contains(httpResponse.statusCode) {
                completion(.success(true))
            } else {
                completion(.failure(ApiError.serverError(httpResponse.statusCode)))
            }
        }.resume()
    }
    
    // 복약 정보 업데이트
    func updateMedicationStatus(medicationId: Int, taken: Bool, completion: @escaping (Result<Bool, Error>) -> Void) {
        guard let url = URL(string: "\(baseURL)/medications/\(medicationId)") else {
            completion(.failure(ApiError.invalidURL))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "PATCH"
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        
        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: ["taken": taken])
        } catch {
            completion(.failure(error))
            return
        }
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                completion(.failure(error))
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse else {
                completion(.failure(ApiError.invalidResponse))
                return
            }
            
            if (200...299).contains(httpResponse.statusCode) {
                completion(.success(true))
            } else {
                completion(.failure(ApiError.serverError(httpResponse.statusCode)))
            }
        }.resume()
    }
}

// API 에러 정의
enum ApiError: Error {
    case invalidURL
    case invalidResponse
    case serverError(Int)
    case decodingError
}
