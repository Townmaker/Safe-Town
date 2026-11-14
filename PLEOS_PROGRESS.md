# Pleos Compose 구현 진행 기록

## 이어서 시작할 때

Android Studio에서 이 프로젝트를 열고 Codex에 아래 요청을 보냅니다.

> PLEOS_PROGRESS.md를 먼저 읽고, 완료된 코드와 최신 빌드·Pleos 실행 상태를 확인한 뒤 첫 미완료 항목부터 이어서 진행해줘. 이번에 바꾼 것, 확인 결과, 바로 다음 작업을 이 파일에 갱신해줘.

1. Android Studio에서 현재 프로젝트를 엽니다. 실행 기기는 Pleos Connect v2.1.0입니다.
2. reference/figma-captures/의 01~06 캡처와 reference/figma-make/src/의 화면·스타일·Three.js 소스를 확인합니다. 캡처의 검은 디스플레이 프레임 바깥은 비교 대상에서 뺍니다.
3. 기능 한 묶음씩 작업하고 .\gradlew.bat :app:assembleDebug로 확인합니다. 앱을 Pleos에서 실행해 작업한 화면의 전환과 창 크기를 확인합니다.
4. 중단하거나 한 묶음을 끝낼 때 완료 여부와 실제 실행·비교 결과, 다음 항목을 아래에 기록합니다.

## 완료 및 확인 상태

- 마을 홈, 데모 주행, 결과, 주행 기록, 마이페이지, 설정을 Kotlin과 Jetpack Compose로 구현했습니다.
- 차량 상태 팝업, 친구 마을·배지, 목적지 데모·지도 확대, 주행 종료·보상, 기록 선택, 차량 부품 선택, 프로필·설정 및 빠른 실행을 구현했습니다.
- 주행 기록, 누적 XP, 닉네임, 기본 토글, 빠른 실행 목록을 기기에 저장합니다.
- 원본 Three.js의 마을 12단계와 차량 메시를 Compose Canvas에서 렌더링합니다. 드래그 회전, 핀치 확대, 차량 부품 선택과 굴뚝 연기·구름의 애니메이션을 지원합니다. 원본에서 가져온 형상을 사용하며 그림자, 실시간 조명, 재질은 추가 비교가 필요합니다.
- Pleos Vehicle SDK로 배터리 충전량을 읽어 홈 차량 상태 팝업, 주행 화면, 마이페이지에 표시합니다. 나머지 차량 점검 값은 시연 데이터입니다. Android Automotive가 주행 제한을 알리면 앱은 정차 안내를 표시합니다.
- Pleos NaviHelper로 문서의 예제 목적지 42dot을 기본 내비에 전달합니다. 목적지 정보가 일치할 때 앱에 확인 완료를 표시합니다. 콜백 확인이 늦거나 지원되지 않으면 Pleos 내비에서 확인하도록 표시합니다. 앱 안 지도, 교통규칙 예고, 주행 기록과 점수는 시연용입니다.
- 상세 토글과 밝기·주야간 선택을 로컬에 저장합니다. 밝기 및 주야간 선택은 시연 설정으로 표시됩니다.
- 모델 생성 스크립트는 reference/tools/export-pleos-models.mjs에 있습니다. 이미 생성된 모델을 앱에 포함하므로 앱 빌드에 Node나 npm이 필요하지 않습니다.

## 검증 기록

