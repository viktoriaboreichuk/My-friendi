package com.vega.yakor

import android.graphics.Path as AndroidPath
import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val SPLASH_ASPECT_0298 = 941f / 1672f

/**
 * Lunari 0.2.9.8 mobile portrait splash revision.
 *
 * 1) The approved 941x1672 composition is no longer stretched: the foreground
 *    always uses ContentScale.Fit. A dim Crop copy only fills extra device space.
 * 2) The lunar effect is particle-first: broad low-alpha ribbons + dense travelling
 *    dust + settled glitter + right-hand curl burst. No single bright "strip" trail.
 */
@Composable
fun LunariSplash0296(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entrance = remember { Animatable(0f) }
    var activeDots by remember { mutableIntStateOf(1) }
    val settledDust = remember { makeSettledDust0298() }
    val travellingDust = remember { makeTravellingDust0298() }

    val loopTransition = rememberInfiniteTransition(label = "lunari-splash-loop-0298")
    val loop by loopTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lunari-splash-loop-phase-0298"
    )

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4700, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(550)
            activeDots = if (activeDots >= 4) 1 else activeDots + 1
        }
    }

    LaunchedEffect(Unit) {
        delay(5200)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050817))
    ) {
        // Fills tall/narrow phone area without deforming the approved artwork.
        Image(
            painter = painterResource(R.drawable.lunari_splash_mobile_0296),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.30f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA050817))
        )

        // The actual approved composition: preserve its exact aspect ratio.
        Image(
            painter = painterResource(R.drawable.lunari_splash_mobile_0296),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Canvas(Modifier.fillMaxSize()) {
            val e = entrance.value.coerceIn(0f, 1f)
            val viewW = size.width
            val viewH = size.height
            val viewAspect = viewW / viewH

            val fitW: Float
            val fitH: Float
            val originX: Float
            val originY: Float
            if (viewAspect < SPLASH_ASPECT_0298) {
                fitW = viewW
                fitH = viewW / SPLASH_ASPECT_0298
                originX = 0f
                originY = (viewH - fitH) * 0.5f
            } else {
                fitH = viewH
                fitW = viewH * SPLASH_ASPECT_0298
                originY = 0f
                originX = (viewW - fitW) * 0.5f
            }

            fun map(nx: Float, ny: Float): Offset = Offset(
                originX + fitW * nx,
                originY + fitH * ny
            )

            val moonProgress = smoothStep0298(phase0298(e, 0.00f, 0.22f))
            val trailProgress = smoothStep0298(phase0298(e, 0.13f, 0.84f))
            val logoProgress = smoothStep0298(phase0298(e, 0.43f, 0.88f))

            // Moon wakes softly first.
            val moonCenter = map(0.235f, 0.395f)
            val moonBreath = 0.92f + 0.08f * sin01(loop)
            val moonAlpha = moonProgress * moonBreath
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF9F2FF).copy(alpha = 0.14f * moonAlpha),
                        Color(0xFFC7A8FF).copy(alpha = 0.055f * moonAlpha),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = fitW * 0.205f
                ),
                center = moonCenter,
                radius = fitW * 0.205f
            )

            // Three broad, low-alpha ribbons. These are deliberately secondary;
            // the readable effect comes from the dust field, not a bright line.
            val ribbonPaths = buildRibbonPaths0298(::map)
            val ribbonStarts = listOf(0.00f, 0.08f, 0.17f)
            val ribbonEnds = listOf(0.82f, 0.92f, 1.00f)
            val ribbonWidths = listOf(0.020f, 0.010f, 0.0042f)
            val ribbonAlphas = listOf(0.075f, 0.13f, 0.24f)

            ribbonPaths.forEachIndexed { index, rawPath ->
                val local = smoothStep0298(phase0298(trailProgress, ribbonStarts[index], ribbonEnds[index]))
                if (local > 0f) {
                    val measure = PathMeasure(rawPath, false)
                    val segment = AndroidPath()
                    measure.getSegment(0f, measure.length * local, segment, true)
                    val composePath = segment.asComposePath()
                    val settle = if (e < 0.96f) 1f else 0.72f + 0.18f * sin01(loop + index * 0.17f)
                    drawPath(
                        path = composePath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFB98CFF).copy(alpha = ribbonAlphas[index] * 0.58f * settle),
                                Color(0xFFE1C9FF).copy(alpha = ribbonAlphas[index] * settle),
                                Color(0xFF8C4DE8).copy(alpha = ribbonAlphas[index] * 0.72f * settle)
                            ),
                            start = map(0.15f, 0.49f),
                            end = map(0.91f, 0.45f)
                        ),
                        style = Stroke(
                            width = fitW * ribbonWidths[index],
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            // Settled moon dust: many particles accumulate behind/around the flow.
            settledDust.forEach { particle ->
                val gate = smoothStep0298(phase0298(trailProgress, particle.t - 0.10f, particle.t + 0.11f))
                if (gate > 0f) {
                    val path = lunarParticlePath0298(particle.t)
                    val center = map(
                        path.first + particle.offsetX,
                        path.second + particle.offsetY
                    )
                    val pulse = 0.42f + 0.58f * sin01(loop + particle.phase)
                    val settle = if (e < 0.96f) 1f else 0.64f + 0.24f * pulse
                    val alpha = (particle.alpha * gate * pulse * settle).coerceIn(0f, 0.90f)
                    drawSparkle0298(
                        center = center,
                        radius = fitW * particle.size,
                        alpha = alpha,
                        cross = particle.cross,
                        fitW = fitW
                    )
                }
            }

            // Travelling dust: particles move forward along the ribbon and fade,
            // giving the actual sweep/flow requested by the storyboard.
            travellingDust.forEach { particle ->
                val local = phase0298(trailProgress, particle.spawn, particle.spawn + particle.life)
                if (local in 0.001f..0.999f) {
                    val eased = smoothStep0298(local)
                    val pathT = (particle.spawn + eased * particle.travel).coerceIn(0f, 1f)
                    val path = lunarParticlePath0298(pathT)
                    val envelope = sin((PI * local).toFloat()).coerceAtLeast(0f)
                    val center = map(
                        path.first + particle.offsetX * (1f - 0.35f * eased),
                        path.second + particle.offsetY
                    )
                    val alpha = (particle.alpha * envelope).coerceIn(0f, 0.92f)
                    drawSparkle0298(
                        center = center,
                        radius = fitW * particle.size,
                        alpha = alpha,
                        cross = particle.cross,
                        fitW = fitW
                    )
                }
            }

            // Dense right-hand curl burst: strongest sparkle cloud in frames 3-5.
            val curlGate = smoothStep0298(phase0298(trailProgress, 0.58f, 0.92f))
            if (curlGate > 0f) {
                repeat(34) { index ->
                    val u = index / 33f
                    val angle = -0.6f + u * 4.65f + loop * 0.16f
                    val radius = fitW * (0.095f * (1f - 0.48f * u))
                    val c = map(0.835f, 0.462f)
                    val p = Offset(
                        c.x + cos(angle) * radius,
                        c.y + sin(angle) * radius * 0.55f
                    )
                    val pulse = 0.48f + 0.52f * sin01(loop + index * 0.091f)
                    val a = curlGate * pulse * (0.22f + (index % 5) * 0.08f)
                    drawSparkle0298(
                        center = p,
                        radius = fitW * (0.0017f + (index % 4) * 0.0008f),
                        alpha = a.coerceAtMost(0.86f),
                        cross = index % 7 == 0,
                        fitW = fitW
                    )
                }
            }

            // Logo luminosity follows the passing dust.
            val logoCenter = map(0.55f, 0.447f)
            val logoPulse = 0.92f + 0.08f * sin01(loop + 0.21f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF0E6FF).copy(alpha = 0.075f * logoProgress * logoPulse),
                        Color(0xFFAA78F2).copy(alpha = 0.024f * logoProgress * logoPulse),
                        Color.Transparent
                    ),
                    center = logoCenter,
                    radius = fitW * 0.38f
                ),
                center = logoCenter,
                radius = fitW * 0.38f
            )

            // One soft star accent late in the entrance.
            val starRise = smoothStep0298(phase0298(e, 0.70f, 0.79f))
            val starFall = 1f - smoothStep0298(phase0298(e, 0.82f, 0.95f))
            val starAccent = (starRise * starFall).coerceIn(0f, 1f)
            val starAlpha = (0.12f + 0.74f * starAccent + 0.035f * sin01(loop)).coerceIn(0.08f, 0.88f)
            val star = map(0.374f, 0.354f)
            drawSparkle0298(
                center = star,
                radius = fitW * 0.010f,
                alpha = starAlpha,
                cross = true,
                fitW = fitW,
                crossScale = 4.4f
            )

            // One clean loading-dot row. Mask baked static dots at exact image coords.
            val dotXs = floatArrayOf(0.41369f, 0.47011f, 0.52704f, 0.58453f)
            val dotY = 0.83050f
            val localBackgrounds = listOf(
                Color(0xFF111432),
                Color(0xFF302D59),
                Color(0xFF19173C),
                Color(0xFF141438)
            )
            dotXs.forEachIndexed { index, nx ->
                val center = map(nx, dotY)
                val bg = localBackgrounds[index]
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(bg, bg, bg.copy(alpha = 0f)),
                        center = center,
                        radius = fitW * 0.031f
                    ),
                    center = center,
                    radius = fitW * 0.031f
                )
                drawCircle(
                    color = Color(0xFF685983).copy(alpha = 0.76f),
                    center = center,
                    radius = fitW * 0.0115f
                )
                if (index < activeDots) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.97f),
                                Color(0xFFDABFFF).copy(alpha = 0.86f),
                                Color(0xFF9B6BE8).copy(alpha = 0.17f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = fitW * 0.0235f
                        ),
                        center = center,
                        radius = fitW * 0.0235f
                    )
                    drawCircle(
                        color = Color(0xFFF4EBFF),
                        center = center,
                        radius = fitW * 0.0096f
                    )
                }
            }
        }
    }
}

