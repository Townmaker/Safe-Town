# Pleos Compose 구현 진행 기록

## 이어서 시작할 때

Android Studio에서 이 프로젝트를 열고 Codex에 아래 요청을 보냅니다.

> PLEOS_PROGRESS.md를 먼저 읽고, 완료된 코드와 최신 빌드·Pleos 실행 상태를 확인한 뒤 첫 미완료 항목부터 이어서 진행해줘. 이번에 바꾼 것, 확인 결과, 바로 다음 작업을 이 파일에 갱신해줘.

1. Android Studio에서 현재 프로젝트를 엽니다. 실행 기기는 Pleos Connect v2.1.0입니다.
2. 최신 화면은 reference/figma-make/src/의 App.tsx와 index.css를 기준으로 비교합니다. reference/figma-captures/의 01~06 캡처는 이전 버전의 참고 자료입니다.
3. 기능 한 묶음씩 작업하고 .\gradlew.bat :app:assembleDebug로 확인합니다. 앱을 Pleos에서 실행해 작업한 화면의 전환과 창 크기를 확인합니다.
4. 중단하거나 한 묶음을 끝낼 때 완료 여부와 실제 실행·비교 결과, 다음 항목을 아래에 기록합니다.

## 최신 Figma 반영 작업

- 2026-11-14. 첨부한 최신 Vite 내보내기를 기존 참고 소스와 비교했습니다. App.tsx와 index.css가 바뀌었고 두 Three.js 모델 소스는 동일합니다.
- 반영 범위는 Safe Town 표기, 홈 등급 카드, 출발 전 차량 점검 8항목, 주행 헤더와 검색창, 마이페이지 정비 목록·친구 마을 이동, 설정 목록과 공통 하단 메뉴입니다.
- 온보드 Vehicle SDK의 조회 기능과 NaviHelper 정보 조회를 Pleos Connect v2.1.0에서 항목별 검증합니다. 외부 서비스 연결 없이 시연하는 기능은 시연으로 표시합니다.
- 2026-11-14. Pleos에서 8칸 점검, 홈, 주행, 마이페이지에서 친구 방문·복귀, 기록, 설정을 검증했습니다. 가상 배터리 79%와 도어 잠금 해제가 앱에 반영됐고 시험 후 100%와 잠금 상태로 복구했습니다. 에뮬레이터가 값을 제공하지 않은 점검 항목은 확인 불가로 표시됐습니다.
- 2026-11-15. Pleos 누적거리 10→12 km를 주입해 차량 주행거리 2.0 km, 결과 화면, 보상 복귀와 저장 기록을 확인했습니다. 앱의 기존 기록과 XP는 복구했습니다. Virtual Console의 누적거리 감소 제한으로 가상 차량 누적값은 12 km입니다.

## 완료 및 확인 상태