- 2026-11-13. 원본 프로젝트 빌드 및 Pleos Connect v2.1.0 설치 성공. 마을, 주행, 결과, 기록, 마이페이지, 설정을 캡처했습니다. 친구 마을을 열고 데모 목적지를 적용해 주행 결과와 기록이 연결되는 흐름을 확인했습니다.
- 2026-11-14. 모델을 추가한 검증본 빌드와 기존 단위 테스트 성공. Pleos 분할 화면에서 출발 전 팝업, 마을 드래그 회전, 차량 부품 선택과 모델 변경을 눈으로 확인했습니다. 팝업 배경의 어두움과 섬 바닥에 물이 겹치는 현상을 보정했습니다. 수정한 마을 그림을 Pleos에서 다시 확인했습니다.
- 2026-11-14. D 드라이브 원본 프로젝트에서 디버그 빌드와 기존 단위 테스트 1개가 통과했습니다. Pleos에서 배터리 100%와 변경값 79%가 표시됐습니다. Drive·25 km/h에서 정차 안내를 확인한 뒤 Park·0 km/h와 기존 100%를 복구했습니다.
- 2026-11-14. 앱에서 42dot을 선택해 Pleos 내비의 경로 미리보기(50m, 1분)와 안내 화면이 열리는 것을 확인했습니다. 앱에 돌아왔을 때 “Pleos 목적지 확인 완료”가 표시됐습니다.
- 2026-11-14. 좁은 폭에서 주행 종료 버튼 공간을 확보하고 설정 하단 여백을 줄였습니다. ADB의 가상 해상도 변경으로는 Pleos 시스템의 앱 창 경계가 새 해상도로 재배치되지 않아, Pleos 창 크기 변경을 통한 추가 검증이 필요합니다. 가상 디스플레이는 2560×1440으로 복구했습니다.

## 다음 작업

1. 일반 장소 검색과 저장된 집·최근 목적지의 데이터 소스를 연결합니다. 내비 도착 예정 시간과 거리의 단위, SDK 응답 이벤트를 확인하고 실제 경로에 맞춰 표시합니다.
2. 타이어, 안전벨트, 도어 등 실제 지원되는 차량 항목을 개별 검증합니다. 앱에서 주행 기록과 보상 점수에 사용할 신호를 정의하고 가상 차량에서 재현 가능한 흐름을 준비합니다.
3. Figma 홈과 차량의 그림자·조명, 카메라 구도와 투명 재질을 비교하고 보정합니다. 12단계 마을, 핀치, 차량 부품 그룹별 선택 및 다른 창 크기의 화면을 확인합니다.
4. 친구·컬렉션, 지도 다운로드, 전화·메시지·음악·AI의 외부 연동을 구현합니다. 외부 AI처럼 비밀 키를 사용하는 기능은 서버로 연결합니다.

## 관련 소스

- PleosApp.kt는 앱 셸과 하단 탭, 공통 UI입니다.
- VillagePage.kt와 PleosNativeModels.kt는 홈 화면과 3D 모델의 카메라·투영입니다.
- PleosArtwork.kt는 데모 지도와 모델 로딩 실패 시의 대체 아트입니다.
- DrivingPage.kt는 주행 화면과 목적지·빠른 실행입니다.
- PleosUtilityPages.kt는 결과, 기록, 마이페이지, 설정입니다.
- PleosDriveState.kt는 앱 상태, 저장 및 데모 내비게이션입니다.
- PleosVehicleState.kt는 Pleos 배터리 읽기와 Android Automotive 주행 제한을 처리합니다.
- PleosNavigationService.kt는 Pleos 내비에 42dot 경로를 요청하고 목적지 확인을 처리합니다. 참고 문서는 [NaviHelper 목적지](https://document.pleos.ai/api-reference/connect-sdk-pleos/NaviHelper/destinations)입니다.

## 원본 3D 모델 재생성

원본 모델을 바꿨을 때만 실행합니다. 아래 PowerShell 명령은 프로젝트 루트 기준이며 의존성은 임시 폴더에 설치합니다.

```powershell
$modelTool = Join-Path $env:TEMP ('pleos-model-tool-' + [guid]::NewGuid().ToString('N'))
New-Item -Path $modelTool -ItemType Directory | Out-Null
Copy-Item .\reference\tools\export-pleos-models.mjs -Destination $modelTool
npm.cmd install --prefix $modelTool --ignore-scripts --no-audit --no-fund three@0.187.0 typescript@5.9.3
node (Join-Path $modelTool 'export-pleos-models.mjs') .\reference\figma-make\src .\app\src\main\assets\pleos-models
```
