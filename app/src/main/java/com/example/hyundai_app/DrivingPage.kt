package com.example.hyundai_app

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.CancellationException
import java.util.Locale

private data class Rule(val distance: String, val sign: String, val title: String, val detail: String, val color: Color)
private val demoRules = listOf(
    Rule("120m", "30", "어린이 보호구역", "제한속도 30km/h", Amber),
    Rule("420m", "CAM", "신호 과속 단속", "신호 변경에 주의하세요", Coral),
    Rule("680m", "↘", "우측 차로 합류", "합류 차량에 양보하세요", Teal),
)

@Composable
internal fun DrivingPage(state: PleosDriveState, navigation: NavigationService, modifier: Modifier, wide: Boolean) {
    var zoom by rememberSaveable { mutableFloatStateOf(1f) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var request by remember { mutableStateOf<String?>(null) }
    var routeError by rememberSaveable { mutableStateOf<String?>(null) }
    var activeAction by rememberSaveable { mutableIntStateOf(-1) }
    var activeRule by rememberSaveable { mutableIntStateOf(-1) }

    fun route(place: String) {
        val target = place.trim()
        if (target.isBlank() || request != null) return
        query = target
        searchOpen = false
        routeError = null
        request = target
    }
    LaunchedEffect(request) {
        val place = request ?: return@LaunchedEffect
        try {
            state.destination = navigation.requestRoute(place)
        } catch (cancel: CancellationException) {
            throw cancel
        } catch (error: RouteRequestException) {
            routeError = error.message
        } catch (error: Exception) {
            Log.w("PleosNavigation", "Could not request route", error)
            routeError = "Pleos 내비에 연결할 수 없어요."
        } finally {
            if (request == place) request = null
        }
    }

    Column(modifier) {
        DrivingHeader(state.elapsed, state.batteryLabel, wide)
        if (wide) {
            Row(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 17.u, vertical = 10.u),
                horizontalArrangement = Arrangement.spacedBy(11.u)) {
                MapPanel(Modifier.weight(1.69f).fillMaxHeight(), state.destination, zoom, request, routeError, navigation.isDemo,
                    { if (request == null) searchOpen = true }, { zoom = (zoom + it).coerceIn(.80f, 1.4f) }, { zoom = 1f })
                Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(11.u)) {
                    RulePanel(state.elapsed, Modifier.weight(1.94f).fillMaxWidth()) { activeRule = it }
                    QuickPanel(state.selectedQuickActions, Modifier.weight(1f).fillMaxWidth()) { activeAction = it }
                }
            }
        } else {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(10.u),
                verticalArrangement = Arrangement.spacedBy(10.u)) {
                MapPanel(Modifier.fillMaxWidth().height(405.u), state.destination, zoom, request, routeError, navigation.isDemo,
                    { if (request == null) searchOpen = true }, { zoom = (zoom + it).coerceIn(.80f, 1.4f) }, { zoom = 1f })
                RulePanel(state.elapsed, Modifier.fillMaxWidth()) { activeRule = it }
                QuickPanel(state.selectedQuickActions, Modifier.fillMaxWidth().height(125.u)) { activeAction = it }
            }
        }
    }
    if (searchOpen) AppDialog("목적지 검색", if (navigation.isDemo) "경로 안내 · 데모" else "Pleos 내비게이션",
        "location", { searchOpen = false }) {
        routeError?.let { Label(it, 13, Coral) }
        if (navigation.isDemo) Label("출발 · 현재 위치", 13, Muted)
        OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), singleLine = true,
            label = { Label(if (navigation.isDemo) "장소, 주소를 입력하세요" else "목적지 이름 또는 주소", 13, Muted) })
        ActionButton(if (navigation.isDemo) "가장 빠른 경로 찾기" else "Pleos 내비로 보내기", Modifier.fillMaxWidth()) {
            route(query)
        }
        Label(if (navigation.isDemo) "최근 검색한 목적지" else "연결 테스트 목적지", 13, weight = FontWeight.Bold)
        navigation.suggestedPlaces.filter { !navigation.isDemo || query.isBlank() || it.contains(query) }.forEach { place ->
            AppListRow("location", place, if (navigation.isDemo) "최근 목적지" else "서울시 강남구 남부순환로 2621",
                onClick = { route(place) })
        }
    }
    if (activeAction >= 0) {
        QuickActionDialog(activeAction, navigation, { activeAction = -1 }, ::route)
    }
    if (activeRule >= 0) {
        val rule = demoRules[activeRule]
        AppDialog(rule.title, "교통법규 · 데모", "shield", { activeRule = -1 }) {
            Label(rule.detail, 16)
            Label("${rule.distance} 앞", 13, rule.color)
        }
    }
}

