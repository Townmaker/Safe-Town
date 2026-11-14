package com.example.hyundai_app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPInputStream
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
internal fun VillageModel(stage: Int, modifier: Modifier, onTap: () -> Unit) {
    var azimuth by remember { mutableFloatStateOf(.64f) }
    var pitch by remember { mutableFloatStateOf(.58f) }
    var distance by remember { mutableFloatStateOf(13f) }
    val tap by rememberUpdatedState(onTap)
    val camera = Camera(
        Point(sin(azimuth) * cos(pitch) * distance, sin(pitch) * distance, cos(azimuth) * cos(pitch) * distance),
        Point(0f, if (stage < 9) .8f else .1f, 0f),
        42f,
    )
    NativeScene("village-$stage", camera, modifier.pointerInput(Unit) {
        detectTransformGestures { _, pan, zoom, _ ->
            azimuth -= pan.x * .005f
            pitch = (pitch + pan.y * .003f).coerceIn(.25f, 1.05f)
            distance = (distance / zoom).coerceIn(8f, 18f)
        }
    }.pointerInput(Unit) { detectTapGestures { tap() } }) {
        VillageArt(stage, Modifier.matchParentSize(), onTap)
    }
}

@Composable
internal fun VehicleModel(group: Int, modifier: Modifier) {
    val eye = when (group) {
        0 -> Point(3.9f, 2.35f, 4.4f)
        1 -> Point(3.55f, 1.6f, 3.4f)
        2 -> Point(3.2f, 1.35f, 2.8f)
        else -> Point(4.15f, 2.55f, -2.8f)
    }
    val targetX = when (group) {
        0 -> .75f
        3 -> 1.3f
        else -> 0f
    }
    NativeScene("vehicle", Camera(eye, Point(targetX, .9f, 0f), 38f, group + 1), modifier) {
        VehicleArt(group, Modifier.matchParentSize())
    }
}

