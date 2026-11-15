package com.example.hyundai_app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight

@Composable
internal fun AppListRow(
    icon: String, title: String, detail: String, modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 58.u).padding(horizontal = 9.u, vertical = 7.u),
            horizontalArrangement = Arrangement.spacedBy(12.u), verticalAlignment = Alignment.CenterVertically) {
            AppIcon(icon, Modifier.size(18.u), Muted)
            Column(Modifier.weight(1f)) {
                Label(title, 13, weight = FontWeight.Medium)
                Label(detail, 11, Color(0xFF55AA9E))
            }
            AppIcon("chevron", Modifier.size(17.u), Muted)
        }
        HorizontalDivider(color = Line)
    }
}

@Composable
internal fun HistoryPage(state: PleosDriveState, modifier: Modifier, wide: Boolean) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val records = state.records
    val record = records[selected.coerceIn(records.indices)]
    Column(modifier) {
        UtilityHeader("주행히스토리")
        if (wide) Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 15.u, vertical = 17.u),
            horizontalArrangement = Arrangement.spacedBy(12.u)) {
            HistoryList(records, selected, Modifier.weight(2f).fillMaxHeight(), scrollable = true) { selected = it }
            HistoryDetail(record, Modifier.weight(3f).fillMaxHeight(), scrollable = true)
        } else Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(12.u),
            verticalArrangement = Arrangement.spacedBy(12.u)) {
            HistoryList(records, selected, Modifier.fillMaxWidth(), scrollable = false) { selected = it }
            HistoryDetail(record, Modifier.fillMaxWidth(), scrollable = false)
        }
    }
}

@Composable
private fun HistoryList(
    records: List<DemoRecord>, selected: Int, modifier: Modifier, scrollable: Boolean, select: (Int) -> Unit,
) {
    Panel(modifier) {
        val scroll = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(Modifier.fillMaxWidth().then(scroll).padding(19.u),
            verticalArrangement = Arrangement.spacedBy(7.u)) {
            Label("최근 30일", 11, Teal)
            Label("주행 기록", 27)
            Label("총 184 km · 평균 안전점수 92점", 11, Muted)
            Spacer(Modifier.height(9.u))
            records.forEachIndexed { index, record ->
                val active = index == selected
                Panel(Modifier.fillMaxWidth().heightIn(min = 70.u).clickable { select(index) },
                    color = if (active) Color(0xFF759496) else Wash, radius = 12) {
                    Row(Modifier.fillMaxWidth().padding(11.u), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.u)) {
                        Box(Modifier.size(39.u).clip(CircleShape).background(Mint), contentAlignment = Alignment.Center) {
                            Label(record.score.toString(), 13, weight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f)) {
                            Label(record.date, 10, if (active) Color.White else Teal)
                            Label(record.route, 13, if (active) Color.White else Ink, weight = FontWeight.Medium, lines = 1)
                            Label("${record.duration} · ${record.distance} · +${record.xp} XP", 9,
                                if (active) Color(0xFFE6F6F1) else Muted, lines = 1)
                        }
                        AppIcon("chevron", Modifier.size(17.u), if (active) Color.White else Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryDetail(record: DemoRecord, modifier: Modifier, scrollable: Boolean) {
    Panel(modifier) {
        val scroll = if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(Modifier.fillMaxWidth().then(scroll).padding(22.u),
            verticalArrangement = Arrangement.spacedBy(14.u)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Label(record.date, 11, Teal)
                    Label(record.route, 22, lines = 1)
                }
                Label("${record.score}", 35, weight = FontWeight.Bold)
                Label("점", 11, Muted)
            }
            TripMetrics(record)
            Label("점수 및 경험치 변동 · 시연", 11, Muted)
            ScoreRow("30", "어린이 보호구역 안전 통과", "제한속도를 안정적으로 유지했어요", "+20 XP", false)
            if (record.score < 95) ScoreRow("●", "황색 신호 진입", "정지선 전 감속이 늦었어요", "-8점 · -15 XP", true)
            else ScoreRow("✓", "안전 주행 유지", "급가속·급제동 없이 주행했어요", "+35 XP", false)
        }
    }
}

@Composable
internal fun ProfilePage(state: PleosDriveState, modifier: Modifier, wide: Boolean) {
    var detail by rememberSaveable { mutableStateOf("") }
    var selectedPart by rememberSaveable { mutableIntStateOf(3) }
    Column(modifier) {
        UtilityHeader("마이페이지")
        if (wide) Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 15.u, vertical = 17.u),
            horizontalArrangement = Arrangement.spacedBy(13.u)) {
            ProfileMain(state, Modifier.weight(1.94f).fillMaxHeight()) { detail = it }
            VehiclePanel(selectedPart, state.batteryLabel, state.inspection?.odometerKm, Modifier.weight(1f).fillMaxHeight()) { selectedPart = it }
        } else Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(12.u),
            verticalArrangement = Arrangement.spacedBy(12.u)) {
            ProfileMain(state, Modifier.fillMaxWidth().heightIn(max = 480.u)) { detail = it }
            VehiclePanel(selectedPart, state.batteryLabel, state.inspection?.odometerKm, Modifier.fillMaxWidth().height(460.u)) { selectedPart = it }
        }
    }
    if (detail.isNotBlank()) ProfileDialog(detail, state, wide) { detail = "" }
}

@Composable
private fun ProfileMain(state: PleosDriveState, modifier: Modifier, select: (String) -> Unit) {
    Panel(modifier) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(21.u)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 96.u), horizontalArrangement = Arrangement.spacedBy(13.u),
                verticalAlignment = Alignment.CenterVertically) {
                IconTile("user", 56, Color(0xFF68CFC2))
                Column(Modifier.weight(2f)) {
                    Label("골드 드라이버", 11, Teal)
                    Label(state.driverName, 22, lines = 1)
                    Label("마을 레벨 ${state.villageLevel} · ${if (state.toggles[7]) "프로필 공개" else "프로필 비공개"}", 11, Muted, lines = 1)
                }
                listOf("2,277" to "누적 안전운전 km · 시연", "94" to "안전운전 점수 · 시연", "Lv. ${state.villageLevel}" to "마을 레벨").forEach { (value, caption) ->
                    Column(Modifier.weight(.78f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Label(value, 17, weight = FontWeight.Bold, lines = 1)
                        Label(caption, 10, Teal, lines = 1)
                    }
                }
            }
            HorizontalDivider(color = Line)
            profileMenus.forEach { (icon, title, subtitle) -> AppListRow(icon, title, subtitle) { select(title) } }
        }
    }
}

