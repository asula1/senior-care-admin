# Apple Watch 에뮬레이터 설정 및 실행 가이드

이 문서는 Xcode에서 Apple Watch 시뮬레이터를 설정하고 노인 케어 앱을 실행하는 방법을 안내합니다.

## 사전 요구사항

- Xcode 15 이상
- macOS Ventura(13.0) 이상
- 프로젝트가 올바르게 설정됨

## 시뮬레이터 설정 및 앱 실행 단계

### 1. Xcode 프로젝트 열기

```bash
# 터미널에서 프로젝트 폴더로 이동
cd /Users/zzambab98/Documents/senior-care-watch/watch-app-ios

# Xcode에서 프로젝트 파일 열기
open SeniorCare.xcodeproj
```

혹은 Finder에서 프로젝트 폴더로 이동하여 `SeniorCare.xcodeproj` 파일을 더블클릭합니다.

### 2. 시뮬레이터 선택

1. Xcode 상단의 스키마 선택 드롭다운 메뉴에서 "SeniorCare Watch App"을 선택합니다.
2. 타겟 디바이스로 "iPhone + Apple Watch" 시뮬레이터 페어를 선택합니다.
   - 예: "iPhone 15 + Apple Watch Series 9 (46mm)"

### 3. 앱 실행하기

1. Xcode 상단의 "Run" 버튼(▶️)을 클릭하거나 `Cmd + R` 단축키를 누릅니다.
2. iPhone 시뮬레이터와 Apple Watch 시뮬레이터가 모두 실행됩니다.
3. Watch 시뮬레이터에서 "노인 케어" 앱을 찾아 실행합니다.

### 4. 시뮬레이터 기능 테스트

#### 심박수 등 건강 데이터 모의 제공하기

1. Watch 시뮬레이터 메뉴에서 "Features" > "Health" > "Heart Rate" 선택
2. 심박수 값을 설정하여 앱에서 심박수 데이터가 업데이트되는지 확인

#### 낙상 감지 테스트하기

1. Watch 시뮬레이터 메뉴에서 "Features" > "CMMotionManager" 선택
2. 가속도계(Accelerometer) 탭으로 이동
3. "Preset" 드롭다운에서 "Bump"를 선택하거나 X, Y, Z 값을 크게 변경하여 낙상을 시뮬레이션

#### 위치 정보 테스트하기

1. 시뮬레이터 메뉴에서 "Features" > "Location" 선택
2. 사전 정의된 위치 중 하나를 선택하거나 커스텀 위치 좌표 설정

### 5. 디버깅

1. Xcode 콘솔에서 로그 및 오류 메시지 확인
2. 필요한 경우 브레이크포인트를 설정하여 코드 실행 흐름 추적
3. 메모리 및 CPU 사용량을 모니터링하려면 Xcode의 Debug Navigator 사용

## 주의사항

- HealthKit 데이터는 시뮬레이터에서 실제 기기처럼 완전히 작동하지 않을 수 있습니다.
- 일부 센서 데이터(심박수, 가속도계 등)는 시뮬레이터에서 모의 값을 설정해야 제대로 테스트할 수 있습니다.
- 앱이 백그라운드에서도 제대로 작동하는지 테스트하려면 시뮬레이터에서 Home 버튼을 눌러 앱을 백그라운드로 보내고 일정 시간 후에 다시 확인합니다.

## 실제 기기에서 테스트

실제 기기에서 테스트하려면:

1. Apple 개발자 계정이 필요합니다.
2. Xcode에서 프로젝트의 Signing & Capabilities 설정에 Team 정보를 추가해야 합니다.
3. iPhone과 Apple Watch 기기를 Mac에 연결합니다.
4. 런 타겟으로 실제 기기를 선택하고 앱을 실행합니다.
