# Townmaker · Safe Town

Pleos Connect용 안전운전 마을 앱입니다. Kotlin과 Jetpack Compose로 차량용 화면을 구성합니다.

## 처음 실행하기

1. GitHub에서 이 저장소를 클론하고, Android Studio에서 `settings.gradle.kts`가 있는 루트 폴더를 엽니다.
2. SDK Manager에서 Android SDK Platform 37과 Android SDK Platform-Tools, Android Emulator를 설치합니다. 저장소의 Gradle Wrapper와 Gradle JVM 설정을 사용합니다. 현재 Gradle 데몬 설정은 Java 25입니다.
3. Gradle 동기화를 완료합니다. Android Studio가 각 PC의 SDK 경로를 `local.properties`에 생성합니다.
4. Pleos Connect v2.1.0 가상 기기(Android 14, API 34)를 만든 뒤 Android Studio에서 실행 대상을 선택하고 `app`을 실행합니다. API 37은 빌드에 쓰이는 SDK 버전입니다. [Pleos 에뮬레이터 설정](https://document.pleos.ai/docs/connect/guide/getting-started/application-development/setup-connect-sdk-emulator)
5. 변경 후 아래 명령으로 빌드와 단위 테스트를 확인합니다.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

macOS 또는 Linux에서는 `./gradlew :app:assembleDebug :app:testDebugUnitTest`를 실행합니다. Pleos 런타임 검증은 Windows의 Pleos Connect v2.1.0에서 진행했습니다. 현재 빌드와 시연에는 별도의 외부 API 키가 필요하지 않습니다.

## 팀 작업

- 최신 `main`으로 시작하고, 화면이나 기능 단위로 브랜치를 만든 뒤 Pull Request를 엽니다. 예를 들어 홈 UI와 장소 검색은 각각 `ui/village-home`, `feature/place-search`에서 진행할 수 있습니다.
- 디자인 변경에는 Figma 프레임 링크나 캡처, 변경할 화면과 버튼 동작을 함께 남깁니다. 기존 비교 자료는 [reference/figma-captures](reference/figma-captures)에 있습니다.
- 기능 변경에는 입력·출력, 로딩·오류 화면, 시연 데이터로 확인하는 방법을 기록합니다. 지도·장소 검색을 바꿀 때는 Compose 화면과 `NavigationService` 인터페이스를 연결 지점으로 씁니다.
- API를 연결할 때는 제공사, 공식 문서, 사용 기능, 테스트 환경을 먼저 정합니다. 서버용 비밀 키는 팀의 비밀 저장소를 통해 서버 환경 변수로 구성합니다. 모바일 SDK 전용 키는 제공사가 지원하는 앱 제한을 적용합니다. 저장소에는 설정 변수 이름과 예시 값만 공유합니다.
- 작업을 마치거나 중단할 때 Pull Request와 [PLEOS_PROGRESS.md](PLEOS_PROGRESS.md)에 변경 사항, 테스트 결과, 다음 할 일을 업데이트합니다.

## 현재 동작

Pleos 에뮬레이터에서 배터리·안전벨트·도어 잠금 표시, 주행 제한, 내비의 현재 위치와 최근 목적지 조회, `42dot` 목적지 전달을 확인했습니다. 앱의 지도와 주행 점수는 시연용입니다. 나머지 연동과 화면 검증 현황은 [PLEOS_PROGRESS.md](PLEOS_PROGRESS.md)에 있습니다.
