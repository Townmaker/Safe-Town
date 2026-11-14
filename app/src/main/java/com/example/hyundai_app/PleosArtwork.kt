package com.example.hyundai_app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun polygon(points: List<Offset>) = Path().apply {
    moveTo(points.first().x, points.first().y)
    points.drop(1).forEach { lineTo(it.x, it.y) }
    close()
}

private data class ScenePoint(val x: Float, val y: Float)
private data class House(val point: ScenePoint, val size: Float)

@Composable
internal fun VillageArt(stage: Int, modifier: Modifier, onTap: () -> Unit) {
    var rotation by remember { mutableStateOf(-.18f) }
    var zoom by remember { mutableStateOf(1f) }
    val tap by rememberUpdatedState(onTap)
    val houses = remember {
        listOf(House(ScenePoint(-.54f, -.32f), .13f), House(ScenePoint(.02f, -.52f), .18f),
            House(ScenePoint(.54f, -.24f), .15f), House(ScenePoint(-.63f, .14f), .12f),
            House(ScenePoint(.31f, .26f), .15f), House(ScenePoint(-.10f, .54f), .13f),
            House(ScenePoint(.65f, .40f), .11f))
    }
    val trees = remember {
        listOf(ScenePoint(-.58f, -.48f), ScenePoint(.20f, -.70f), ScenePoint(.68f, -.16f),
            ScenePoint(-.70f, .24f), ScenePoint(-.32f, .37f), ScenePoint(.13f, .62f), ScenePoint(.76f, .30f),
            ScenePoint(.43f, -.52f), ScenePoint(-.28f, -.57f), ScenePoint(-.60f, .54f))
    }
    Canvas(modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, scale, turn ->
            rotation += pan.x / 310f + turn / 180f
            zoom = (zoom * scale).coerceIn(.70f, 1.55f)
        }
    }.pointerInput(Unit) { detectTapGestures { tap() } }) {
        val center = Offset(size.width * .505f, size.height * .75f)
        val r = minOf(size.width * .356f, size.height * .69f) * zoom
        val ry = r * .57f
        val future = stage >= 7
        val land = if (future) Color(0xFF578D8B) else Color(0xFF75AF75)

        fun rotatePoint(p: ScenePoint) = ScenePoint(p.x * cos(rotation) - p.y * sin(rotation),
            p.x * sin(rotation) + p.y * cos(rotation))
        fun project(p: ScenePoint) = rotatePoint(p).let { center + Offset(it.x * r, it.y * ry) }
        fun ground(radius: Float, color: Color, y: Float = 0f) {
            drawOval(color, center + Offset(-r * radius, -ry * radius + y), Size(2 * r * radius, 2 * ry * radius))
        }

        if (stage < 10) {
            cloud(Offset(size.width * .64f, size.height * .08f), r * .16f)
            cloud(Offset(size.width * .45f, size.height * .015f), r * .085f)
            cloud(Offset(size.width * .75f, size.height * .33f), r * .048f)
        }
        ground(1.31f, Color(0xFFBAE5E5))
        ground(1.14f, Color(0xFFF4F2D9), r * .025f)
        ground(1.12f, Color(0xFFF7F3DB))
        ground(1f, land, r * .02f)
        ground(.99f, land)
        val pathWidth = r * .115f
        val trail = listOf(ScenePoint(-.68f,.60f), ScenePoint(-.34f,.38f), ScenePoint(-.06f,.04f),
            ScenePoint(.10f,-.14f), ScenePoint(.46f,-.34f), ScenePoint(.73f,-.45f))
        trail.zipWithNext().forEach { (a, b) ->
            drawLine(Color(0xFFB2A374), project(a) + Offset(0f, 4f), project(b) + Offset(0f, 4f), pathWidth, StrokeCap.Square)
            drawLine(Color(0xFFDDC996), project(a), project(b), pathWidth, StrokeCap.Square)
        }

        val fence = List(33) { i ->
            val angle = i * (2 * PI / 33)
            ScenePoint((cos(angle) * .87).toFloat(), (sin(angle) * .87).toFloat())
        }.filter { it.x < -.24f || it.y > .36f }
        val objects = houses.map { Triple(it.point, 0, it.size) } +
            trees.map { Triple(it, 1, .06f) } + fence.map { Triple(it, 2, .012f) }
        objects.sortedBy { rotatePoint(it.first).y }.forEach { (point, kind, scale) ->
            val base = project(point)
            when (kind) {
                0 -> house(base, r * scale * 1.15f, stage)
                1 -> tree(base, r * scale, future)
                else -> post(base, r * scale)
            }
        }
    }
}

private fun DrawScope.cloud(center: Offset, radius: Float) {
    listOf(Offset(-.78f,.04f) to .62f, Offset(-.38f,-.26f) to .86f, Offset(.18f,.13f) to .94f,
        Offset(.80f,.03f) to .68f).forEach { (shift, amount) ->
        val p = center + shift * radius
        val r = radius * amount
        drawCircle(Brush.radialGradient(listOf(Color(0xFFF9FFFF), Color(0xFFB9D8C6)), p - Offset(r*.25f,r*.33f), r*1.25f),
            r, p, alpha = .89f)
    }
}

