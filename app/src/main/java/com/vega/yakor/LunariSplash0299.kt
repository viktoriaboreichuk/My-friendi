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

private data class Sprite0299(
    val sx: Int,
    val sy: Int,
    val sw: Int,
    val sh: Int,
    val dx: Int,
    val dy: Int,
    val dw: Int,
    val dh: Int
)

private val MOON_0299 = Sprite0299(4, 4, 571, 713, 41, 550, 571, 713)
private val MOON_GLOW_0299 = Sprite0299(579, 4, 565, 650, 19, 535, 565, 650)
private val TRAIL4_0299 = Sprite0299(4, 721, 925, 489, 16, 769, 925, 489)
private val TRAIL3_0299 = Sprite0299(933, 721, 937, 386, 4, 809, 937, 386)
private val TRAIL2_0299 = Sprite0299(4, 1214, 885, 318, 46, 858, 885, 318)
private val LOGO_0299 = Sprite0299(893, 1214, 915, 281, 24, 854, 915, 281)
private val TRAIL1_0299 = Sprite0299(4, 1536, 908, 269, 33, 826, 908, 269)
private val STAR_0299 = Sprite0299(916, 1536, 243, 237, 235, 732, 243, 237)
private val TAGLINE_0299 = Sprite0299(1163, 1536, 638, 42, 159, 1002, 638, 42)

