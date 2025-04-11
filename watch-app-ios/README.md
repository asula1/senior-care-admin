# Apple watchOS 애플리케이션

이 디렉토리는 노인 케어 시스템의 Apple Watch(watchOS) 버전 앱을 포함합니다.

## 기능

- 낙상 감지: 가속도 센서를 활용한 자동 감지
- 비정상 심박수 알림: 심박수 센서 데이터 분석
- SOS 긴급 호출: 간단한 버튼 조작으로 긴급 연락
- 위치 추적: GPS를 통한 실시간 위치 공유
- 건강 모니터링: 활동량, 수면, 심박수 등 추적
- 복약 알림 및 관리
- 간단한 메시징 기능

## 개발 환경 설정

1. Xcode 15.0 이상 필요
2. SwiftUI 기반 개발
3. HealthKit, CoreLocation, CoreMotion 프레임워크 사용
4. WatchKit 및 WatchConnectivity 프레임워크 활용

## 앱 구조

```
watch-app-ios/
├── SeniorCare/               # iOS 컴패니언 앱
├── SeniorCare WatchApp/      # watchOS 앱
│   ├── Views/                # SwiftUI 뷰 파일
│   ├── Models/               # 데이터 모델
│   ├── Services/             # 네트워크 및 기타 서비스
│   └── Managers/             # 센서 및 기능 관리자
└── Shared/                   # 공유 코드 및 자원
```

## 빌드 및 실행

1. Xcode에서 프로젝트 열기
2. 시뮬레이터 또는 실제 기기 선택
3. Run 버튼 클릭하여 실행
