package com.vega.yakor

import android.graphics.Path as AndroidPath
import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Lunari 0.2.9.6 mobile-portrait loading screen.
 *
 * Visual source is the user-approved flat portrait artwork. We deliberately keep
 * it intact and add only restrained animation layers on top: moon breathing,
 * lunar stream/dust, logo-area glow, a short star accent, loading dots and a few
 * micro-twinkles. No tablet/landscape adaptation is attempted in this patch.
 */
@Composable
fun LunariSplash0296(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val entrance = remember { Animatable(0f) }
    var activeDots by remember { mutableIntStateOf(1) }

    val loopTransition = rememberInfiniteTransition(label = "lunari-splash-loop")
    val loop by loopTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lunari-splash-breathe"
    )

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(320)
            activeDots = if (activeDots >= 4) 1 else activeDots + 1
        }
    }

    LaunchedEffect(Unit) {
        // One entrance only; the main screen is already composed underneath.
        delay(2200)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF050817))
    ) {
        Image(
            painter = painterResource(R.drawable.lunari_splash_mobile_0296),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            // Portrait-only patch: preserve the complete approved composition.
            // Device/aspect-ratio refinement belongs to the later adaptation pass.
            contentScale = ContentScale.FillBounds
        )

        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val e = entrance.value.coerceIn(0f, 1f)

            val moonProgress = smoothStep(phase(e, 0.02f, 0.36f))
            val trailProgress = smoothStep(phase(e, 0.16f, 0.72f))
            val logoProgress = smoothStep(phase(e, 0.40f, 0.82f))

            // 1) Moon: a soft breathing halo, never a hard flash.
            val moonBreath = 0.90f + loop * 0.10f
            val moonAlpha = moonProgress * moonBreath
            val moonCenter = Offset(w * 0.235f, h * 0.395f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF8F1FF).copy(alpha = 0.14f * moonAlpha),
                        Color(0xFFC9ACFF).copy(alpha = 0.085f * moonAlpha),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = w * 0.255f
                ),
                radius = w * 0.255f,
                center = moonCenter
            )

            // 2) Lunar stream: revealed once during the entrance, then left as a
            // restrained shimmer. It travels just below the logo, not across it.
            if (trailProgress > 0.001f) {
                val rawPath = AndroidPath().apply {
                    moveTo(w * 0.175f, h * 0.486f)
                    cubicTo(
                        w * 0.30f, h * 0.474f,
                        w * 0.43f, h * 0.512f,
                        w * 0.60f, h * 0.502f
                    )
                    cubicTo(
                        w * 0.72f, h * 0.496f,
                        w * 0.82f, h * 0.465f,
                        w * 0.872f, h * 0.438f
                    )
                    cubicTo(
                        w * 0.915f, h * 0.416f,
                        w * 0.925f, h * 0.456f,
                        w * 0.866f, h * 0.477f
                    )
                }
                val measure = PathMeasure(rawPath, false)
                val segment = AndroidPath()
                measure.getSegment(
                    0f,
                    measure.length * trailProgress,
                    segment,
                    true
                )
                val composePath = segment.asComposePath()
                val settle = if (e < 1f) 1f else 0.78f + loop * 0.12f

                drawPath(
                    path = composePath,
                    color = Color(0xFF8E52F2).copy(alpha = 0.08f * settle),
                    style = Stroke(width = w * 0.019f)
                )
                drawPath(
                    path = composePath,
                    color = Color(0xFFB576FF).copy(alpha = 0.25f * settle),
                    style = Stroke(width = w * 0.0085f)
                )
                drawPath(
                    path = composePath,
                    color = Color(0xFFE0C7FF).copy(alpha = 0.72f * settle),
                    style = Stroke(width = w * 0.0025f)
                )

                val tip = FloatArray(2)
                if (measure.getPosTan(measure.length * trailProgress, tip, null)) {
                    val tipOffset = Offset(tip[0], tip[1])
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.85f),
                                Color(0xFFC58CFF).copy(alpha = 0.38f),
                                Color.Transparent
                            ),
                            center = tipOffset,
                            radius = w * 0.035f
                        ),
                        center = tipOffset,
                        radius = w * 0.035f
                    )
                }

                val dust = listOf(
                    0.26f to 0.493f,
                    0.34f to 0.500f,
                    0.43f to 0.505f,
                    0.52f to 0.505f,
                    0.61f to 0.497f,
                    0.70f to 0.482f,
                    0.78f to 0.463f,
                    0.84f to 0.447f
                )
                dust.forEachIndexed { index, (nx, ny) ->
                    val localGate = phase(trailProgress, index * 0.085f, index * 0.085f + 0.28f)
                    if (localGate > 0f) {
                        val pulse = 0.55f + 0.45f * if (index % 2 == 0) loop else (1f - loop)
                        drawCircle(
                            color = Color(0xFFE8D8FF).copy(alpha = 0.56f * localGate * pulse),
                            radius = w * (0.0025f + (index % 3) * 0.0012f),
                            center = Offset(w * nx, h * ny)
                        )
                    }
                }
            }

            // 3) Logo area: a very restrained luminosity lift, without moving or
            // scaling the approved logo artwork.
            val logoBreath = 0.90f + loop * 0.10f
            val logoCenter = Offset(w * 0.55f, h * 0.445f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEBDFFF).copy(alpha = 0.065f * logoProgress * logoBreath),
                        Color(0xFF9A65E6).copy(alpha = 0.025f * logoProgress * logoBreath),
                        Color.Transparent
                    ),
                    center = logoCenter,
                    radius = w * 0.42f
                ),
                center = logoCenter,
                radius = w * 0.42f
            )

            // 4) Short star accent near the logo. One pulse in the entrance, then
            // only a tiny background twinkle.
            val starRise = smoothStep(phase(e, 0.62f, 0.76f))
            val starFall = 1f - smoothStep(phase(e, 0.78f, 0.96f))
            val starAccent = (starRise * starFall).coerceIn(0f, 1f)
            val starAlpha = (0.16f + starAccent * 0.70f + loop * 0.06f).coerceAtMost(0.88f)
            val star = Offset(w * 0.374f, h * 0.354f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.72f * starAlpha),
                        Color(0xFFC9A7FF).copy(alpha = 0.20f * starAlpha),
                        Color.Transparent
                    ),
                    center = star,
                    radius = w * 0.050f
                ),
                center = star,
                radius = w * 0.050f
            )
            drawLine(
                color = Color(0xFFF7EEFF).copy(alpha = starAlpha),
                start = Offset(star.x - w * 0.028f, star.y),
                end = Offset(star.x + w * 0.028f, star.y),
                strokeWidth = w * 0.0013f
            )
            drawLine(
                color = Color(0xFFF7EEFF).copy(alpha = starAlpha),
                start = Offset(star.x, star.y - w * 0.028f),
                end = Offset(star.x, star.y + w * 0.028f),
                strokeWidth = w * 0.0013f
            )

            // 5) Quiet background micro-twinkles. Ornaments/clouds stay static.
            val microStars = listOf(
                0.290f to 0.181f,
                0.659f to 0.184f,
                0.748f to 0.301f,
                0.293f to 0.610f,
                0.744f to 0.603f,
                0.572f to 0.667f
            )
            microStars.forEachIndexed { index, (nx, ny) ->
                val pulse = if (index % 2 == 0) loop else (1f - loop)
                val alpha = 0.12f + pulse * 0.24f
                val center = Offset(w * nx, h * ny)
                drawCircle(
                    color = Color(0xFFE7D7FF).copy(alpha = alpha),
                    radius = w * 0.0022f,
                    center = center
                )
            }

            // 6) Loading dots. Opaque dim circles first mask the single bright dot
            // baked into the flat reference, then 1 -> 2 -> 3 -> 4 illuminate.
            val dotY = h * 0.824f
            val dotXs = floatArrayOf(0.408f, 0.464f, 0.520f, 0.576f)
            dotXs.forEachIndexed { index, nx ->
                val center = Offset(w * nx, dotY)
                drawCircle(
                    color = Color(0xFF514566),
                    radius = w * 0.0145f,
                    center = center
                )
                if (index < activeDots) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.98f),
                                Color(0xFFD7BBFF).copy(alpha = 0.88f),
                                Color(0xFF9B6BE8).copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = w * 0.027f
                        ),
                        radius = w * 0.027f,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFF4EBFF),
                        radius = w * 0.0105f,
                        center = center
                    )
                }
            }
        }
    }
}

private fun phase(value: Float, start: Float, end: Float): Float {
    if (end <= start) return if (value >= end) 1f else 0f
    return ((value - start) / (end - start)).coerceIn(0f, 1f)
}

private fun smoothStep(value: Float): Float {
    val t = value.coerceIn(0f, 1f)
    return t * t * (3f - 2f * t)
}
