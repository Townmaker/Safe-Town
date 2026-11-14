package com.example.hyundai_app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun VillagePage(state: PleosDriveState, modifier: Modifier, wide: Boolean) {
    var showChrome by rememberSaveable { mutableStateOf(true) }
    var friendIndex by rememberSaveable { mutableIntStateOf(-1) }
    var dialog by rememberSaveable { mutableStateOf("") }
    val stage = if (friendIndex >= 0) friends[friendIndex].second else (state.earnedXp / 800).coerceAtMost(villageStages.lastIndex)
    val future = stage >= 7
    val sky = if (future) listOf(Color(0xFF3B637B), Color(0xFF72B2CA), Color(0xFFB3D8D2))
        else listOf(Color(0xFF7CCBDD), Color(0xFFA5E4EC), Color(0xFFD9F2D0))

    BoxWithConstraints(modifier.background(Brush.verticalGradient(sky))) {
        VillageModel(stage, Modifier.matchParentSize()) { showChrome = !showChrome }
        if (showChrome) {
            Box(Modifier.fillMaxWidth().height(82.u).background(Brush.verticalGradient(
                listOf(Color(0x6C144653), Color.Transparent)))) {
                Row(Modifier.align(Alignment.TopStart).padding(start = if (wide) 34.u else 12.u, top = 16.u),
                    horizontalArrangement = Arrangement.spacedBy(11.u), verticalAlignment = Alignment.CenterVertically) {
                    IconTile("road", 38, background = Color(0x7EEDFFFF))
                    Column {
                        Label("다함께 차차차", 17, weight = FontWeight.Bold)
                        Label("안전운전으로 자라는 마을", 14, Color.White)
                    }
                }
                if (wide) Row(Modifier.align(Alignment.TopCenter).padding(top = 23.u),
                    horizontalArrangement = Arrangement.spacedBy(12.u), verticalAlignment = Alignment.CenterVertically) {
                    AppIcon("calendar", Modifier.size(18.u), Ink)
                    Label("6월 12일 수요일", 15)
                    Label("10:24", 20, weight = FontWeight.Bold)
                }
            }

            val levelWidth = if (wide) minOf(maxWidth * .25f, 257.u) else maxWidth * .55f
            Column(
                Modifier.align(Alignment.TopStart).padding(start = if (wide) 34.u else 12.u, top = if (wide) 103.u else 82.u)
                    .width(levelWidth).clip(RoundedCornerShape(18.u)).background(
                        Brush.horizontalGradient(listOf(Color(0x71325661), Color(0x1C396775)))
                    ).padding(if (wide) 20.u else 12.u),
                verticalArrangement = Arrangement.spacedBy(if (wide) 12.u else 8.u),
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.clip(RoundedCornerShape(8.u)).background(Mint).padding(horizontal = 9.u, vertical = 5.u)) {
                        Label("LV. ${(stage + 1).toString().padStart(2, '0')}", 11, weight = FontWeight.Bold)
                    }
                    Spacer(Modifier.weight(1f))
                    Label(if (friendIndex >= 0) "${friends[friendIndex].first}의 마을" else "나의 마을", 11, Color(0xFFD5F0EF))
                }
                Column {
                    Label("작은 발걸음에서", if (wide) 25 else 19, Color.White, weight = FontWeight.Bold)
                    Label("별까지 이어지는 길", if (wide) 25 else 19, Color(0xFFDAFFF2), weight = FontWeight.Bold)
                }
                Label(villageStages[stage], 15, Color.White, weight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Label("누적 안전 주행", 11, Color(0xFFD7F6F2))
                    Label(if (friendIndex < 0) "186 km" else "${(stage + 1) * 1240} km", 11, Color.White, weight = FontWeight.Bold)
                }
                Meter(if (friendIndex < 0) .38f + (state.earnedXp % 800) / 1800f else .38f, Modifier.fillMaxWidth())
                Label("♧  오늘 안전 주행으로  +${180 + state.earnedXp} XP", 11, Color(0xFFDEF7F1), weight = FontWeight.Medium)
            }

            Column(Modifier.align(Alignment.TopEnd).padding(top = if (wide) 103.u else 83.u, end = if (wide) 34.u else 10.u)
                .width(if (wide) minOf(maxWidth * .22f, 220.u) else maxWidth * .36f),
                verticalArrangement = Arrangement.spacedBy(9.u)) {
                VillageShortcut("users", "친구 마을", "4명 접속 중") { dialog = "friends" }
                VillageShortcut("shield", "컬렉션", "12 / 48 배지") { dialog = "badges" }
            }

            if (wide) Panel(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.u), color = Color(0xC9F7FAE9), radius = 13) {
                Label("↔  드래그 둘러보기     ⌁  핀치 확대 · 축소     ○  터치 화면만 보기", 11, Ink,
                    Modifier.padding(horizontal = 15.u, vertical = 11.u))
            }
            Panel(Modifier.align(Alignment.BottomEnd).padding(end = if (wide) 34.u else 12.u, bottom = 16.u)
                .width(if (wide) 230.u else 176.u).clickable { state.navigate(DrivePage.Driving) },
                color = Color(0xEE234A50), edge = Color(0xFF6AA8A4), radius = 18) {
                Row(Modifier.fillMaxWidth().padding(12.u), horizontalArrangement = Arrangement.spacedBy(10.u),
                    verticalAlignment = Alignment.CenterVertically) {
                    IconTile("arrow", 47, Ink, Mint)
                    Column(Modifier.weight(1f)) {
                        Label("오늘도 안전하게", 10, Color(0xFFB4D5D5))
                        Label(if (state.isDriving) "주행으로 돌아가기" else "주행 시작", if (wide) 19 else 15, Color.White, weight = FontWeight.Bold)
                    }
                    AppIcon("chevron", Modifier.size(16.u), Color.White)
                }
            }
        }
    }

    if (dialog == "friends") {
        AppDialog("친구의 마을", "함께 성장하는 이웃", "users", { dialog = "" }) {
            friends.forEachIndexed { index, (name, level) ->
                Panel(Modifier.fillMaxWidth().clickable { friendIndex = index; dialog = "" }, radius = 12) {
                    Row(Modifier.fillMaxWidth().padding(11.u), horizontalArrangement = Arrangement.spacedBy(10.u),
                        verticalAlignment = Alignment.CenterVertically) {
                        IconTile("user", 37)
                        Column(Modifier.weight(1f)) { Label(name, 15, weight = FontWeight.Bold); Label(villageStages[level], 11, Muted) }
                        AppIcon("chevron", Modifier.size(17.u))
                    }
                }
            }
            if (friendIndex >= 0) ActionButton("나의 마을로 돌아가기", Modifier.fillMaxWidth()) { friendIndex = -1; dialog = "" }
        }
    }
    if (dialog == "badges") {
        AppDialog("나의 배지 컬렉션", "지역 방문 기록", "shield", { dialog = "" }) { BadgeGrid(wide) }
    }
}

