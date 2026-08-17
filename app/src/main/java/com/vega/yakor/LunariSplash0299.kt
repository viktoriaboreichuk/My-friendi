package com.vega.yakor

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private const val BASE_W_0299 = 941f
private const val BASE_H_0299 = 2048f
private const val BASE_ASPECT_0299 = BASE_W_0299 / BASE_H_0299
private const val DESIGN_Y_OFFSET_0299 = 188f
private const val DESIGN_H_0299 = 1672f
private const val VISUAL_DIM_02911 = 0.75f

private data class Sprite0299(
    val sx: Int,
    val sy: Int,
    val sw: Int,
    val sh: Int
)

// Exact FULL-resolution atlas rectangles from the uploaded pack.
private val MOON_0299 = Sprite0299(4, 4, 571, 713)
private val TRAIL4_0299 = Sprite0299(4, 721, 925, 489)
private val TRAIL3_0299 = Sprite0299(933, 721, 937, 386)
private val TRAIL2_0299 = Sprite0299(4, 1214, 885, 318)
private val LOGO_0299 = Sprite0299(893, 1214, 915, 281)
private val TRAIL1_0299 = Sprite0299(4, 1536, 908, 269)
private val STAR_0299 = Sprite0299(916, 1536, 243, 237)
private val TAGLINE_0299 = Sprite0299(1163, 1536, 638, 42)

/**
 * Lunari 0.2.9.11 reference-matched layered portrait splash.
 *
 * The supplied 941x2048 base is always fitted without cropping or stretching.
 * The middle 941x1672 design area matches the user's portrait reference.
 * Moon/logo/tagline start from alpha 0 and fade in gradually. The moon is rendered
 * only once and gently twinkles; the separate moon-glow sprite is intentionally
 * not drawn, preventing the double-crescent effect seen in 0.2.9.10.
 *
 * The four supplied trail PNG stages are cross-faded in storyboard order rather
 * than stacked, keeping the energy ribbon readable and preventing over-brightness.
 */