private data class Point(val x: Float, val y: Float, val z: Float) {
    operator fun minus(other: Point) = Point(x - other.x, y - other.y, z - other.z)
    fun dot(other: Point) = x * other.x + y * other.y + z * other.z
    fun cross(other: Point) = Point(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    fun unit(): Point {
        val length = sqrt(dot(this))
        return Point(x / length, y / length, z / length)
    }
}

private data class Camera(val eye: Point, val target: Point, val fov: Float, val highlight: Int = 0)
private data class Mesh(val positions: FloatArray, val lighting: FloatArray, val colors: IntArray, val tags: ByteArray)

@Composable
private fun NativeScene(
    model: String,
    camera: Camera,
    modifier: Modifier,
    fallback: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit,
) {
    val context = LocalContext.current.applicationContext
    var mesh by remember(model) { mutableStateOf<Mesh?>(null) }
    LaunchedEffect(model) {
        mesh = withContext(Dispatchers.IO) {
            runCatching { readMesh(context, model) }.onFailure {
                android.util.Log.w("PleosMesh", "Could not read $model", it)
            }.getOrNull()
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hasMotion = mesh?.tags?.any { it.toInt() in 11..15 } == true
    var seconds by remember(model) { mutableFloatStateOf(0f) }
    LaunchedEffect(lifecycle, model, hasMotion) {
        if (hasMotion) lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var lastFrame = android.os.SystemClock.elapsedRealtimeNanos()
            while (true) {
                delay(83)
                val now = android.os.SystemClock.elapsedRealtimeNanos()
                seconds += (now - lastFrame) / 1_000_000_000f
                lastFrame = now
            }
        }
    }
    Box(modifier.clipToBounds()) {
        val loaded = mesh
        if (loaded == null) {
            fallback()
        } else {
            val renderer = remember(loaded) { MeshProjection(loaded) }
            Canvas(Modifier.matchParentSize()) {
                renderer.draw(drawContext.canvas.nativeCanvas, camera, size.width, size.height, seconds)
            }
        }
    }
}

private fun readMesh(context: Context, model: String): Mesh {
    val bytes = context.assets.open("pleos-models/$model.meshdata").use { input ->
        GZIPInputStream(input).use { it.readBytes() }
    }
    val data = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val count = data.int
    require(count in 3..240000 && count % 3 == 0 && bytes.size.toLong() == 4L + count * 28L)
    val positions = FloatArray(count * 3)
    val lighting = FloatArray(count)
    val colors = IntArray(count)
    val tags = ByteArray(count)
    val light = (if (model == "vehicle") Point(4f, 8f, 6f) else Point(-6f, 11f, 7f)).unit()
    for (i in 0 until count) {
        positions[i * 3] = data.float
        positions[i * 3 + 1] = data.float
        positions[i * 3 + 2] = data.float
        lighting[i] = .69f + .42f * (light.x * data.float + light.y * data.float + light.z * data.float).coerceAtLeast(0f)
        val r = data.get().toInt() and 255
        val g = data.get().toInt() and 255
        val b = data.get().toInt() and 255
        colors[i] = (r shl 16) or (g shl 8) or b
        tags[i] = data.get()
    }
    return Mesh(positions, lighting, colors, tags)
}

private class MeshProjection(private val mesh: Mesh) {
    private val count = mesh.colors.size
    private val depth = FloatArray(count)
    private val points = FloatArray(count * 2)
    private val order = LongArray(count / 3)
    private val sortedPoints = FloatArray(count * 2)
    private val sortedColors = IntArray(count)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var cachedCamera: Camera? = null
    private var cachedWidth = 0f
    private var cachedHeight = 0f
    private var vertices = 0
    private var cachedSeconds = -1f

    fun draw(canvas: Canvas, camera: Camera, width: Float, height: Float, seconds: Float) {
        if (camera != cachedCamera || width != cachedWidth || height != cachedHeight || seconds != cachedSeconds) {
            project(camera, width, height, seconds)
            cachedCamera = camera
            cachedWidth = width
            cachedHeight = height
            cachedSeconds = seconds
        }
        if (vertices > 0) {
            canvas.drawVertices(Canvas.VertexMode.TRIANGLES, vertices * 2, sortedPoints, 0,
                null, 0, sortedColors, 0, null, 0, 0, paint)
        }
    }

    private fun project(camera: Camera, width: Float, height: Float, seconds: Float) {
        val forward = (camera.target - camera.eye).unit()
        val right = forward.cross(Point(0f, 1f, 0f)).unit()
        val up = right.cross(forward)
        val focal = (height * .5f) / tan(Math.toRadians(camera.fov.toDouble() * .5).toFloat())
        val time = seconds * .15f
        val cloudX1 = sin(seconds * .08f) * 1.4f
        val cloudX2 = (sin(seconds * .08f + 2f) - sin(2f)) * 1.4f
        for (i in 0 until count) {
            val p = i * 3
            val tag = mesh.tags[i].toInt()
            val cloudX = when (tag) {
                14 -> cloudX1
                15 -> cloudX2
                else -> 0f
            }
            val smokeY = if (tag in 11..13) (time + (tag - 11) * .24f) % .5f else 0f
            val x = mesh.positions[p] - camera.eye.x + cloudX
            val y = mesh.positions[p + 1] - camera.eye.y + smokeY
            val z = mesh.positions[p + 2] - camera.eye.z
            val d = forward.x * x + forward.y * y + forward.z * z
            depth[i] = d
            val scale = focal / d.coerceAtLeast(.1f)
            points[2 * i] = width * .5f + (right.x * x + right.y * y + right.z * z) * scale
            points[2 * i + 1] = height * .5f - (up.x * x + up.y * y + up.z * z) * scale
        }

        var visible = 0
        for (base in 0 until count step 3) {
            if (depth[base] <= .1f || depth[base + 1] <= .1f || depth[base + 2] <= .1f) continue
            val p = base * 2
            val cross = (points[p + 2] - points[p]) * (points[p + 5] - points[p + 1]) -
                (points[p + 3] - points[p + 1]) * (points[p + 4] - points[p])
            if (cross >= 0f) continue
            val d = (depth[base] + depth[base + 1] + depth[base + 2]) / 3f
            // Draw terrain layers before objects. Sort depth within each layer.
            val tag = mesh.tags[base].toInt()
            val terrainLayer = if (tag in 5..10) 11 - tag else 0
            val depthKey = d.toRawBits().toLong()
            val layer = (terrainLayer.toLong() shl 28) + (depthKey ushr 4)
            order[visible++] = (layer shl 32) or base.toLong()
        }
        java.util.Arrays.sort(order, 0, visible)
        vertices = 0
        for (i in visible - 1 downTo 0) {
            val base = order[i].toInt()
            repeat(3) { step ->
                val vertex = base + step
                sortedPoints[vertices * 2] = points[vertex * 2]
                sortedPoints[vertices * 2 + 1] = points[vertex * 2 + 1]
                sortedColors[vertices] = shade(vertex, camera.highlight)
                vertices++
            }
        }
    }

    private fun shade(i: Int, highlight: Int): Int {
        val shade = if (mesh.tags[i].toInt() == 5) 1f else mesh.lighting[i]
        val tag = mesh.tags[i].toInt()
        val color = mesh.colors[i]
        fun channel(shift: Int, sky: Int, accent: Int): Int {
            val value = (color shr shift) and 255
            val mixed = when {
                highlight == 0 -> value * .81f + sky * .19f
                highlight == tag -> value * .42f + accent * .58f
                else -> value.toFloat()
            }
            return (mixed * shade).toInt().coerceIn(0, 255)
        }
        if (tag == 5) return 0xFF7E98A0.toInt()
        val alpha = when (tag) {
            in 11..13 -> 90
            14, 15 -> 235
            else -> 255
        }
        return (alpha shl 24) or (channel(16, 226, 255) shl 16) or
            (channel(8, 245, if (highlight == 4) 239 else 99) shl 8) or
            channel(0, 219, if (highlight == 4) 230 else 110)
    }
}