- 마을 홈, 데모 주행, 결과, 주행 기록, 마이페이지, 설정을 Kotlin과 Jetpack Compose로 구현했습니다.
- 차량 상태 팝업, 친구 마을·배지, 목적지 데모·지도 확대, 주행 종료·보상, 기록 선택, 차량 부품 선택, 프로필·설정 및 빠른 실행을 구현했습니다.
- 주행 기록, 누적 XP, 닉네임과 설정을 기기에 저장합니다. 최신 시안의 주행 화면에 전화·메시지·음악·AI 빠른 실행 4개를 표시합니다.
- 원본 Three.js의 마을 12단계와 차량 메시를 Compose Canvas에서 렌더링합니다. 드래그 회전, 핀치 확대, 차량 부품 선택과 굴뚝 연기·구름의 애니메이션을 지원합니다. 원본에서 가져온 형상을 사용하며 그림자, 실시간 조명, 재질은 추가 비교가 필요합니다.
- Pleos Vehicle SDK의 배터리, 운전석 좌측 안전벨트, 승객 도어 잠금 정보를 8칸 점검 화면에 적용했습니다. 브레이크액, 워셔액, 미션오일은 기기의 지원 여부에 따라 조회합니다. 타이어와 엔진오일 경고는 경고등 조회를 사용하며 현재 에뮬레이터에서는 해당 값을 확인할 수 없습니다. 앱은 조회에 실패한 값을 확인 불가로 표시합니다. Android Automotive의 주행 제한이 활성화되면 정차 안내를 표시합니다. [Pleos 차량 SDK 권한](https://document.pleos.ai/docs/pleos-only/connect-sdk-pleos/Vehicle/vehicle-permission)
- Pleos NaviHelper로 출발지 주소와 최근 목적지를 조회합니다. 최근 목적지나 문서 예제 목적지를 기본 내비로 보내고, 반환된 목적지가 일치하면 확인 완료를 표시합니다. 다음 회전 안내는 내비가 설명을 반환할 때 표시합니다. 앱 안 지도·교통규칙 판정·점수·외부 서비스 빠른 실행은 시연용입니다. [NaviHelper 이벤트](https://document.pleos.ai/api-reference/connect-sdk-pleos/NaviHelper/event-listeners)
- 차량 주행거리 차이를 저장하고, 차량 값이 없으면 확인 불가로 표시합니다. 세션 시간은 앱 타이머, 결과 점수와 보상은 시연입니다. 공통 헤더는 기기 날짜와 시각을 표시합니다.
- 상세 토글과 밝기·주야간 선택을 로컬에 저장합니다. 밝기 및 주야간 선택은 시연 설정으로 표시됩니다.
- 모델 생성 스크립트는 reference/tools/export-pleos-models.mjs에 있습니다. 이미 생성된 모델을 앱에 포함하므로 앱 빌드에 Node나 npm이 필요하지 않습니다.

## 검증 기록

- 2026-11-13. 원본 프로젝트 빌드 및 Pleos Connect v2.1.0 설치 성공. 마을, 주행, 결과, 기록, 마이페이지, 설정을 캡처했습니다. 친구 마을을 열고 데모 목적지를 적용해 주행 결과와 기록이 연결되는 흐름을 확인했습니다.
- 2026-11-14. 모델을 추가한 검증본 빌드와 기존 단위 테스트 성공. Pleos 분할 화면에서 출발 전 팝업, 마을 드래그 회전, 차량 부품 선택과 모델 변경을 눈으로 확인했습니다. 팝업 배경의 어두움과 섬 바닥에 물이 겹치는 현상을 보정했습니다. 수정한 마을 그림을 Pleos에서 다시 확인했습니다.
- 2026-11-14. D 드라이브 원본 프로젝트에서 디버그 빌드와 기존 단위 테스트 1개가 통과했습니다. Pleos에서 배터리 100%와 변경값 79%가 표시됐습니다. Drive·25 km/h에서 정차 안내를 확인한 뒤 Park·0 km/h와 기존 100%를 복구했습니다.
- 2026-11-14. 앱에서 42dot을 선택해 Pleos 내비의 경로 미리보기(50m, 1분)와 안내 화면이 열리는 것을 확인했습니다. 앱에 돌아왔을 때 “Pleos 목적지 확인 완료”가 표시됐습니다.
- 2026-11-14. 좁은 폭에서 주행 종료 버튼 공간을 확보하고 설정 하단 여백을 줄였습니다. ADB의 가상 해상도 변경으로는 Pleos 시스템의 앱 창 경계가 새 해상도로 재배치되지 않아, Pleos 창 크기 변경을 통한 추가 검증이 필요합니다. 가상 디스플레이는 2560×1440으로 복구했습니다.

- 2026-11-15. Pleos 재부팅 후 출발지 주소·최근 목적지, 42dot 기본 내비 경로, 앱 복귀 후 경로 확인 및 내비 회전 안내 응답(도착)을 확인했습니다. 원본 프로젝트에서 디버그 빌드 및 기존 단위 테스트가 통과했고 최신 APK를 재설치했습니다. 앱 시각이 에뮬레이터 시각과 일치했습니다. 최신 APK에서 25 km/h 시 정차 안내가 표시됐고 가상 차량을 Park·0 km/h로 복구했습니다. [Pleos Virtual Console](https://document.pleos.ai/docs/pleos-only/app-test-guide/virtual-console-guide)

- 2026-11-15. Android 개발자 오버레이(900×1280)를 Pleos에서 별도로 실행했습니다. AAOS가 가상 디스플레이를 주행 제한 대상으로 처리해 해당 화면에서는 정차 안내가 표시됐습니다. 안전 제한을 유지하고 오버레이를 삭제했습니다. 기본 디스플레이에 420×790dp와 850×420dp Compose 레이아웃을 적용해 주요 화면을 검증합니다. 첫 테스트에서 세로 주행 기록 상세의 중첩 스크롤 오류를 발견했습니다. 세로에서는 하나의 스크롤 영역, 가로에서는 분할 영역별 스크롤을 사용하도록 수정했습니다. 수정 후 Pleos에서 두 레이아웃 테스트가 통과했습니다. 홈 등급과 친구 버튼, 주행 시작·종료 버튼, 목적지 검색, 주행 기록 상세 스크롤, 마이페이지와 설정 이동을 확인했습니다.

- 2026-11-15. Pleos의 실제 최근 목적지인 다이소 성남고등지구점을 선택해 기본 내비 경로, 목적지 확인 완료, 다음 회전 안내 “기업지원허브 방면”이 표시되는 것을 확인했습니다. 원본 프로젝트의 앱·테스트 APK 빌드와 단위 테스트를 완료했습니다.

## 다음 작업

1. Pleos 기본 분할 화면과 기본 디스플레이의 420×790dp·850×420dp 앱 레이아웃을 검증했습니다. 다른 차량의 디스플레이 정책과 실화면은 별도 창에서 FormFactorConsole로 검증합니다. [폼 팩터 테스트 가이드](https://document.pleos.ai/docs/pleos-only/app-test-guide/form-factor-test-guide)
2. 실차를 사용할 수 있을 때 지원 차종의 경고등·오일·워셔액 API 응답과 차종별 영역을 확인합니다. 일반 장소 검색, 신호 통과 판정, 실시간 친구·컬렉션과 외부 서비스는 현재 외부 서비스 없이 실행하는 범위에서 시연으로 유지합니다.

## 관련 소스

- PleosApp.kt는 앱 셸과 하단 탭, 공통 UI입니다.
- VillagePage.kt와 PleosNativeModels.kt는 홈 화면과 3D 모델의 카메라·투영입니다.
- PleosArtwork.kt는 데모 지도와 모델 로딩 실패 시의 대체 아트입니다.
- DrivingPage.kt는 주행 화면과 목적지·빠른 실행입니다.
- PleosUtilityPages.kt는 결과, 기록, 마이페이지, 설정입니다.
- PleosDriveState.kt는 앱 상태, 저장 및 데모 내비게이션입니다.
- PleosVehicleState.kt는 차량 점검 데이터, 누적 주행거리, Android Automotive 주행 제한을 처리합니다.
- PleosNavigationService.kt는 Pleos 내비의 현재 위치와 최근 목적지, 경로 요청·확인, 회전 안내 조회를 처리합니다. 참고 문서는 [NaviHelper 목적지](https://document.pleos.ai/api-reference/connect-sdk-pleos/NaviHelper/destinations)입니다.

## 원본 3D 모델 재생성

원본 모델을 바꿨을 때만 실행합니다. 아래 PowerShell 명령은 프로젝트 루트 기준이며 의존성은 임시 폴더에 설치합니다.

```powershell
$modelTool = Join-Path $env:TEMP ('pleos-model-tool-' + [guid]::NewGuid().ToString('N'))
New-Item -Path $modelTool -ItemType Directory | Out-Null
Copy-Item .\reference\tools\export-pleos-models.mjs -Destination $modelTool
npm.cmd install --prefix $modelTool --ignore-scripts --no-audit --no-fund three@0.187.0 typescript@5.9.3
node (Join-Path $modelTool 'export-pleos-models.mjs') .\reference\figma-make\src .\app\src\main\assets\pleos-models
```