@Composable
fun LunariSplash0299(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entrance = remember { Animatable(0f) }
    var activeDot by remember { mutableIntStateOf(-1) }
    val settledDust = remember { makeSettledDust0299() }
    val travellingDust = remember { makeTravellingDust0299() }
    val ambientStars = remember { makeAmbientStars0299() }
    val atlas = ImageBitmap.imageResource(R.drawable.lunari_splash_atlas_0299)

    val loopTransition = rememberInfiniteTransition(label = "lunari-splash-loop-02911")
    val loop by loopTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lunari-splash-loop-phase-02911"
    )

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 5200, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        delay(240)
        activeDot = 0
        while (isActive) {
            delay(430)
            activeDot = (activeDot + 1) % 4
        }
    }

    LaunchedEffect(Unit) {
        delay(5650)
        onFinished()
    }

    val e = entrance.value.coerceIn(0f, 1f)
    val progressPercent = (e * 100f).roundToInt().coerceIn(0, 100)

    val moonAlpha = (
        smoothStep0299(phase0299(e, 0.00f, 0.24f)) *
            (0.88f + 0.12f * sin01_0299(loop)) * VISUAL_DIM_02911
        ).coerceIn(0f, VISUAL_DIM_02911)
    val logoAlpha = (
        smoothStep0299(phase0299(e, 0.13f, 0.47f)) * VISUAL_DIM_02911
        ).coerceIn(0f, VISUAL_DIM_02911)
    val taglineAlpha = (
        smoothStep0299(phase0299(e, 0.31f, 0.64f)) * VISUAL_DIM_02911
        ).coerceIn(0f, VISUAL_DIM_02911)
    val starAlpha = (
        smoothStep0299(phase0299(e, 0.08f, 0.30f)) *
            (0.50f + 0.42f * sin01_0299(loop + 0.17f)) * VISUAL_DIM_02911
        ).coerceIn(0f, 0.68f)

    val trail1Alpha = bell0299(e, 0.17f, 0.28f, 0.41f) * 0.64f * VISUAL_DIM_02911
    val trail2Alpha = bell0299(e, 0.34f, 0.45f, 0.57f) * 0.66f * VISUAL_DIM_02911
    val trail3Alpha = bell0299(e, 0.50f, 0.61f, 0.73f) * 0.68f * VISUAL_DIM_02911
    val trail4Alpha = if (e < 0.66f) {
        0f
    } else {
        smoothStep0299(phase0299(e, 0.66f, 0.84f)) *
            (0.60f + 0.04f * sin01_0299(loop + 0.31f)) * VISUAL_DIM_02911
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF03050E))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFF03050E))
            ambientStars.forEach { s ->
                drawCircle(
                    color = Color(0xFFD8C8FF).copy(alpha = s.alpha * VISUAL_DIM_02911),
                    radius = size.minDimension * s.radius,
                    center = Offset(size.width * s.x, size.height * s.y)
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6D3DA6).copy(alpha = 0.055f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.76f, size.height * 0.34f),
                    radius = size.width * 0.56f
                ),
                center = Offset(size.width * 0.76f, size.height * 0.34f),
                radius = size.width * 0.56f
            )
        }

        Image(
            painter = painterResource(R.drawable.lunari_splash_base_0299),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            alpha = VISUAL_DIM_02911
        )

        Canvas(Modifier.fillMaxSize()) {
            val viewW = size.width
            val viewH = size.height
            val viewAspect = viewW / viewH
            val fitW: Float
            val fitH: Float
            val originX: Float
            val originY: Float
            if (viewAspect < BASE_ASPECT_0299) {
                fitW = viewW
                fitH = viewW / BASE_ASPECT_0299
                originX = 0f
                originY = (viewH - fitH) * 0.5f
            } else {
                fitH = viewH
                fitW = viewH * BASE_ASPECT_0299
                originY = 0f
                originX = (viewW - fitW) * 0.5f
            }
            val baseScale = fitW / BASE_W_0299

            fun drawPlaced(
                sprite: Sprite0299,
                designX: Float,
                designY: Float,
                spriteScale: Float,
                alpha: Float
            ) {
                if (alpha <= 0.001f) return
                drawImage(
                    image = atlas,
                    srcOffset = IntOffset(sprite.sx, sprite.sy),
                    srcSize = IntSize(sprite.sw, sprite.sh),
                    dstOffset = IntOffset(
                        (originX + designX * baseScale).roundToInt(),
                        (originY + (DESIGN_Y_OFFSET_0299 + designY) * baseScale).roundToInt()
                    ),
                    dstSize = IntSize(
                        (sprite.sw * spriteScale * baseScale).roundToInt().coerceAtLeast(1),
                        (sprite.sh * spriteScale * baseScale).roundToInt().coerceAtLeast(1)
                    ),
                    alpha = alpha.coerceIn(0f, 1f)
                )
            }

            fun mapDesign(nx: Float, ny: Float): Offset = Offset(
                originX + (BASE_W_0299 * nx) * baseScale,
                originY + (DESIGN_Y_OFFSET_0299 + DESIGN_H_0299 * ny) * baseScale
            )

            drawPlaced(MOON_0299, designX = 75f, designY = 395f, spriteScale = 0.735f, alpha = moonAlpha)

            drawPlaced(TRAIL1_0299, designX = 251f, designY = 665f, spriteScale = 0.66f, alpha = trail1Alpha)
            drawPlaced(TRAIL2_0299, designX = 266f, designY = 650f, spriteScale = 0.66f, alpha = trail2Alpha)
            drawPlaced(TRAIL3_0299, designX = 232f, designY = 625f, spriteScale = 0.66f, alpha = trail3Alpha)
            drawPlaced(TRAIL4_0299, designX = 239f, designY = 590f, spriteScale = 0.66f, alpha = trail4Alpha)

            val trailProgress = smoothStep0299(phase0299(e, 0.18f, 0.90f))
            settledDust.forEach { particle ->
                val gate = smoothStep0299(phase0299(trailProgress, particle.t - 0.10f, particle.t + 0.11f))
                if (gate > 0f) {
                    val path = lunarParticlePath0299(particle.t)
                    val center = mapDesign(path.first + particle.offsetX, path.second + particle.offsetY)
                    val pulse = 0.42f + 0.58f * sin01_0299(loop + particle.phase)
                    val settle = if (e < 0.96f) 1f else 0.66f + 0.22f * pulse
                    val alpha = (particle.alpha * gate * pulse * settle * VISUAL_DIM_02911)
                        .coerceIn(0f, 0.62f)
                    drawSparkle0299(center, fitW * particle.size, alpha, particle.cross, fitW)
                }
            }

            travellingDust.forEach { particle ->
                val local = phase0299(trailProgress, particle.spawn, particle.spawn + particle.life)
                if (local in 0.001f..0.999f) {
                    val eased = smoothStep0299(local)
                    val pathT = (particle.spawn + eased * particle.travel).coerceIn(0f, 1f)
                    val path = lunarParticlePath0299(pathT)
                    val envelope = sin((PI * local).toFloat()).coerceAtLeast(0f)
                    val center = mapDesign(
                        path.first + particle.offsetX * (1f - 0.35f * eased),
                        path.second + particle.offsetY
                    )
                    val alpha = (particle.alpha * envelope * VISUAL_DIM_02911)
                        .coerceIn(0f, 0.66f)
                    drawSparkle0299(center, fitW * particle.size, alpha, particle.cross, fitW)
                }
            }

            val curlGate = smoothStep0299(phase0299(trailProgress, 0.58f, 0.92f))
            if (curlGate > 0f) {
                repeat(28) { index ->
                    val u = index / 27f
                    val angle = -0.6f + u * 4.65f + loop * 0.16f
                    val radius = fitW * (0.078f * (1f - 0.48f * u))
                    val c = mapDesign(0.76f, 0.445f)
                    val p = Offset(
                        c.x + cos(angle) * radius,
                        c.y + sin(angle) * radius * 0.55f
                    )
                    val pulse = 0.48f + 0.52f * sin01_0299(loop + index * 0.091f)
                    val a = curlGate * pulse * (0.18f + (index % 5) * 0.055f) * VISUAL_DIM_02911
                    drawSparkle0299(
                        p,
                        fitW * (0.00145f + (index % 4) * 0.00065f),
                        a.coerceAtMost(0.54f),
                        index % 7 == 0,
                        fitW
                    )
                }
            }

            drawPlaced(LOGO_0299, designX = 190f, designY = 635f, spriteScale = 0.74f, alpha = logoAlpha)
            drawPlaced(STAR_0299, designX = 331f, designY = 567f, spriteScale = 0.185f, alpha = starAlpha)
            drawPlaced(TAGLINE_0299, designX = 215f, designY = 925f, spriteScale = 0.95f, alpha = taglineAlpha)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    LoadingDot0299(active = index == activeDot)
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = "Загрузка... $progressPercent%",
                color = Color(0xFFD9C9F3).copy(alpha = VISUAL_DIM_02911),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun LoadingDot0299(active: Boolean) {
    Canvas(Modifier.size(18.dp)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        drawCircle(
            color = Color(0xFF040408),
            radius = size.minDimension * 0.44f,
            center = c
        )
        drawCircle(
            color = Color(0xFF443553).copy(alpha = 0.72f),
            radius = size.minDimension * 0.44f,
            center = c,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.minDimension * 0.06f)
        )
        if (active) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f),
                        Color(0xFFDEBFFF).copy(alpha = 0.72f),
                        Color(0xFF8A56D6).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = c,
                    radius = size.minDimension * 0.78f
                ),
                radius = size.minDimension * 0.78f,
                center = c
            )
            drawCircle(
                color = Color(0xFFF6ECFF).copy(alpha = 0.82f),
                radius = size.minDimension * 0.34f,
                center = c
            )
        }
    }
}

