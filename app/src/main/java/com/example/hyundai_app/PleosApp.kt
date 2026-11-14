package com.example.hyundai_app

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlin.math.min

internal val Ink = Color(0xFF173E47)
internal val Muted = Color(0xFF65868D)
internal val Teal = Color(0xFF218579)
internal val Mint = Color(0xFF79DCC6)
internal val Line = Color(0xFFDCEAE7)
internal val Paper = Color(0xFFF8FDFB)
internal val Wash = Color(0xFFF0F7F5)
internal val Navy = Color(0xFF182F3C)
internal val Coral = Color(0xFFEC5767)
internal val Amber = Color(0xFFECA62D)
private val AppUnit = compositionLocalOf { 1f }
internal val Int.u @Composable get() = (this * AppUnit.current).dp
internal val Int.pt @Composable get() = (this * AppUnit.current).sp

@Composable
fun PleosApp() {
    val context = LocalContext.current
    val state = remember(context) { PleosDriveState(context.applicationContext) }
    val automotive = remember(context) { context.isAutomotive() }
    val navigationService = remember(context, automotive) {
        if (automotive) PleosNavigationService(context.applicationContext) else DemoNavigationService()
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var restricted by remember(automotive) { mutableStateOf(automotive) }
    DisposableEffect(context, automotive) {
        val monitor = if (automotive) {
            runCatching { DrivingRestrictions(context) { restricted = it } }.getOrNull()
        } else null
        onDispose { monitor?.close() }
    }
    LaunchedEffect(context, automotive, lifecycle) {
        if (automotive) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            observeBattery(context) { state.batteryLevel = it }
        }
    }
    var checkShown by rememberSaveable { mutableStateOf(false) }
    var showCheck by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.isDriving) {
        while (state.isDriving) {
            delay(1000)
            state.elapsed++
        }
    }
    LaunchedEffect(state.page, state.toggles[5], restricted) {
        if (!restricted && !checkShown && state.page == DrivePage.Village && state.toggles[5]) {
            delay(900)
            checkShown = true
            showCheck = true
        }
    }
    BackHandler(state.page != DrivePage.Village) { state.navigate(DrivePage.Village) }

    BoxWithConstraints(Modifier.fillMaxSize().background(Paper).windowInsetsPadding(WindowInsets.safeDrawing)) {
        val scale = min(maxWidth.value / 1280f, maxHeight.value / 720f).coerceIn(.65f, 2.25f)
        val wide = maxWidth.value >= 700f * scale && maxWidth.value >= maxHeight.value * 1.38f
        CompositionLocalProvider(AppUnit provides scale) {
            Column(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFFDEEFEC), Color(0xFFF5FAF8), Color(0xFFE4F0EF)))
                )
            ) {
                val content = Modifier.weight(1f).fillMaxWidth()
                if (restricted) {
                    Box(content, contentAlignment = Alignment.Center) {
                        Label("정차 후 이용할 수 있어요", 20, weight = FontWeight.Bold)
                    }
                } else when (state.page) {
                    DrivePage.Village -> VillagePage(state, content, wide)
                    DrivePage.Driving -> DrivingPage(state, navigationService, content, wide)
                    DrivePage.History -> HistoryPage(state, content, wide)
                    DrivePage.Profile -> ProfilePage(state, content, wide)
                    DrivePage.Settings -> SettingsPage(state, content, wide)
                    DrivePage.Result -> state.result?.let { ResultPage(it, content, wide) { state.navigate(DrivePage.Village) } }
                }
                if (!restricted && state.page != DrivePage.Result) BottomNavigation(state)
            }
            if (!restricted && showCheck && state.page == DrivePage.Village) {
                VehicleCheck(wide, state.batteryLevel) { showCheck = false }
            }
        }
    }
}

@Composable
internal fun Label(
    text: String, size: Int, color: Color = Ink,
    modifier: Modifier = Modifier, weight: FontWeight = FontWeight.Normal, lines: Int = Int.MAX_VALUE,
) = Text(text, modifier, color, fontSize = size.pt, fontWeight = weight, lineHeight = (size + 4).pt,
    maxLines = lines, overflow = TextOverflow.Ellipsis)

@Composable
internal fun Panel(
    modifier: Modifier = Modifier,
    color: Color = Paper,
    edge: Color = Line,
    radius: Int = 16,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius.u)
    Box(modifier.clip(shape).background(color).border(1.dp, edge, shape), content = content)
}