@Composable
private fun VehiclePanel(selected: Int, battery: String, odometerKm: Float?, modifier: Modifier, choose: (Int) -> Unit) {
    Panel(modifier) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(19.u),
            verticalArrangement = Arrangement.spacedBy(10.u)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.u)) {
                IconTile("car", 42, Color(0xFF65CCC0))
                Column(Modifier.weight(1f)) {
                    Label("차량 모델 · 시연", 9, Muted)
                    Label("차차 EV 2025", 14, weight = FontWeight.Medium)
                }
                Label("EV 충전량 $battery", 11, Teal)
            }
            odometerKm?.let { Label("차량 누적 주행거리 %,.1f km".format(java.util.Locale.KOREA, it), 11, Muted) }
            VehicleModel(vehicleParts[selected].group, Modifier.fillMaxWidth().height(218.u))
            Label("차량 부품 상태 · 시연", 12, weight = FontWeight.Bold)
            vehicleParts.forEachIndexed { index, part ->
                Column(Modifier.fillMaxWidth().heightIn(min = 48.u).clip(RoundedCornerShape(8.u))
                    .background(if (selected == index) Color(0xFFE8F6F2) else Color.Transparent)
                    .clickable { choose(index) }.padding(8.u), verticalArrangement = Arrangement.spacedBy(5.u)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Label(part.name, 16, modifier = Modifier.weight(1f))
                        Label(part.detail, 9, if (part.percent < 30) Color(0xFF9B530E) else Ink, weight = FontWeight.Bold)
                    }
                    Meter(part.percent / 100f, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ProfileDialog(title: String, state: PleosDriveState, wide: Boolean, close: () -> Unit) {
    val icon = profileMenus.first { it.second == title }.first
    var name by rememberSaveable { mutableStateOf(state.driverName) }
    AppDialog(title, "마이페이지", icon, close) {
        when (title) {
            "내 프로필" -> {
                OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Label("닉네임", 13) }, singleLine = true)
                ToggleRow("프로필 공개", "다른 이용자에게 주행 기록 공개", state.toggles[7]) { state.toggle(7) }
                ActionButton("저장", Modifier.fillMaxWidth()) { state.rename(name); close() }
            }
            "주행 기록" -> {
                state.records.take(3).forEach { Label("${it.route} · ${it.score}점", 14) }
                ActionButton("전체 주행 기록 보기", Modifier.fillMaxWidth()) { state.navigate(DrivePage.History); close() }
            }
            "업적 및 배지" -> BadgeGrid(wide)
            "마을 성장 기록" -> villageStages.take((state.earnedXp / 800 + 1).coerceIn(1, villageStages.size))
                .forEachIndexed { index, label -> Label("Lv. ${index + 1}   $label", 15, Teal) }
            "친구 및 커뮤니티" -> friends.forEachIndexed { index, (friend, level) ->
                AppListRow("user", friend, villageStages[level]) {
                    state.visitFriend(index)
                    close()
                }
            }
        }
    }
}

