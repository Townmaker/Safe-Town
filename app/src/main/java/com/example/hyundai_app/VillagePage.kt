package com.example.hyundai_app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Path
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun VillagePage(state: PleosDriveState, modifier: Modifier, wide: Boolean) {
    var showChrome by rememberSaveable { mutableStateOf(true) }
    val friendIndex = state.visitingFriend
    var dialog by rememberSaveable { mutableStateOf("") }
    val stage = friendIndex?.let { friends[it].second } ?: (state.villageLevel - 1)
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
                        Label("Safe Town", 17, weight = FontWeight.Bold)
                    }
                }
                if (wide) Row(Modifier.align(Alignment.TopCenter).padding(top = 23.u),
                    horizontalArrangement = Arrangement.spacedBy(12.u), verticalAlignment = Alignment.CenterVertically) {
                    AppIcon("calendar", Modifier.size(18.u), Ink)
                    val clock = AppClock.current
                    Label(clock.format(dateFormat), 15)
                    Label(clock.format(timeFormat), 20, weight = FontWeight.Bold)
                }
            }

            val levelWidth = if (wide) (maxWidth * .25f).coerceIn(290.u, 350.u) else maxWidth * .55f
            GradeCard(stage, friendIndex?.let { friends[it].first }, state.earnedXp, wide,
                Modifier.align(Alignment.TopStart)
                    .padding(start = if (wide) 34.u else 12.u, top = if (wide) 103.u else 82.u)
                    .width(levelWidth))
            if (friendIndex != null) {
                ActionButton("나의 마을로 돌아가기", Modifier.align(Alignment.BottomStart)
                    .padding(start = if (wide) 34.u else 12.u, bottom = 16.u),
                    bg = Paper, fg = Teal) { state.visitingFriend = null }
            }

            Column(Modifier.align(Alignment.TopEnd).padding(top = if (wide) 103.u else 83.u, end = if (wide) 34.u else 10.u)
                .width(if (wide) minOf(maxWidth * .22f, 220.u) else maxWidth * .36f),
                verticalArrangement = Arrangement.spacedBy(9.u)) {
                VillageShortcut("users", "친구 마을", "4명 접속 중") { dialog = "friends" }
                VillageShortcut("shield", "컬렉션", "12 / 48 배지") { dialog = "badges" }
            }

            if (wide && maxWidth >= 950.u) Panel(Modifier.align(Alignment.BottomCenter).padding(bottom = 16.u), color = Color(0xC9F7FAE9), radius = 13) {
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
                        Label(if (state.isDriving) "주행으로 돌아가기" else "주행 시작",
                            if (wide) 19 else 15, Color.White, weight = FontWeight.Bold, lines = 1)
                    }
                    AppIcon("chevron", Modifier.size(16.u), Color.White)
                }
            }
        }
    }

    if (dialog == "friends") {
        AppDialog("친구의 마을", "함께 성장하는 이웃", "users", { dialog = "" }) {
            friends.forEachIndexed { index, (name, level) ->
                Panel(Modifier.fillMaxWidth().clickable { state.visitFriend(index); dialog = "" }, radius = 12) {
                    Row(Modifier.fillMaxWidth().padding(11.u), horizontalArrangement = Arrangement.spacedBy(10.u),
                        verticalAlignment = Alignment.CenterVertically) {
                        IconTile("user", 37)
                        Column(Modifier.weight(1f)) { Label(name, 15, weight = FontWeight.Bold); Label(villageStages[level], 11, Muted) }
                        AppIcon("chevron", Modifier.size(17.u))
                    }
                }
            }
            if (friendIndex != null) ActionButton("나의 마을로 돌아가기", Modifier.fillMaxWidth()) {
                state.visitingFriend = null
                dialog = ""
            }
        }
    }
    if (dialog == "badges") {
        AppDialog("나의 배지 컬렉션", "지역 방문 기록", "shield", { dialog = "" }) { BadgeGrid(wide) }
    }
}