private fun DrawScope.house(base: Offset, r: Float, stage: Int) {
    fun p(x: Float, y: Float) = base + Offset(x*r, y*r)
    val modern = stage >= 3
    val tower = if (stage >= 7) 1.6f else 1f
    val left = if (modern) Color(0xFFCED1B8) else Color(0xFFAC873D)
    val right = if (modern) Color(0xFF8CABA4) else Color(0xFF80652A)
    drawOval(Color(0x55405F42), p(-.75f,-.11f), Size(r*2.05f,r*.55f))
    drawPath(polygon(listOf(p(-.72f,-.13f),p(-.68f,-.86f*tower),p(.18f,-.71f*tower),p(.18f,.04f))), left)
    drawPath(polygon(listOf(p(.18f,.04f),p(.18f,-.71f*tower),p(.75f,-.95f*tower),p(.70f,-.22f))), right)
    drawPath(polygon(listOf(p(-.22f,-.02f),p(-.22f,-.51f),p(.06f,-.46f),p(.06f,.02f))), Color(0xFF594B2D))
    if (modern) {
        drawPath(polygon(listOf(p(-.76f,-.85f*tower),p(-.19f,-1.12f*tower),p(.79f,-.96f*tower),p(.18f,-.70f*tower))), Color(0xFF4D8285))
        repeat(if (stage >= 7) 4 else 2) { i ->
            drawLine(Color(0xFF457785), p(-.57f,-.37f-i*.21f),p(-.34f,-.33f-i*.21f),r*.10f)
        }
    } else {
        val apex = p(-.13f,-1.68f)
        val rim = listOf(p(-.92f,-.82f), p(-.66f,-.66f), p(.13f,-.55f), p(.80f,-.78f), p(.89f,-.93f))
        val roof = listOf(Color(0xFFBF9550),Color(0xFFC6A05B),Color(0xFFAA813C),Color(0xFF957239))
        rim.zipWithNext().forEachIndexed { i, (a,b) -> drawPath(polygon(listOf(apex,a,b)), roof[i]) }
    }
}

private fun DrawScope.tree(base: Offset, r: Float, future: Boolean) {
    drawOval(Color(0x4F385C3D), base - Offset(.2f*r,.16f*r), Size(2.2f*r,.47f*r))
    drawLine(Color(0xFF806335), base, base - Offset(0f,r*1.12f), r*.34f, StrokeCap.Round)
    val top = base - Offset(0f,r*1.37f)
    val leaves = List(10) { i ->
        val a = i * PI / 5
        top + Offset((cos(a)*r).toFloat(), (sin(a)*r*.94).toFloat())
    }
    val colors = if (future) listOf(Color(0xFFB5F0DA), Color(0xFF509C9A)) else listOf(Color(0xFFD8EFAB), Color(0xFF8FCB76))
    drawPath(polygon(leaves), Brush.radialGradient(colors, top - Offset(r*.32f,r*.30f),r*1.38f))
}

private fun DrawScope.post(base: Offset, r: Float) {
    drawLine(Color(0x583B5433), base, base + Offset(r*4f,r*.4f), r*1.35f, StrokeCap.Round)
    drawLine(Color(0xFF96733C), base, base - Offset(r*.35f,r*4.6f), r*1.65f, StrokeCap.Round)
    drawCircle(Color(0xFFCBAB64), r*.78f, base - Offset(r*.35f,r*4.6f))
}

private val mapRoads = listOf(
    "M-40 130 220 180 420 70 760 120 970 35 1250 110",
    "M50 570 180 400 445 355 610 180 890 200 1190 85",
    "M80-30l70 280 230 140 80 250M570-20l90 260 240 150 70 250M1020-20 900 200l110 190-30 250",
)
private val mapRoute = "M120 590c55-105 110-175 186-189 98-19 153 24 239-53 78-70 56-154 158-180 75-19 135 35 203-12 52-36 78-98 98-176"