@Composable
private fun VillageShortcut(icon: String, title: String, subtitle: String, open: () -> Unit) {
    Panel(Modifier.fillMaxWidth().height(67.u).clickable(onClick = open), color = Color(0xDCF8FFFF), radius = 15) {
        Row(Modifier.fillMaxSize().padding(horizontal = 15.u), horizontalArrangement = Arrangement.spacedBy(11.u),
            verticalAlignment = Alignment.CenterVertically) {
            AppIcon(icon, Modifier.size(22.u))
            Column { Label(title, 14, weight = FontWeight.Bold); Label(subtitle, 10, Muted) }
        }
    }
}

@Composable
internal fun BadgeGrid(wide: Boolean) {
    val count = if (wide) 4 else 3
    badges.chunked(count).forEachIndexed { row, labels ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.u)) {
            labels.forEachIndexed { col, name ->
                val earned = row * count + col < 5
                Panel(Modifier.weight(1f), color = if (earned) Color(0xFFE8F8F0) else Wash, radius = 10) {
                    Column(Modifier.fillMaxWidth().padding(8.u), horizontalAlignment = Alignment.CenterHorizontally) {
                        AppIcon("sparkles", Modifier.size(22.u), if (earned) Teal else Muted)
                        Label(name, 12, lines = 1)
                        Label(if (earned) "방문 완료" else "미획득", 10, Muted)
                    }
                }
            }
            repeat(count - labels.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
internal fun VehicleCheck(wide: Boolean, battery: Int?, close: () -> Unit) {
    val subtitle = if (battery == null) "PLEOS VEHICLE CHECK · 데모" else "PLEOS VEHICLE CHECK · 배터리 연동"
    AppDialog("출발 전 차량 상태", subtitle, "car", close) {
        val checks = listOf("EV 배터리" to (battery?.let { "$it%" } ?: "72% · 데모"), "타이어 공기압" to "정상", "안전벨트" to "체결",
            "도어 잠금" to "완료", "브레이크액" to "정상", "워셔액" to "보충 필요")
        checks.chunked(if (wide) 3 else 2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.u)) {
                row.forEach { (label, value) ->
                    val ok = label != "워셔액"
                    Panel(Modifier.weight(1f).height(52.u), color = Paper, radius = 12) {
                        Row(Modifier.fillMaxSize().padding(horizontal = 9.u), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.u)) {
                            Box(Modifier.size(28.u).clip(RoundedCornerShape(50)).background(if (ok) Color(0xFF55B49D) else Amber),
                                contentAlignment = Alignment.Center) { Label(if (ok) "✓" else "!", 14, Color.White, weight = FontWeight.Bold) }
                            Column { Label(label, 9, Muted, lines = 1); Label(value, 11, weight = FontWeight.Bold, lines = 1) }
                        }
                    }
                }
            }
        }
        Panel(Modifier.fillMaxWidth(), color = Color(0xFFDDF4E9), radius = 12) {
            Row(Modifier.fillMaxWidth().padding(11.u), horizontalArrangement = Arrangement.spacedBy(11.u),
                verticalAlignment = Alignment.CenterVertically) {
                AppIcon("sparkles", Modifier.size(20.u))
                Column { Label("시연 점검 · 안전 조건 5개 충족", 12, Teal, weight = FontWeight.Bold); Label("차량 관리 보너스 +15 XP", 9, Muted) }
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Label("워셔액 보충이 필요합니다. 다음 주행 전 점검해 주세요.", 10, Color(0xFFA4540B))
        }
    }
}