@Composable
internal fun IconTile(name: String, size: Int = 40, color: Color = Teal, background: Color = Color(0xFFE3F4EF)) {
    Box(Modifier.size(size.u).clip(RoundedCornerShape(12.u)).background(background), contentAlignment = Alignment.Center) {
        AppIcon(name, Modifier.size((size / 2).u), color)
    }
}

@Composable
internal fun AppIcon(name: String, modifier: Modifier = Modifier.size(24.dp), color: Color = Teal) {
    val paths = remember(name) { (iconPaths[name] ?: iconPaths.getValue("chevron")).map { PathParser().parsePathString(it).toPath() } }
    Canvas(modifier) {
        val factor = size.minDimension / 24f
        scale(factor, factor, pivot = Offset.Zero) {
            paths.forEach { drawPath(it, color, style = Stroke(1.8f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
        }
    }
}

private val iconPaths = mapOf(
    "arrow" to listOf("m5 12 14-7-5.5 14-2.7-5.8L5 12Z"),
    "calendar" to listOf("M6 3v3M18 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14H4V6a1 1 0 0 1 1-1Z", "M8 13h2M14 13h2M8 17h2"),
    "car" to listOf("m5 16-1.5-1.8L5.7 8h12.6l2.2 6.2L19 16H5Z", "m7.3 8 1.3-3h6.8l1.3 3M7 16v3M17 16v3M7.5 12h.1M16.5 12h.1"),
    "chevron" to listOf("m9 6 6 6-6 6"),
    "clock" to listOf("M12 3.5a8.5 8.5 0 1 0 .01 0", "M12 7v5l3.5 2"),
    "compass" to listOf("M12 3.5a8.5 8.5 0 1 0 .01 0", "m15.7 8.3-2.1 5.3-5.3 2.1 2.1-5.3 5.3-2.1Z"),
    "fuel" to listOf("M5 21V4h10v17M4 21h12M7 8h6M15 8h2l2 3v7a1.5 1.5 0 0 0 3 0v-6l-2-2"),
    "history" to listOf("M4 5v5h5M5.7 9.2A7.5 7.5 0 1 1 5 15M12 8v4l3 2"),
    "home" to listOf("m3 11 9-7 9 7", "M5 10v10h14V10M9 20v-6h6v6"),
    "location" to listOf("M19 10c0 5-7 11-7 11S5 15 5 10a7 7 0 1 1 14 0Z", "M12 7.8a2.2 2.2 0 1 0 .01 0"),
    "message" to listOf("M4 5h16v11H9l-5 4V5Z", "M8 10h.1M12 10h.1M16 10h.1"),
    "music" to listOf("M9 18V5l10-2v13M6 15a3 3 0 1 0 .01 0M16 13a3 3 0 1 0 .01 0"),
    "phone" to listOf("M7 3 4 5c0 8 7 15 15 15l2-3-5-3-2 2c-3-1-5-3-6-6l2-2-3-5Z"),
    "road" to listOf("m8 3-3 18M16 3l3 18M12 4v3M12 10v4M12 17v3"),
    "shield" to listOf("M12 3 5 6v5c0 4.5 2.8 8 7 10 4.2-2 7-5.5 7-10V6l-7-3Z", "m9 12 2 2 4-4"),
    "sparkles" to listOf("m12 3 1.3 3.7L17 8l-3.7 1.3L12 13l-1.3-3.7L7 8l3.7-1.3L12 3ZM19 14l.8 2.2L22 17l-2.2.8L19 20l-.8-2.2L16 17l2.2-.8L19 14ZM5 13l.8 2.2L8 16l-2.2.8L5 19l-.8-2.2L2 16l2.2-.8L5 13Z"),
    "settings" to listOf("M12 9a3 3 0 1 0 .01 0", "M10 3h4l.6 2.3 2 .9 2-.9 2.1 2.1-.9 2 .9 2L23 12v3l-2.3.6-.9 2 .9 2-2.1 2.1-2-.9-2 .9L14 24h-4l-.6-2.3-2-.9-2 .9-2.1-2.1.9-2-.9-2L1 15v-3l2.3-.6.9-2-.9-2 2.1-2.1 2 .9 2-.9Z"),
    "sun" to listOf("M12 8.5a3.5 3.5 0 1 0 .01 0", "M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"),
    "user" to listOf("M12 4a4 4 0 1 0 .01 0M4.5 21a7.5 7.5 0 0 1 15 0"),
    "users" to listOf("M9 5a3 3 0 1 0 .01 0M17 7.5a2.5 2.5 0 1 0 .01 0", "M3 20a6 6 0 0 1 12 0M14 16a5 5 0 0 1 7 4"),
    "close" to listOf("m6 6 12 12M6 18 18 6"),
)

@Composable
private fun BottomNavigation(state: PleosDriveState) {
    BoxWithConstraints(Modifier.fillMaxWidth().height(78.u).background(Color(0xFFF5FCF9))) {
        val showEnd = state.page == DrivePage.Driving && state.isDriving
        val compact = maxWidth < 820.u
        val bar = when {
            compact && showEnd -> Modifier.fillMaxWidth().padding(end = 114.u).align(Alignment.CenterStart)
            compact -> Modifier.fillMaxWidth().align(Alignment.CenterStart)
            showEnd -> Modifier.width(minOf(820.u, maxWidth - 240.u)).align(Alignment.Center)
            else -> Modifier.width(680.u).align(Alignment.Center)
        }
        Row(bar.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            DrivePage.entries.filter { it != DrivePage.Result }.forEach { item ->
                val active = state.page == item
                val tint = if (active) Teal else Muted
                Box(
                    Modifier.weight(1f).fillMaxHeight().clickable(role = Role.Tab) { state.navigate(item) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AppIcon(item.icon, Modifier.size(23.u), tint)
                        Spacer(Modifier.height(5.u))
                        Label(item.label, if (compact) 11 else 13, tint, weight = if (active) FontWeight.Bold else FontWeight.Medium, lines = 1)
                    }
                    if (active) Box(Modifier.align(Alignment.BottomCenter).width(38.u).height(3.u).background(Mint))
                }
            }
        }
        if (showEnd) {
            ActionButton("■  주행 종료", Modifier.align(Alignment.CenterEnd).padding(end = if (compact) 5.u else 24.u),
                bg = Color(0xFFC2A8A9), fg = Color.White, minHeight = 49, onClick = state::endDrive)
        }
    }
}

@Composable
internal fun UtilityHeader(title: String) {
    Row(Modifier.fillMaxWidth().height(70.u).background(Color(0xFFF6FCF9)).padding(horizontal = 30.u),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.u)) {
        IconTile("road", 38)
        Column(Modifier.weight(1f)) {
            Label("다함께 차차차", 16, weight = FontWeight.Bold)
            Label(title, 10, Muted)
        }
        Label("6월 12일 수요일 · 10:24", 11)
    }
}

@Composable
internal fun ActionButton(
    text: String, modifier: Modifier = Modifier, bg: Color = Teal,
    fg: Color = Color.White, minHeight: Int = 44, onClick: () -> Unit,
) {
    Box(
        modifier.heightIn(min = minHeight.u).clip(RoundedCornerShape(13.u)).background(bg)
            .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 13.u, vertical = 7.u),
        contentAlignment = Alignment.Center,
    ) { Label(text, 13, fg, weight = FontWeight.Bold, lines = 1) }
}

@Composable
internal fun Meter(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier.height(5.u).clip(RoundedCornerShape(6.u)).background(Line)) {
        Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().background(
            Brush.horizontalGradient(listOf(Color(0xFF5AC6A2), Color(0xFF75E3EB)))
        ))
    }
}

@Composable
internal fun AppDialog(title: String, subtitle: String, icon: String, close: () -> Unit,
    content: @Composable ColumnScope.() -> Unit) {
    Dialog(close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val window = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { window?.setDimAmount(.035f) }
        BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 14.u), contentAlignment = Alignment.Center) {
            val width = minOf(maxWidth, 610.u)
            Panel(Modifier.width(width).heightIn(max = 615.u), color = Color(0xFFF1FAF6), radius = 24) {
                Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(21.u),
                    verticalArrangement = Arrangement.spacedBy(10.u)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.u)) {
                        IconTile(icon, 44)
                        Column(Modifier.weight(1f)) {
                            Label(subtitle, 9, Teal, weight = FontWeight.Bold)
                            Label(title, 22, weight = FontWeight.Medium)
                        }
                        ActionButton("×", bg = Color.White, fg = Muted, minHeight = 36, onClick = close)
                    }
                    Spacer(Modifier.height(2.u))
                    content()
                }
            }
        }
    }
}