/**
 * Lunari 0.2.9.9 layered mobile portrait splash.
 *
 * The user-supplied starfield/cloud/filigree pieces are pre-composited into a
 * 941x2048 portrait base without stretching or cropping them. The base itself is
 * always shown with ContentScale.Fit. If a phone aspect differs slightly, a quiet
 * procedural continuation fills only the outside area so no black letterbox bars
 * appear and the supplied artwork remains fully visible and undistorted.
 *
 * User PNG layers (moon/logo/tagline/star/trail stages) are packed into one alpha
 * atlas and rendered at their exact source positions. The accepted 0.2.9.8 dust
 * and glitter system is preserved above the staged trail images.
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

    val loopTransition = rememberInfiniteTransition(label = "lunari-splash-loop-0299")
    val loop by loopTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lunari-splash-loop-phase-0299"
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
    val moonGlowAlpha = smoothStep0299(phase0299(e, 0.00f, 0.30f)) *
        (0.78f + 0.10f * sin01_0299(loop))
    val logoAlpha = (0.10f + 0.90f * smoothStep0299(phase0299(e, 0.16f, 0.66f)))
        .coerceAtMost(1f)
    val taglineAlpha = smoothStep0299(phase0299(e, 0.30f, 0.68f)).coerceIn(0f, 0.96f)

    val starRise = smoothStep0299(phase0299(e, 0.61f, 0.73f))
    val starFall = 1f - smoothStep0299(phase0299(e, 0.81f, 0.94f))
    val starAlpha = (starRise * starFall * 0.92f + if (e > 0.94f) 0.12f * sin01_0299(loop) else 0f)
        .coerceIn(0f, 0.92f)

    val trail1Alpha = bell0299(e, 0.10f, 0.24f, 0.42f) * 0.78f
    val trail2Alpha = bell0299(e, 0.22f, 0.39f, 0.61f) * 0.80f
    val trail3Alpha = bell0299(e, 0.36f, 0.57f, 0.82f) * 0.84f
    val trail4Alpha = if (e < 0.53f) {
        0f
    } else if (e < 0.84f) {
        smoothStep0299(phase0299(e, 0.53f, 0.84f)) * 0.82f
    } else {
        0.52f + 0.08f * sin01_0299(loop)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050817))
    ) {
        // Full-screen continuation only outside the fitted 941x2048 artwork.
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Color(0xFF050817))
            ambientStars.forEach { s ->
                drawCircle(
                    color = Color(0xFFD8C8FF).copy(alpha = s.alpha),
                    radius = size.minDimension * s.radius,
                    center = Offset(size.width * s.x, size.height * s.y)
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6D3DA6).copy(alpha = 0.09f), Color.Transparent),
                    center = Offset(size.width * 0.76f, size.height * 0.34f),
                    radius = size.width * 0.56f
                ),
                center = Offset(size.width * 0.76f, size.height * 0.34f),
                radius = size.width * 0.56f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF7650A7).copy(alpha = 0.07f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.94f),
                    radius = size.width * 0.64f
                ),
                center = Offset(size.width * 0.50f, size.height * 0.94f),
                radius = size.width * 0.64f
            )
        }

        Image(
            painter = painterResource(R.drawable.lunari_splash_base_0299),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
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
            val scale = fitW / BASE_W_0299

            fun drawSprite(sprite: Sprite0299, alpha: Float) {
                if (alpha <= 0.001f) return
                drawImage(
                    image = atlas,
                    srcOffset = IntOffset(sprite.sx, sprite.sy),
                    srcSize = IntSize(sprite.sw, sprite.sh),
                    dstOffset = IntOffset(
                        (originX + sprite.dx * scale).roundToInt(),
                        (originY + sprite.dy * scale).roundToInt()
                    ),
                    dstSize = IntSize(
                        (sprite.dw * scale).roundToInt().coerceAtLeast(1),
                        (sprite.dh * scale).roundToInt().coerceAtLeast(1)
                    ),
                    alpha = alpha.coerceIn(0f, 1f)
                )
            }

            fun mapDesign(nx: Float, ny: Float): Offset = Offset(
                originX + (BASE_W_0299 * nx) * scale,
                originY + (DESIGN_Y_OFFSET_0299 + DESIGN_H_0299 * ny) * scale
            )

            drawSprite(MOON_0299, 0.94f)
            drawSprite(MOON_GLOW_0299, moonGlowAlpha)

            drawSprite(TRAIL1_0299, trail1Alpha)
            drawSprite(TRAIL2_0299, trail2Alpha)
            drawSprite(TRAIL3_0299, trail3Alpha)
            drawSprite(TRAIL4_0299, trail4Alpha)

            // Preserve the accepted 0.2.9.8 dust/glitter effect.
            val trailProgress = smoothStep0299(phase0299(e, 0.12f, 0.88f))
            settledDust.forEach { particle ->
                val gate = smoothStep0299(phase0299(trailProgress, particle.t - 0.10f, particle.t + 0.11f))
                if (gate > 0f) {
                    val path = lunarParticlePath0299(particle.t)
                    val center = mapDesign(path.first + particle.offsetX, path.second + particle.offsetY)
                    val pulse = 0.42f + 0.58f * sin01_0299(loop + particle.phase)
                    val settle = if (e < 0.96f) 1f else 0.66f + 0.22f * pulse
                    val alpha = (particle.alpha * gate * pulse * settle).coerceIn(0f, 0.90f)
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
                    val alpha = (particle.alpha * envelope).coerceIn(0f, 0.92f)
                    drawSparkle0299(center, fitW * particle.size, alpha, particle.cross, fitW)
                }
            }

            val curlGate = smoothStep0299(phase0299(trailProgress, 0.58f, 0.92f))
            if (curlGate > 0f) {
                repeat(34) { index ->
                    val u = index / 33f
                    val angle = -0.6f + u * 4.65f + loop * 0.16f
                    val radius = fitW * (0.095f * (1f - 0.48f * u))
                    val c = mapDesign(0.835f, 0.462f)
                    val p = Offset(
                        c.x + cos(angle) * radius,
                        c.y + sin(angle) * radius * 0.55f
                    )
                    val pulse = 0.48f + 0.52f * sin01_0299(loop + index * 0.091f)
                    val a = curlGate * pulse * (0.22f + (index % 5) * 0.08f)
                    drawSparkle0299(
                        p,
                        fitW * (0.0017f + (index % 4) * 0.0008f),
                        a.coerceAtMost(0.86f),
                        index % 7 == 0,
                        fitW
                    )
                }
            }

            drawSprite(LOGO_0299, logoAlpha)
            drawSprite(STAR_0299, starAlpha)
            drawSprite(TAGLINE_0299, taglineAlpha)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 62.dp),
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
                color = Color(0xFFD9C9F3),
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
            color = Color(0xFF443553).copy(alpha = 0.95f),
            radius = size.minDimension * 0.44f,
            center = c,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.minDimension * 0.06f)
        )
        if (active) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.98f),
                        Color(0xFFDEBFFF).copy(alpha = 0.94f),
                        Color(0xFF8A56D6).copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    center = c,
                    radius = size.minDimension * 0.78f
                ),
                radius = size.minDimension * 0.78f,
                center = c
            )
            drawCircle(
                color = Color(0xFFF6ECFF),
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