@Composable
internal fun RouteMap(zoom: Float, routeActive: Boolean, modifier: Modifier = Modifier) {
    val roads = remember { mapRoads.map { PathParser().parsePathString(it).toPath() } }
    val route = remember { PathParser().parsePathString(mapRoute).toPath() }
    val text = rememberTextMeasurer()
    Canvas(modifier) {
        drawRect(Color(0xFFDAE9E8))
        val factor = maxOf(size.width / 1200f, size.height / 600f) * zoom
        val x = (size.width - 1200 * factor) / 2
        val y = (size.height - 600 * factor) / 2
        clipRect {
            withTransform({ translate(x,y); scale(factor,factor,Offset.Zero) }) {
                for (i in 0..15) drawLine(Color(0xFFC0D5D6), Offset(i*80f,0f), Offset(i*80f,600f), 1.5f)
                for (i in 0..8) drawLine(Color(0xFFC0D5D6), Offset(0f,i*80f), Offset(1200f,i*80f), 1.5f)
                roads.forEachIndexed { i, road ->
                    drawPath(road, Color(0xFFB1C8C7), style = Stroke(22f))
                    if (i < 2) drawPath(road, Color(0xFFDAE9E8), style = Stroke(3f))
                }
                val blue = if (routeActive) Color(0xFF399CFC) else Color(0xFF48D7EF)
                drawPath(route, blue.copy(alpha = .15f), style = Stroke(36f, cap = StrokeCap.Round))
                drawPath(route, blue.copy(alpha = .30f), style = Stroke(24f, cap = StrokeCap.Round))
                drawPath(route, blue, style = Stroke(14f, cap = StrokeCap.Round))
                drawPath(route, Color(0xFFBAF8FF), style = Stroke(3f, cap = StrokeCap.Round))
                listOf(Triple("새봄로",210f,130f), Triple("푸른대로",755f,75f),
                    Triple("한빛공원",935f,445f), Triple("중앙마을",345f,505f)).forEach { (name, px, py) ->
                    drawText(text, name, Offset(px,py), style = TextStyle(color = Muted, fontSize = 17.sp))
                }
                drawCircle(Navy, 31f, Offset(715f,160f))
                drawCircle(Color(0xFF75E8F2),31f,Offset(715f,160f), style = Stroke(3f))
                drawPath(polygon(listOf(Offset(703f,170f), Offset(715f,140f), Offset(727f,170f), Offset(715f,165f))), Color(0xFF70E5F4))
                drawCircle(Color.White, 25f, Offset(546f,345f))
                drawCircle(Coral, 25f, Offset(546f,345f), style = Stroke(6f))
                drawText(text, "30", Offset(532f,329f), style = TextStyle(color = Navy, fontSize = 23.sp,fontWeight = FontWeight.Bold))
                val triangle = polygon(listOf(Offset(900f,126f), Offset(928f,176f), Offset(872f,176f)))
                drawPath(triangle, Color.White)
                drawPath(triangle, Coral, style = Stroke(6f))
                drawLine(Navy,Offset(900f,141f),Offset(900f,158f),4f)
                drawCircle(Navy,2.8f,Offset(900f,165f))
                drawCircle(Navy,24f,Offset(305f,399f))
                drawCircle(Amber,24f,Offset(305f,399f), style = Stroke(4f))
                drawRect(Amber, Offset(297f,391f), Size(16f,15f), style = Stroke(4f))
                if (routeActive) {
                    drawCircle(Coral, 19f, Offset(1001f,32f))
                    drawCircle(Color.White, 7f, Offset(1001f,32f))
                }
            }
        }
    }
}

@Composable
internal fun VehicleArt(group: Int, modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        fun p(x: Float,y: Float) = Offset(w*x,h*y)
        fun face(color: Long, vararg points: Pair<Float,Float>) {
            drawPath(polygon(points.map { p(it.first,it.second) }),Color(color))
        }
        drawRect(Brush.verticalGradient(listOf(Paper, Color(0xFFE4EEEA))))
        drawOval(Color(0xFF7D969E),p(-.15f,.45f),Size(w*1.4f,h*.80f))
        drawOval(Color(0x50405861),p(.25f,.66f),Size(w*.68f,h*.28f))
        listOf(.69f to .75f,.90f to .53f).forEach { (x,y) ->
            drawOval(if(group == 1) Mint else Color(0xFF142A34),p(x-.05f,y-.14f),Size(w*.11f,h*.29f))
            drawOval(if(group == 2) Coral else Color(0xFF2D4D5B),p(x-.028f,y-.095f),Size(w*.068f,h*.19f))
            drawOval(Navy,p(x-.012f,y-.055f),Size(w*.036f,h*.11f))
        }
        face(0xFF29596A,.20f to .45f,.51f to .59f,.51f to .86f,.22f to .68f)
        face(0xFF214C5B,.51f to .59f,.94f to .32f,.91f to .47f,.74f to .62f,.65f to .80f,.51f to .86f)
        face(if(group==0) 0xFF79DCC6 else 0xFF53939E,.20f to .45f,.57f to .26f,.94f to .32f,.51f to .59f)
        face(0xFF122F43,.37f to .32f,.50f to .13f,.78f to .09f,.88f to .15f,.91f to .29f,.67f to .46f)
        face(0xFF0A202D,.66f to .46f,.71f to .23f,.88f to .15f,.91f to .29f)
        face(0xFF1D4257,.41f to .31f,.52f to .16f,.71f to .23f,.66f to .42f)
        face(0xFF102E3B,.24f to .57f,.47f to .69f,.47f to .81f,.24f to .66f)
        face(0xFFF3FFFF,.22f to .52f,.27f to .55f,.27f to .62f,.23f to .59f)
        face(0xFFF3FFFF,.43f to .63f,.51f to .67f,.51f to .72f,.43f to .69f)
        face(0xFF819B9B,.21f to .68f,.51f to .84f,.51f to .90f,.21f to .74f)
        if (group == 3) {
            drawRoundRect(Color(0xFFCFD9DA),p(.45f,.40f),Size(w*.08f,h*.12f),androidx.compose.ui.geometry.CornerRadius(w*.018f))
        }
    }
}