@Composable
internal fun SettingsPage(state: PleosDriveState, modifier: Modifier) {
    var detail by rememberSaveable { mutableStateOf("") }
    Column(modifier) {
        UtilityHeader("설정")
        Panel(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 15.u, vertical = 12.u)) {
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(28.u)) {
                Label("차량과 앱을 나에게 맞게", 11, Teal)
                Spacer(Modifier.height(9.u))
                Label("설정", 27)
                Spacer(Modifier.height(23.u))
                Label("주행 환경", 14, weight = FontWeight.Medium)
                Spacer(Modifier.height(10.u))
                preferences.take(7).forEachIndexed { index, (title, description) ->
                    ToggleRow(title, description, state.toggles[index]) { state.toggle(index) }
                }

                Spacer(Modifier.height(13.u))
                Label("개인정보 및 계정", 15, weight = FontWeight.Bold)
                ToggleRow(preferences[7].first, preferences[7].second, state.toggles[7]) { state.toggle(7) }
                settingDetails.keys.forEach { title ->
                    AppListRow("settings", title, "") { detail = title }
                }
            }
        }
    }
    if (detail.isNotBlank()) SettingsDialog(detail, state) { detail = "" }
}

@Composable
private fun ToggleRow(title: String, description: String, checked: Boolean, toggle: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = 63.u)
            .toggleable(checked, role = Role.Switch, onValueChange = { toggle() })
            .padding(horizontal = 8.u, vertical = 7.u), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Label(title, 13, weight = FontWeight.Medium)
                if (description.isNotBlank()) Label(description, 11, Color(0xFF55AA9E))
            }
            Box(Modifier.size(34.u, 19.u).clip(CircleShape).background(if (checked) Color(0xFF316F67) else Color(0xFFC9D9D8))
                .padding(3.u), contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart) {
                Box(Modifier.size(13.u).background(if (checked) Color(0xFF8FE4D6) else Color(0xFF799195), CircleShape))
            }
        }
        HorizontalDivider(color = Line)
    }
}

@Composable
private fun SettingsDialog(title: String, state: PleosDriveState, close: () -> Unit) {
    val entries = settingDetails.getValue(title)
    var choice by rememberSaveable(title) { mutableStateOf("") }


    AppDialog(title, "설정", "settings", close) {
        when (title) {
            "앱 정보 / 버전" -> {
                Label("Safe Town", 21, weight = FontWeight.Bold)
                Label("안전운전으로 함께 성장하는 마을 · 1.0", 14, Muted)
            }
            "운전자 프로필 전환" -> entries.forEach { profile ->
                AppListRow("user", profile, if (state.driverName == profile) "사용 중" else "개인 설정 불러오기") {
                    state.rename(profile)
                    close()
                }
            }
            "화면 밝기 및 주·야간 모드" -> {
                Label("화면 밝기 · 데모", 15)
                Slider(state.displayBrightness, state::saveBrightness, Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.u)) {
                    entries.forEach { mode ->
                        ActionButton(mode, Modifier.weight(1f), bg = if (state.displayMode == mode) Teal else Muted) { state.saveDisplayMode(mode) }
                    }
                }
            }
            "고객센터 / FAQ" -> entries.forEach { question ->
                AppListRow("message", question, if (choice == question) "차량 연결 및 앱 설정을 확인해 주세요." else "") { choice = question }
            }
            else -> entries.forEach { item ->
                ToggleRow(item, "데모 설정", state.isDetailEnabled(title, item)) {
                    state.toggleDetail(title, item)
                }
            }
        }
    }
}