@Composable
private fun DrivingHeader(elapsed: Int, battery: String, wide: Boolean) {
    val time = String.format(Locale.US, "%02d:%02d", elapsed / 60, elapsed % 60)
    Row(Modifier.fillMaxWidth().height(if (wide) 92.u else 70.u).background(Color(0xFFF6FCF9))
        .padding(horizontal = if (wide) 25.u else 12.u), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (wide) 24.u else 10.u)) {
        Row(Modifier.weight(if (wide) 1.25f else 1.75f), horizontalArrangement = Arrangement.spacedBy(9.u),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.u).background(Mint, CircleShape))
            Column {
                Label("안전 주행 기록 중", 15, weight = FontWeight.Bold, lines = 1)
                Label("$time · 시연 주행 기록 중", 10, Muted, lines = 1)
            }
        }
        if (wide) {
            HeaderMetric("calendar", "2025. 06. 12", "수요일", Modifier.weight(1f))
            HeaderMetric("clock", "현재 시각", "10:24", Modifier.weight(.9f))
        }
        HeaderMetric("sparkles", "이번 주행 경험치", "+${elapsed / 10} XP", Modifier.weight(1.15f))
        HeaderMetric("fuel", "배터리", battery, Modifier.weight(.8f))
        if (wide) {
            HeaderMetric("road", "전체 주행", "24,386 km", Modifier.weight(1f))
            HeaderMetric("sun", "오늘의 날씨", "24° 맑음", Modifier.weight(.95f))
        }
    }
}

@Composable
private fun HeaderMetric(icon: String, caption: String, value: String, modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.u)) {
        if (icon == "sun") IconTile(icon, 39, Amber, Color(0xFFFFF2CF))
        else AppIcon(icon, Modifier.size(17.u))
        Column {
            Label(caption, 11, Muted, lines = 1)
            Label(value, if (icon == "sun") 21 else 16, weight = FontWeight.Bold, lines = 1)
        }
    }
}

@Composable
private fun MapPanel(modifier: Modifier, destination: RoutePreview?, zoom: Float, request: String?, routeError: String?,
    demoNavigation: Boolean, search: () -> Unit, adjustZoom: (Float) -> Unit, reset: () -> Unit) {
    Panel(modifier) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth().height(68.u).padding(horizontal = 15.u),
                horizontalArrangement = Arrangement.spacedBy(10.u), verticalAlignment = Alignment.CenterVertically) {
                Label("다함께 차차차", 12, Teal, weight = FontWeight.Bold, lines = 1)
                Row(verticalAlignment = Alignment.Bottom) {
                    Label("58", 24, weight = FontWeight.Bold)
                    Label("km/h · 데모", 8, Muted)
                }
                IconTile("arrow", 37, Color(0xFF4AC8CF))
                Column(Modifier.weight(1f)) {
                    Label(when {
                        request != null -> "경로 요청 중"
                        destination != null && !demoNavigation -> if (destination.confirmed) "Pleos 목적지 확인 완료"
                            else "Pleos 내비에서 경로 확인"
                        else -> "320m 앞 · 시연"
                    }, 10, Teal, lines = 1)
                    Label(destination?.place ?: "푸른대로 방면 우회전", 14, weight = FontWeight.Bold, lines = 1)
                }
                if (demoNavigation || destination == null) {
                    Column { Label("도착 예정", 9, Muted); Label(destination?.arrival ?: "10:48", 12, weight = FontWeight.Bold) }
                    Column { Label("남은 거리", 9, Muted); Label(destination?.distance ?: "12.4km", 12, weight = FontWeight.Bold) }
                }
            }
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                RouteMap(zoom, demoNavigation && destination != null, Modifier.matchParentSize())
                Panel(Modifier.align(Alignment.TopStart).padding(14.u).width(minOf(360.u, maxWidth * .68f))
                    .height(42.u).clickable(onClick = search), color = Navy, edge = Color(0xFF33515E), radius = 12) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 12.u), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(9.u)) {
                        AppIcon("location", Modifier.size(18.u), Color(0xFF78E1E8))
                        Label(when {
                            request != null -> "경로를 찾는 중…"
                            routeError != null -> "목적지 설정 실패 · 다시 보기"
                            destination != null -> if (destination.confirmed) destination.place else "Pleos 내비 요청 · ${destination.place}"
                            else -> "어디로 갈까요?"
                        }, 13, Color.White, Modifier.weight(1f), FontWeight.Bold, 1)
                        AppIcon("chevron", Modifier.size(14.u), Color(0xFF78E1E8))
                    }
                }
                Column(Modifier.align(Alignment.TopEnd).padding(15.u), verticalArrangement = Arrangement.spacedBy(3.u)) {
                    ActionButton("+", bg = Navy, minHeight = 37) { adjustZoom(.12f) }
                    ActionButton("−", bg = Navy, minHeight = 37) { adjustZoom(-.12f) }
                }
                Panel(Modifier.align(Alignment.BottomStart).padding(15.u), color = Navy, edge = Color.Transparent, radius = 8) {
                    Label("⌖  시연 지도", 11, Color(0xFF78E1E8), Modifier.padding(10.u))
                }
                Box(Modifier.align(Alignment.BottomEnd).padding(15.u).size(41.u).clip(CircleShape).background(Navy)
                    .clickable(onClickLabel = "지도 원래 배율", onClick = reset), contentAlignment = Alignment.Center) {
                    AppIcon("compass", Modifier.size(23.u), Color(0xFF78E1E8))
                }
            }
        }
    }
}

