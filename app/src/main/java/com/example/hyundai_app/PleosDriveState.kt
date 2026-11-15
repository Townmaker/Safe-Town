package com.example.hyundai_app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

internal enum class DrivePage(val label: String, val icon: String) {
    Village("홈", "home"), History("주행히스토리", "history"),
    Driving("주행", "road"), Profile("마이페이지", "user"),
    Settings("설정", "settings"), Result("주행 결과", "shield"),
}

internal data class DemoRecord(
    val date: String, val route: String, val distance: String,
    val duration: String, val xp: Int, val score: Int, val distanceFromVehicle: Boolean = false,
)

internal data class VehiclePart(val name: String, val detail: String, val percent: Int, val group: Int)

internal val vehicleParts = listOf(
    VehiclePart("엔진오일", "교체까지 7,640km", 76, 3),
    VehiclePart("브레이크액", "교체까지 15,614km", 61, 2),
    VehiclePart("냉각수", "교체까지 75,614km", 76, 3),
    VehiclePart("에어컨필터", "교체까지 7,640km", 76, 0),
    VehiclePart("브레이크패드", "교체까지 15,614km", 61, 2),
    VehiclePart("자동차 배터리", "교체까지 2년 4개월", 58, 0),
    VehiclePart("타이어", "교체까지 25,614km", 51, 1),
    VehiclePart("엔진에어필터", "교체까지 15,614km", 61, 0),
    VehiclePart("미션오일", "교체까지 55,614km", 70, 3),
    VehiclePart("점화플러그", "교체까지 55,614km", 70, 0),
    VehiclePart("연료필터", "교체까지 25,614km", 51, 0),
)

internal val villageStages = listOf(
    "움집 마을", "초가집 마을", "기와집 마을", "벽돌집 마을", "단독주택 마을", "연립주택 마을",
    "아파트 도시", "초고층 주상복합", "스마트 친환경 타워", "공중부양 주거단지", "우주 돔 거주기지", "은하 우주정거장",
)
internal val friends = listOf("민준" to 8, "서윤" to 6, "도현" to 4, "하린" to 11)
internal val badges = listOf(
    "서울 한강", "부산 해운대", "경주 첨성대", "제주 오름", "강릉 바다", "전주 한옥", "인천 대교",
    "속초 설악", "대전 엑스포", "여수 밤바다", "광주 무등산", "수원 화성", "통영 항구", "춘천 호수",
    "울산 대왕암", "포항 호미곶", "군산 근대항", "담양 대나무", "안동 하회마을", "태백 별빛",
    "평창 대관령", "남해 다랭이", "목포 해상로", "세종 호수공원",
)

internal data class QuickAction(val label: String, val icon: String)
internal val quickActions = listOf(
    QuickAction("전화", "phone"), QuickAction("메시지", "message"), QuickAction("음악", "music"),
    QuickAction("AI에게 말하기", "sparkles"),
)

internal val preferences = listOf(
    "음성 안내" to "주행 중 필요한 안내 음성",
    "효과음" to "정차 후 게임 효과음",
    "알림" to "업적, 마을 성장, 친구 활동 알림",
    "제한속도 경고" to "현재 도로 제한속도 초과 시 음성과 화면으로 안내",
    "교통법규 자동 판정" to "신호와 표지판 통과 결과를 자동으로 기록",
    "차량 상태 자동 점검" to "시동 후 차량 안전 상태와 관리 항목 확인",
    "위치 기반 배지 알림" to "새로운 지역 진입 시 컬렉션 획득 가능 여부 안내",
    "안전운전 기록 공개" to "다른 이용자에게 인증 기록 공개 여부",
)

internal val profileMenus = listOf(
    Triple("user", "내 프로필", "닉네임, 프로필 사진, 운전자 칭호"),
    Triple("history", "주행 기록", "월별·주별 주행 기록과 안전운전 점수"),
    Triple("shield", "업적 및 배지", "누적 안전주행 기록과 지역 컬렉션"),
    Triple("home", "마을 성장 기록", "레벨, 누적 획득 XP, 건물 발전 내역"),
    Triple("users", "친구 및 커뮤니티", "친구 목록, 크루, 공동 목표"),
)