private data class SettledDust0298(
    val t: Float,
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val phase: Float,
    val cross: Boolean
)

private data class TravellingDust0298(
    val spawn: Float,
    val life: Float,
    val travel: Float,
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val cross: Boolean
)

private fun makeSettledDust0298(): List<SettledDust0298> {
    val random = Random(29801)
    return List(154) {
        val t = random.nextFloat()
        val spread = if (t > 0.70f) 1.35f else 1f
        SettledDust0298(
            t = t,
            offsetX = (random.nextFloat() - 0.5f) * 0.034f * spread,
            offsetY = (random.nextFloat() - 0.5f) * 0.044f * spread,
            size = 0.00135f + random.nextFloat() * 0.0030f,
            alpha = 0.30f + random.nextFloat() * 0.55f,
            phase = random.nextFloat(),
            cross = random.nextFloat() > 0.88f
        )
    }.sortedBy { it.t }
}

private fun makeTravellingDust0298(): List<TravellingDust0298> {
    val random = Random(29802)
    return List(92) {
        val spawn = random.nextFloat() * 0.84f
        TravellingDust0298(
            spawn = spawn,
            life = 0.14f + random.nextFloat() * 0.12f,
            travel = 0.08f + random.nextFloat() * 0.13f,
            offsetX = (random.nextFloat() - 0.5f) * 0.020f,
            offsetY = (random.nextFloat() - 0.5f) * 0.026f,
            size = 0.0015f + random.nextFloat() * 0.0035f,
            alpha = 0.46f + random.nextFloat() * 0.46f,
            cross = random.nextFloat() > 0.82f
        )
    }.sortedBy { it.spawn }
}