@Composable
private fun RulePanel(elapsed: Int, modifier: Modifier, details: (Int) -> Unit) {
    Panel(modifier) {
        Column(Modifier.fillMaxWidth().padding(14.u), verticalArrangement = Arrangement.spacedBy(6.u)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.u)) {
                IconTile("shield", 35)
                Label("앞으로 지켜야 할 교통법규 · 데모", 15, weight = FontWeight.Bold)
            }
            Spacer(Modifier.height(6.u))
            val visible = demoRules.indices.drop((elapsed / 6).coerceAtMost(demoRules.size))
            if (visible.isEmpty()) Label("시연 안내가 끝났어요", 14, Teal)
            visible.forEach { i ->
                val rule = demoRules[i]
                Panel(Modifier.fillMaxWidth().height(50.u).clickable { details(i) }, color = Wash, radius = 11) {
                    Row(Modifier.fillMaxSize().padding(horizontal = 14.u), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.u)) {
                        Label(rule.distance, 10, Muted, Modifier.width(29.u))
                        Box(Modifier.size(30.u).background(rule.color.copy(alpha = .09f), CircleShape), contentAlignment = Alignment.Center) {
                            Label(rule.sign, 9, rule.color, weight = FontWeight.Bold)
                        }
                        Label(rule.title, 13, Ink, Modifier.weight(1f), FontWeight.Medium)
                        if (elapsed / 6 == i && elapsed % 6 >= 4) Label(if (i == 1) "△" else "○", 17, rule.color)
                        else AppIcon("chevron", Modifier.size(16.u), Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPanel(actions: List<Int>, modifier: Modifier, open: (Int) -> Unit) {
    val colors = listOf(Color(0xFF91BAAA), Color(0xFF9DB5CC), Color(0xFFB99FC6), Color(0xFFC8AD88),
        Color(0xFF78B6A6), Color(0xFF879FAE))
    Panel(modifier) {
        if (actions.isEmpty()) {
            Label("설정에서 빠른 실행을 추가할 수 있어요", 13, Muted, Modifier.align(Alignment.Center).padding(10.u))
        } else Row(Modifier.fillMaxSize().padding(12.u), horizontalArrangement = Arrangement.spacedBy(7.u)) {
            actions.forEach { i ->
                val action = quickActions[i]
                Column(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(12.u)).background(
                    Brush.verticalGradient(listOf(colors[i].copy(alpha = .72f), colors[i]))
                ).clickable { open(i) }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    IconTile(action.icon, 36, Color.White, Color(0x25FFFFFF))
                    Spacer(Modifier.height(5.u))
                    Label(action.label, 10, Color.White, weight = FontWeight.Bold, lines = 1)
                }
            }
        }
    }
}

@Composable
private fun QuickActionDialog(index: Int, navigation: NavigationService, close: () -> Unit, route: (String) -> Unit) {
    val action = quickActions[index]
    var reply by rememberSaveable(index) { mutableStateOf("") }
    var playing by rememberSaveable(index) { mutableStateOf(false) }
    AppDialog(action.label, "빠른 실행 · 데모", action.icon, close) {
        when (index) {
            0 -> {
                if (reply.isBlank()) listOf("김민준", "집", "회사").forEach { person ->
                    AppListRow("phone", person, "통화 화면 미리보기") { reply = person }
                } else {
                    Label("$reply · 통화 화면", 17, weight = FontWeight.Bold)
                    ActionButton("통화 종료", Modifier.fillMaxWidth(), bg = Coral) { reply = "" }
                }
            }
            1 -> {
                listOf("운전 중이에요", "곧 도착해요", "나중에 연락할게요").forEach { message ->
                    AppListRow("message", message, "빠른 답장") { reply = "선택한 메시지 · $message" }
                }
                if (reply.isNotBlank()) Label(reply, 13, Teal)
            }
            2 -> {
                Label("드라이브 플레이리스트", 17, weight = FontWeight.Bold)
                Label("오늘의 드라이브", 13, Muted)
                ActionButton(if (playing) "일시 정지" else "재생", Modifier.fillMaxWidth()) { playing = !playing }
            }
            3 -> {
                Label(reply.ifBlank { "무엇을 도와드릴까요?" }, 17, weight = FontWeight.Bold)
                listOf("가까운 충전소 찾아줘", "현재 차량 상태 알려줘", "안전운전 점수 보여줘").forEach { prompt ->
                    AppListRow("sparkles", prompt, "예시 음성 명령") { reply = "선택한 예시 명령 · $prompt" }
                }
            }
            4 -> {
                if (navigation.isDemo) {
                    Label("저장된 집으로 경로를 설정할까요?", 16)
                    ActionButton("경로 찾기", Modifier.fillMaxWidth()) { route("집"); close() }
                } else Label("앱에 저장된 집 주소가 없어요.", 16)
            }
            5 -> if (navigation.isDemo) {
                navigation.suggestedPlaces.forEach { place ->
                    AppListRow("location", place, "최근 목적지") { route(place); close() }
                }
            } else Label("Pleos 최근 목적지 연결을 준비 중이에요.", 16)
        }
    }
}