private data class SettledDust0299(
    val t: Float,
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val phase: Float,
    val cross: Boolean
)

private data class TravellingDust0299(
    val spawn: Float,
    val life: Float,
    val travel: Float,
    val offsetX: Float,
    val offsetY: Float,
    val size: Float,
    val alpha: Float,
    val cross: Boolean
)

private data class AmbientStar0299(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float
)

private fun makeSettledDust0299(): List<SettledDust0299> {
    val random = Random(29801)
    return List(154) {
        val t = random.nextFloat()
        val spread = if (t > 0.70f) 1.35f else 1f
        SettledDust0299(
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

private fun makeTravellingDust0299(): List<TravellingDust0299> {
    val random = Random(29802)
    return List(92) {
        val spawn = random.nextFloat() * 0.84f
        TravellingDust0299(
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

private fun makeAmbientStars0299(): List<AmbientStar0299> {
    val random = Random(29977)
    return List(120) {
        AmbientStar0299(
            x = random.nextFloat(),
            y = random.nextFloat(),
            radius = 0.0007f + random.nextFloat() * 0.0012f,
            alpha = 0.08f + random.nextFloat() * 0.22f
        )
    }
}

private fun lunarParticlePath0299(tRaw: Float): Pair<Float, Float> {
    val t = tRaw.coerceIn(0f, 1f)
    return if (t < 0.71f) {
        val u = t / 0.71f
        val x = 0.22f + 0.55f * u
        val y = 0.445f + 0.030f * sin((PI * u).toFloat()) + 0.003f * u
        x to y
    } else {
        val u = (t - 0.71f) / 0.29f
        val angle = -0.72f + u * 4.48f
        val radius = 0.082f * (1f - 0.48f * u)
        val cx = 0.75f + 0.028f * u
        val cy = 0.444f - 0.014f * u
        val x = cx + radius * cos(angle)
        val y = cy + radius * 0.53f * sin(angle)
        x to y
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSparkle0299(
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
                Color.White.copy(alpha = alpha.coerceAtMost(0.76f)),
                Color(0xFFE6D0FF).copy(alpha = alpha * 0.58f),
                Color(0xFF9A64E7).copy(alpha = alpha * 0.10f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 3.3f
        ),
        center = center,
        radius = radius * 3.3f
    )
    drawCircle(
        color = Color(0xFFF8F0FF).copy(alpha = (alpha * 0.76f).coerceAtMost(0.72f)),
        center = center,
        radius = radius
    )
    if (cross) {
        val half = radius * crossScale
        drawLine(
            color = Color(0xFFF8EEFF).copy(alpha = alpha * 0.62f),
            start = Offset(center.x - half, center.y),
            end = Offset(center.x + half, center.y),
            strokeWidth = fitW * 0.0009f
        )
        drawLine(
            color = Color(0xFFF8EEFF).copy(alpha = alpha * 0.62f),
            start = Offset(center.x, center.y - half),
            end = Offset(center.x, center.y + half),
            strokeWidth = fitW * 0.0009f
        )
    }
}

private fun bell0299(value: Float, start: Float, peak: Float, end: Float): Float {
    return if (value <= peak) {
        smoothStep0299(phase0299(value, start, peak))
    } else {
        1f - smoothStep0299(phase0299(value, peak, end))
    }.coerceIn(0f, 1f)
}

private fun phase0299(value: Float, start: Float, end: Float): Float {
    if (end <= start) return if (value >= end) 1f else 0f
    return ((value - start) / (end - start)).coerceIn(0f, 1f)
}

private fun smoothStep0299(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}

private fun sin01_0299(value: Float): Float = 0.5f + 0.5f * sin((value * 2f * PI).toFloat())