private fun buildRibbonPaths0298(map: (Float, Float) -> Offset): List<AndroidPath> {
    fun make(offsetY: Float): AndroidPath {
        val p0 = map(0.16f, 0.470f + offsetY)
        val p1 = map(0.31f, 0.492f + offsetY)
        val p2 = map(0.50f, 0.520f + offsetY)
        val p3 = map(0.69f, 0.492f + offsetY)
        val p4 = map(0.80f, 0.478f + offsetY)
        val p5 = map(0.88f, 0.431f + offsetY)
        val p6 = map(0.915f, 0.438f + offsetY)
        val p7 = map(0.965f, 0.451f + offsetY)
        val p8 = map(0.944f, 0.511f + offsetY)
        val p9 = map(0.876f, 0.514f + offsetY)
        val p10 = map(0.826f, 0.493f + offsetY)
        val p11 = map(0.865f, 0.465f + offsetY)
        return AndroidPath().apply {
            moveTo(p0.x, p0.y)
            cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
            cubicTo(p4.x, p4.y, p5.x, p5.y, p6.x, p6.y)
            cubicTo(p7.x, p7.y, p8.x, p8.y, p9.x, p9.y)
            cubicTo(p10.x, p10.y, p10.x, p10.y, p11.x, p11.y)
        }
    }
    return listOf(make(0.010f), make(0.000f), make(-0.011f))
}