internal val settingDetails = linkedMapOf(
    "연결 차량 및 데이터 권한" to listOf("차량 상태 읽기", "현재 위치 접근", "주행 기록 저장", "음성 서비스 접근"),
    "주행 데이터 관리" to listOf("자동 주행 기록", "안전점수 분석", "위반 이벤트 저장", "월간 리포트 생성"),
    "개인정보 및 보안" to listOf("생체 인증", "앱 잠금", "위치 기록 보호", "친구 공개 범위"),
    "음성 데이터 및 AI 설정" to listOf("온디바이스 음성 인식", "음성 기록 자동 삭제", "차차 AI 개인화", "음성 호출어 사용"),
    "배지·컬렉션 알림 관리" to listOf("새 지역 배지", "기간 한정 배지", "친구 획득 소식", "컬렉션 완성 알림"),
    "화면 밝기 및 주·야간 모드" to listOf("주간", "야간", "자동"),
    "운전자 프로필 전환" to listOf("안전운전 마스터", "김민준", "게스트 모드"),
    "고객센터 / FAQ" to listOf("차량 데이터가 보이지 않아요", "안전점수는 어떻게 계산하나요?", "배지를 획득하지 못했어요", "내비게이션 연결이 끊겼어요"),
    "앱 정보 / 버전" to emptyList(),
)

private val initialRecords = listOf(
    DemoRecord("오늘 10:24", "성수동 → 여의도", "18.4 km", "32분 18초", 142, 92),
    DemoRecord("6월 11일 18:42", "강남역 → 잠실", "12.7 km", "26분 04초", 98, 88),
    DemoRecord("6월 9일 14:10", "서울역 → 북촌", "9.2 km", "18분 42초", 110, 97),
    DemoRecord("6월 7일 08:35", "마포 → 판교", "31.8 km", "48분 11초", 204, 90),
)

internal data class RoutePreview(val place: String, val confirmed: Boolean = true)

internal data class NavigationPlace(
    val name: String,
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val displayLatitude: Double? = null,
    val displayLongitude: Double? = null,
    val poiId: String? = null,
    val poiSubId: String? = null,
    val poiProvider: String? = null,
)

internal data class DestinationSearch(
    val origin: String? = null,
    val recent: List<NavigationPlace>? = null,
)

internal interface NavigationService {
    val isDemo: Boolean
    val suggestedPlaces: List<NavigationPlace>
    suspend fun destinationSearch(): DestinationSearch
    suspend fun nextInstruction(): String?
    suspend fun requestRoute(place: NavigationPlace): RoutePreview
}

internal class DemoNavigationService : NavigationService {
    override val isDemo = true
    override val suggestedPlaces = emptyList<NavigationPlace>()
    private val recent = listOf(
        NavigationPlace("서울숲 주차장", "서울 성동구 뚝섬로 273"),
        NavigationPlace("여의도 한강공원"),
        NavigationPlace("성수동 카페거리"),
        NavigationPlace("강남역 5번 출구"),
    )

    override suspend fun destinationSearch() = DestinationSearch("현재 위치", recent)

    override suspend fun nextInstruction(): String? = null

    override suspend fun requestRoute(place: NavigationPlace): RoutePreview {
        delay(700)
        return RoutePreview(place.name)
    }
}

internal class PleosDriveState(context: Context) {
    private val storage = context.getSharedPreferences("village_drive", Context.MODE_PRIVATE)
    val automotive = context.isAutomotive()
    var inspection by mutableStateOf<VehicleInspection?>(null)
    val batteryLabel: String
        get() = inspection?.battery?.let { "$it%" } ?: if (automotive) "—" else "72% · 시연"
    var visitingFriend by mutableStateOf<Int?>(null)
    val villageLevel get() = (earnedXp / 800 + 1).coerceIn(1, villageStages.size)

    fun visitFriend(index: Int) {
        visitingFriend = index
        navigate(DrivePage.Village)
    }

