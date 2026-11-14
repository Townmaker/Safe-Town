# Pleos 마을 앱 포팅 시작하기

이 폴더의 파일은 피그마에서 내보낸 웹 프로토타입의 참고 자료입니다. 앱 코드는 프로젝트의 app 모듈에 있습니다.

- src/App.tsx에서 VillageScreen, DrivingScreen, DriveResult와 페이지 이동을 확인합니다.
- src/index.css에서 화면 배치, 색상, 크기와 반응형 규칙을 확인합니다.
- src/ThreeVillage.tsx와 src/ThreeVehicle.tsx에는 Three.js 장면이 정의되어 있습니다.

## Android Studio의 Codex에 보낼 첫 요청

```text
reference/figma-make/src/App.tsx의 VillageScreen과 src/index.css의 관련 스타일을 읽고, 기존 app 모듈에 Pleos용 마을 홈을 Kotlin과 Jetpack Compose로 만들어줘. 기존 MainActivity에서 새 홈을 열고, 주행 시작을 누르면 간단한 주행 화면으로 넘어가게 해줘. 첫 버전은 마을 영역을 2D 임시 화면으로 구현하고 차량 정보는 데모 데이터로 표시해줘. Pleos 에뮬레이터의 가로 화면을 기준으로 만들고, 좁은 앱 창에서도 버튼과 주요 텍스트를 볼 수 있게 해줘. 현재 앱의 패키지와 최소 SDK 설정을 사용하고, 마지막에 gradlew.bat :app:assembleDebug로 빌드를 확인해줘.
```