/** Normalized path for the dense dust cloud, including the right-hand curl. */
private fun lunarParticlePath0298(tRaw: Float): Pair<Float, Float> {
    val t = tRaw.coerceIn(0f, 1f)
    return if (t < 0.71f) {
        val u = t / 0.71f
        val x = 0.17f + 0.61f * u
        val y = 0.470f + 0.036f * sin((PI * u).toFloat()) + 0.004f * u
        x to y
    } else {
        val u = (t - 0.71f) / 0.29f
        val angle = -0.72f + u * 4.48f
        val radius = 0.104f * (1f - 0.48f * u)
        val cx = 0.80f + 0.035f * u
        val cy = 0.468f - 0.018f * u
        val x = cx + radius * cos(angle)
        val y = cy + radius * 0.53f * sin(angle)
        x to y
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkle0298(
    center: Offset,
    radius: Float,
    alpha: Float,
    cross: Boolean,
    fitW: Float,
    crossScale: Float = 2.7f
) {
    if (alpha <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = alpha.coerceAtMost(0.94f)),
                Color(0xFFE6D0FF).copy(alpha = alpha * 0.68f),
                Color(0xFF9A64E7).copy(alpha = alpha * 0.14f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 3.3f
        ),
        center = center,
        radius = radius * 3.3f
    )
    drawCircle(
        color = Color(0xFFF8F0FF).copy(alpha = (alpha * 0.92f).coerceAtMost(0.92f)),
        center = center,
        radius = radius
    )
    if (cross) {
        val half = radius * crossScale
        drawLine(
            color = Color(0xFFF8EEFF).copy(alpha = alpha * 0.78f),
            start = Offset(center.x - half, center.y),
            end = Offset(center.x + half, center.y),
            strokeWidth = fitW * 0.0009f
        )
        drawLine(
            color = Color(0xFFF8EEFF).copy(alpha = alpha * 0.78f),
            start = Offset(center.x, center.y - half),
            end = Offset(center.x, center.y + half),
            strokeWidth = fitW * 0.0009f
        )
    }
}

private fun phase0298(value: Float, start: Float, end: Float): Float {
    if (end <= start) return if (value >= end) 1f else 0f
    return ((value - start) / (end - start)).coerceIn(0f, 1f)
}

private fun smoothStep0298(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun sin01(value: Float): Float = 0.5f + 0.5f * sin((value * 2f * PI).toFloat())