    private var detailToggles by mutableStateOf(
        settingDetails.flatMap { (title, items) ->
            items.mapIndexed { index, item ->
                val key = "detail:$title:$item"
                key to storage.getBoolean(key, index in listOf(0, 1, 3))
            }
        }.toMap()
    )
    var displayMode by mutableStateOf(storage.getString("display-mode", "자동") ?: "자동")
        private set
    var displayBrightness by mutableFloatStateOf(storage.getFloat("display-brightness", .72f))
        private set

    fun isDetailEnabled(title: String, item: String): Boolean = detailToggles.getValue("detail:$title:$item")

    fun toggleDetail(title: String, item: String) {
        val key = "detail:$title:$item"
        val value = !detailToggles.getValue(key)
        detailToggles = detailToggles + (key to value)
        storage.edit().putBoolean(key, value).apply()
    }

    fun saveDisplayMode(value: String) {
        displayMode = value
        storage.edit().putString("display-mode", value).apply()
    }

    fun saveBrightness(value: Float) {
        displayBrightness = value
        storage.edit().putFloat("display-brightness", value).apply()
    }

    var page by mutableStateOf(DrivePage.Village)
        private set
    var isDriving by mutableStateOf(false)
        private set
    var elapsed by mutableStateOf(0)
    private var startOdometerKm: Float? = null
    var destination by mutableStateOf<RoutePreview?>(null)
    var result by mutableStateOf<DemoRecord?>(null)
        private set
    var records by mutableStateOf(readRecords() + initialRecords)
        private set
    var driverName by mutableStateOf(storage.getString("driver", "안전운전 마스터").orEmpty())
        private set
    var earnedXp by mutableStateOf(storage.getInt("xp", 0))
        private set
    val selectedQuickActions = listOf(0, 1, 2, 3)
    var toggles by mutableStateOf(preferences.indices.map { storage.getBoolean("toggle-$it", it != 4) })
        private set

    fun navigate(target: DrivePage) {
        if (target == DrivePage.Driving && !isDriving) {
            elapsed = 0
            destination = null
            startOdometerKm = inspection?.odometerKm
            isDriving = true
        }
        page = target
    }

    fun endDrive() {
        if (!isDriving) return
        val start = startOdometerKm
        val end = inspection?.odometerKm
        val measuredDistance = if (start != null && end != null && end >= start) end - start else null
        val distance = measuredDistance?.let { String.format(Locale.US, "%.1f km", it) }
            ?: "확인 불가".takeIf { automotive }
            ?: String.format(Locale.US, "%.1f km", elapsed * 58 / 3600.0)
        val record = DemoRecord(
            "오늘", destination?.takeIf { it.confirmed }?.let { "현재 위치 → ${it.place}" }
                ?: if (automotive) "목적지 미설정" else "현재 위치 → 푸른대로",
            distance, "${elapsed / 60}분 ${elapsed % 60}초", 142, 92, measuredDistance != null,
        )
        isDriving = false
        startOdometerKm = null
        result = record
        records = listOf(record) + records
        earnedXp += record.xp
        val recent = JSONArray()
        records.dropLast(initialRecords.size).take(30).forEach { item ->
            recent.put(JSONObject().put("date", item.date).put("route", item.route).put("distance", item.distance)
                .put("duration", item.duration).put("xp", item.xp).put("score", item.score)
                .put("distanceFromVehicle", item.distanceFromVehicle))
        }
        storage.edit().putInt("xp", earnedXp).putString("records", recent.toString()).apply()
        page = DrivePage.Result
    }

    fun rename(value: String) {
        val name = value.trim().take(24)
        if (name.isEmpty()) return
        driverName = name
        storage.edit().putString("driver", name).apply()
    }

    fun toggle(index: Int) {
        val enabled = !toggles[index]
        toggles = toggles.mapIndexed { i, current -> if (i == index) enabled else current }
        storage.edit().putBoolean("toggle-$index", enabled).apply()
    }

    private fun readRecords(): List<DemoRecord> = runCatching {
        val json = JSONArray(storage.getString("records", "[]"))
        List(json.length()) { index ->
            val item = json.getJSONObject(index)
            DemoRecord(item.getString("date"), item.getString("route"), item.getString("distance"),
                item.getString("duration"), item.getInt("xp"), item.getInt("score"),
                item.optBoolean("distanceFromVehicle"))
        }
    }.getOrDefault(emptyList())
}

