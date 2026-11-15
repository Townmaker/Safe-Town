# Safe Town

**출발 전 차량을 점검하고, 주행을 마치면 기록과 함께 마을이 성장하는 Pleos Connect용 안전운전 앱입니다.**

Safe Town은 출발 전 점검부터 목적지 선택과 주행 기록까지 차량 화면에서 이어지도록 설계했습니다. Figma 시안을 Kotlin과 Jetpack Compose로 구현했으며, Pleos SDK를 통해 차량 상태와 기본 내비게이션을 앱에 연결했습니다.

## 주요 기능

1. **출발 전 차량 점검** — 배터리 잔량, 운전석 안전벨트, 도어 잠금, 브레이크액, 워셔액, 변속기 오일, 타이어 경고, 엔진오일 경고를 8개 항목으로 표시합니다. 차량에서 값을 받지 못하면 해당 항목에 ‘확인 불가’를 표시합니다.
2. **기본 내비게이션 연동** — 현재 위치의 주소와 최근 목적지를 불러옵니다. 선택한 목적지를 Pleos 기본 내비게이션에 전달하고, 목적지 설정 여부와 다음 회전 안내를 앱에서 확인할 수 있습니다.
3. **주행 결과와 마을 성장** — 주행 전후의 차량 누적거리 차이로 이번 이동 거리를 계산하고, 주행 시간과 함께 기기에 저장합니다. 결과 화면의 경험치 보상으로 마을이 성장하는 흐름을 체험할 수 있습니다.
4. **차량 화면의 주행 안전** — Android Automotive의 `CarUxRestrictionsManager`로 주행 중 조작 제한을 감지하고, 제한이 활성화되면 정차 안내를 표시합니다.

## Pleos SDK 활용

| SDK | 주요 API | 활용 방식 |
| --- | --- | --- |
| **Vehicle SDK 2.3.2** | `getEvBatteryStateOfCharge`, `getCurrentSeatBeltBuckledState`, `getDoorLockState`, `getTelltalesInfo`, `getCurrentDistanceDriven` | 차량 상태를 출발 전 점검 화면에 반영하고 차량 누적거리로 주행 기록을 계산합니다. [차량 연동 코드](app/src/main/java/com/example/hyundai_app/PleosVehicleState.kt) |
| **NaviHelper SDK 3.0.5** | `getCurrentLocationInfo`, `getRecentDestinationInfo`, `requestRoute`, `getDestinationInfo`, `getTBTInfo` | 출발지 주소와 최근 목적지를 조회하고, 기본 내비에 경로를 요청한 뒤 목적지 반영 여부와 다음 회전 안내를 확인합니다. [내비 연동 코드](app/src/main/java/com/example/hyundai_app/PleosNavigationService.kt) |

## 시연 및 검증 범위

Windows의 **Pleos Connect v2.1.0 에뮬레이터**에서 다음 동작을 확인했습니다.

- 가상 차량의 배터리 잔량과 도어 잠금 상태를 변경하면 출발 전 점검 화면에 반영됩니다.
- 누적거리를 10 km에서 12 km로 변경하면 주행 결과와 기록에 2.0 km가 표시됩니다.
- 최근 목적지를 선택하면 Pleos 기본 내비게이션에서 경로가 열리고, 앱에서 목적지 반영 여부와 다음 회전 안내를 볼 수 있습니다.
- 주행 중 조작 제한이 적용되면 정차 안내가 표시됩니다.

앱 내부 지도, 교통규칙 판정, 안전운전 점수와 마을 보상은 시연용입니다. 경고등과 오일·워셔액 관련 응답은 지원 차량에서 추가 검증할 예정입니다. [상세 검증 기록](PLEOS_PROGRESS.md)

## 실행 방법

1. 저장소를 클론하고 Android Studio에서 `settings.gradle.kts`가 있는 루트 폴더를 엽니다.
2. SDK Manager에서 **Android SDK Platform 37**, Android SDK Platform-Tools, Android Emulator를 설치하고 Gradle 동기화를 실행합니다. 프로젝트의 Gradle 데몬은 **Java 25**를 사용합니다.
3. **Pleos Connect v2.1.0** 가상 기기(Android 14, API 34)를 만든 뒤 Android Studio에서 해당 기기를 선택하고 `app`을 실행합니다. [Pleos 에뮬레이터 설정 가이드](https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator)

빌드와 시연에 별도의 외부 API 키는 필요하지 않습니다. Windows에서 디버그 APK를 빌드하고 단위 테스트를 실행하려면 아래 명령을 사용합니다.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

macOS 또는 Linux에서는 `./gradlew :app:assembleDebug :app:testDebugUnitTest`를 사용합니다.