@Composable
private fun TripMetrics(record: DemoRecord, dark: Boolean = false) {
    val color = if (dark) Color(0xFF29434B) else Line
    HorizontalDivider(color = color)
    Row(Modifier.fillMaxWidth().padding(vertical = 14.u), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf((if (record.distanceFromVehicle) "차량 주행거리" else "주행 거리") to record.distance,
            "세션 시간" to record.duration,
            (if (dark) "최종 경험치" else "획득 경험치") to "+${record.xp} XP").forEach { (caption, value) ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Label(caption, 10, if (dark) Color(0xFF8CAAB4) else Teal)
                Spacer(Modifier.height(5.u))
                Label(value, 14, if (dark) Color.White else Ink, weight = FontWeight.Bold)
            }
        }
    }
    HorizontalDivider(color = color)
}

@Composable
private fun ScoreRow(sign: String, title: String, detail: String, points: String, warning: Boolean) {
    Panel(Modifier.fillMaxWidth().heightIn(min = 56.u), color = Wash, radius = 11) {
        Row(Modifier.fillMaxWidth().padding(11.u), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.u)) {
            Box(Modifier.size(35.u).clip(CircleShape).background(Navy)
                .border(2.u, if (warning) Color(0xFF627B86) else Coral, CircleShape), contentAlignment = Alignment.Center) {
                Label(sign, 13, if (warning) Amber else Color.White, weight = FontWeight.Bold)
            }
            Column(Modifier.weight(1f)) {
                Label(title, 12, weight = FontWeight.Medium)
                Label(detail, 10, Muted)
            }
            Label(points, 10, if (warning) Color(0xFFB73D4B) else Teal, weight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun ResultPage(record: DemoRecord, modifier: Modifier, wide: Boolean, home: () -> Unit) {
    BoxWithConstraints(modifier.background(
        Brush.radialGradient(listOf(Color(0xFF16323A), Color(0xFF0B171E)))
    ), contentAlignment = Alignment.Center) {
        Panel(Modifier.width(minOf(maxWidth - 24.u, 920.u)).heightIn(max = maxHeight - 18.u),
            color = Color(0xFF102731), edge = Color(0xFF2D4B52), radius = 25) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(25.u),
                verticalArrangement = Arrangement.spacedBy(15.u)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.u)) {
                    IconTile("shield", 58, Navy, Mint)
                    Column(Modifier.weight(1f)) {
                        Label("주행 세션 종료 · 점수와 보상은 시연용", 11, Mint)
                        Label("오늘의 주행 결과", 27, Color.White, weight = FontWeight.Medium)
                    }
                    Label("${record.score}", 43, Color.White, weight = FontWeight.Bold)
                    Label("점", 11, Color(0xFF9DB6BA))
                }
                TripMetrics(record, dark = true)
                if (wide) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.u)) {
                    Rewards(Modifier.weight(2.25f))
                    BadgeReward(Modifier.weight(1f).heightIn(min = 215.u))
                } else {
                    Rewards(Modifier.fillMaxWidth())
                    BadgeReward(Modifier.fillMaxWidth())
                }
                ActionButton("보상 받고 마을로 돌아가기  ›", Modifier.fillMaxWidth(), bg = Color(0xFF2D6865), minHeight = 47, onClick = home)
            }
        }
    }
}

@Composable
private fun Rewards(modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(7.u)) {
        Label("경험치 및 점수 내역 · 시연", 13, Color.White)
        ScoreRow("30", "어린이 보호구역 안전 통과", "제한속도를 잘 지켰어요", "+20 XP", false)
        ScoreRow("●", "황색 신호 진입", "정지선 전 감속이 늦었어요", "-8점 · -15 XP", true)
        ScoreRow("✓", "급가속·급제동 없이 주행", "편안한 주행을 유지했어요", "+35 XP", false)
    }
}

@Composable
private fun BadgeReward(modifier: Modifier) {
    Panel(modifier, color = Color(0xFF11262E), edge = Color(0xFF4C4F3D), radius = 16) {
        Column(Modifier.fillMaxWidth().padding(16.u), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.u)) {
            Label("지역 컬렉션", 10, Color(0xFFF1C473))
            Label("새로운 배지 획득", 14, Color.White)
            Box(Modifier.size(74.u).clip(CircleShape).background(
                Brush.linearGradient(listOf(Color(0xFFE4CA71), Mint))
            ).border(4.u, Color(0xFFBD9441), CircleShape), contentAlignment = Alignment.Center) {
                AppIcon("sparkles", Modifier.size(40.u), Color(0xFF594418))
            }
            Label("서울 한강 드라이버", 13, Color.White, weight = FontWeight.Bold)
            Label("한강대교 지역을 처음 방문했어요", 9, Color(0xFFA7C1C4))
            Label("COLLECTION 12 / 48", 9, Color(0xFFF1C473))
        }
    }
}