@Composable
private fun GradeCard(stage: Int, friend: String?, earnedXp: Int, wide: Boolean, modifier: Modifier) {
    val level = stage + 1
    val distance = if (friend == null) 2_277 else level * 1_240
    val goal = (distance / 3_000 + 1) * 3_000
    val numbers = remember { NumberFormat.getIntegerInstance(Locale.KOREA) }

    Panel(modifier, color = Color(0xF3F3FDF9), edge = Color(0x443E8586), radius = 18) {
        Column(Modifier.fillMaxWidth().padding(if (wide) 22.u else 12.u),
            verticalArrangement = Arrangement.spacedBy(8.u)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.u)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.u)) {
                    Label("${friend?.let { "${it}님의" } ?: "나의"} 안전운전 등급", 11, Muted)
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.u)) {
                        Label("Lv.$level", if (wide) 32 else 25, weight = FontWeight.Bold)
                        Label("입니다", 15, modifier = Modifier.padding(bottom = 4.u))
                    }
                    Label("누적 안전운전거리 ${numbers.format(distance)}km · 시연", 10, Muted)
                }
                GradeEmblem(level, Modifier.size(if (wide) 72.u else 45.u, if (wide) 80.u else 51.u))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Label("다음 레벨까지", 10, Muted)
                Label("${numbers.format(goal - distance)}km 남음", 10, weight = FontWeight.Bold)
            }
            Box(Modifier.fillMaxWidth().height(8.u).clip(RoundedCornerShape(8.u)).background(Color(0xFFCFDFDC))) {
                Box(Modifier.fillMaxHeight().fillMaxWidth((distance % 3_000) / 3_000f)
                    .clip(RoundedCornerShape(8.u)).background(
                        Brush.horizontalGradient(listOf(Color(0xFFF35F64), Color(0xFFFF9C60)))))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Label("${numbers.format(goal - 3_000)}km", 8, Muted)
                Label("${numbers.format(goal)}km", 8, Muted)
            }
            HorizontalDivider(color = Line)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.u)) {
                AppIcon("home", Modifier.size(16.u))
                Label(villageStages[stage], 10, modifier = Modifier.weight(1f), weight = FontWeight.Bold, lines = 1)
                Label("오늘 +${if (friend == null) 180 + earnedXp else 180} XP", 9, Color(0xFF965218), weight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun GradeEmblem(level: Int, modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            fun emblem(inset: Float) = Path().apply {
                val w = size.width
                val h = size.height
                moveTo(w * .5f, h * inset)
                lineTo(w * (.92f - inset), h * (.24f + inset / 2))
                lineTo(w * (.92f - inset), h * (.76f - inset / 2))
                lineTo(w * .5f, h * (1f - inset))
                lineTo(w * (.08f + inset), h * (.76f - inset / 2))
                lineTo(w * (.08f + inset), h * (.24f + inset / 2))
                close()
            }
            drawPath(emblem(0f), Brush.linearGradient(listOf(Color(0xFFD8FFF2), Mint, Teal)))
            drawPath(emblem(.10f), Brush.linearGradient(listOf(Color(0xFFC9F5E8), Color(0xFF5BC4AD))))
        }
        Label("LV.$level", 18, Color(0xFF0F625F), weight = FontWeight.Bold, lines = 1)
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

private data class InspectionItem(val name: String, val value: String, val healthy: Boolean?)

@Composable
internal fun VehicleCheck(inspection: VehicleInspection?, automotive: Boolean, close: () -> Unit) {
    val reading = if (automotive) inspection else VehicleInspection(
        battery = 72, tireWarning = false, frontLeftBuckle = true, doorsLocked = true,
        brakeFluidNormal = true, washerWarning = true, engineOilWarning = false, transmissionOilNormal = false,
    )
    val unknown = if (automotive && reading == null) "확인 중…" else "확인 불가"
    fun item(name: String, healthy: Boolean?, good: String, warning: String) =
        InspectionItem(name, when (healthy) { true -> good; false -> warning; null -> unknown }, healthy)
    val checks = listOf(
        InspectionItem("EV 배터리", reading?.battery?.let { "$it%" } ?: unknown, null),
        item("타이어 공기압", reading?.tireWarning?.not(), "정상", "공기압 낮음"),
        item("안전벨트", reading?.frontLeftBuckle, "체결", "미체결"),
        item("도어 잠금", reading?.doorsLocked, "완료", "잠금 해제"),
        item("브레이크액", reading?.brakeFluidNormal, "정상", "점검 필요"),
        item("워셔액", reading?.washerWarning?.not(), "정상", "보충 필요"),
        item("엔진오일 경고등", reading?.engineOilWarning?.not(), "소등", "경고등 켜짐"),
        item("미션오일", reading?.transmissionOilNormal, "정상", "점검 필요"),
    )
    val healthy = checks.count { it.healthy == true }
    val warnings = checks.filter { it.healthy == false }
    val subtitle = if (automotive) "PLEOS VEHICLE CHECK" else "PLEOS VEHICLE CHECK · 시연"
    AppDialog("출발 전 차량 상태", subtitle, "car", close, maxDialogWidth = 720) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = if (maxWidth >= 510.u) 4 else 2
            Column(verticalArrangement = Arrangement.spacedBy(8.u)) {
                checks.chunked(columns).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.u)) {
                        row.forEach { check ->
                            Panel(Modifier.weight(1f).height(57.u), color = Paper, radius = 12) {
                                val color = when (check.healthy) {
                                    true -> Color(0xFF55B49D)
                                    false -> Amber
                                    null -> Muted
                                }
                                Row(Modifier.fillMaxSize().padding(horizontal = 8.u),
                                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.u)) {
                                    Box(Modifier.size(25.u).clip(RoundedCornerShape(50)).background(color),
                                        contentAlignment = Alignment.Center) {
                                        if (check.name == "EV 배터리") {
                                            AppIcon("fuel", Modifier.size(14.u), Color.White)
                                        } else Label(when (check.healthy) { true -> "✓"; false -> "!"; null -> "–" },
                                            13, Color.White, weight = FontWeight.Bold)
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Label(check.name, 10, Muted, lines = 1)
                                        Label(check.value, 12, weight = FontWeight.Bold, lines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Panel(Modifier.fillMaxWidth(), color = Color(0xFFDDF4E9), radius = 12) {
            Row(Modifier.fillMaxWidth().padding(11.u), horizontalArrangement = Arrangement.spacedBy(11.u),
                verticalAlignment = Alignment.CenterVertically) {
                AppIcon("sparkles", Modifier.size(20.u))
                Column {
                    Label("안전 조건 ${healthy}개 충족", 12, Teal, weight = FontWeight.Bold)
                    Label(if (automotive) "상태를 확인한 안전 조건 기준" else "차량 관리 보너스 +15 XP · 시연", 9, Muted)
                }
            }
        }
        if (warnings.isNotEmpty()) {
            Label(warnings.joinToString(" · ") { "${it.name} ${it.value}" }, 10, Color(0xFFA4540B))
        }
    }
}

