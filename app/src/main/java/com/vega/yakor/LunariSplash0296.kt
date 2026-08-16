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
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Mobile portrait splash revision for the 0.2.9.7 test build.
 *
 * The approved 941x1672 artwork remains the visual base. Animation follows the
 * storyboard more literally than the first test: moon wake-up -> several lunar
 * ribbons grow under the logo -> dust/particles fill the curl -> logo/star gain
 * light -> quiet loop. The heavy entrance plays once and lasts about 4.4s; the
 * splash stays up for a minimum of 5s before fading to the already composed app.
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
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lunari-splash-breathe"
    )

    LaunchedEffect(Unit) {
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4400, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            delay(520)
            activeDots = if (activeDots >= 4) 1 else activeDots + 1
        }
    }

    LaunchedEffect(Unit) {
        // Minimum viewing time requested for this revision.
        delay(5000)
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
            contentScale = ContentScale.FillBounds
        )

        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val e = entrance.value.coerceIn(0f, 1f)

            val moonProgress = smoothStep(phase(e, 0.00f, 0.22f))
            val ribbon1Progress = smoothStep(phase(e, 0.10f, 0.56f))
            val ribbon2Progress = smoothStep(phase(e, 0.20f, 0.68f))
            val ribbon3Progress = smoothStep(phase(e, 0.30f, 0.80f))
            val logoProgress = smoothStep(phase(e, 0.30f, 0.82f))

            // Moon wakes first: restrained glow only, no flash and no large fog.
            val moonBreath = 0.90f + loop * 0.10f
            val moonAlpha = moonProgress * moonBreath
            val moonCenter = Offset(w * 0.235f, h * 0.395f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF8F2FF).copy(alpha = 0.13f * moonAlpha),
                        Color(0xFFC5A5FF).copy(alpha = 0.065f * moonAlpha),
                        Color.Transparent
                    ),
                    center = moonCenter,
                    radius = w * 0.22f
                ),
                radius = w * 0.22f,
                center = moonCenter
            )

            // The storyboard reads as a luminous ribbon, not a single line.
            // Build three related curves with staggered reveal timings.
            val path1 = AndroidPath().apply {
                moveTo(w * 0.175f, h * 0.492f)
                cubicTo(w * 0.31f, h * 0.470f, w * 0.43f, h * 0.518f, w * 0.59f, h * 0.503f)
                cubicTo(w * 0.71f, h * 0.492f, w * 0.82f, h * 0.455f, w * 0.884f, h * 0.429f)
                cubicTo(w * 0.927f, h * 0.411f, w * 0.942f, h * 0.452f, w * 0.885f, h * 0.478f)
            }
            val path2 = AndroidPath().apply {
                moveTo(w * 0.190f, h * 0.481f)
                cubicTo(w * 0.34f, h * 0.494f, w * 0.47f, h * 0.505f, w * 0.62f, h * 0.489f)
                cubicTo(w * 0.74f, h * 0.476f, w * 0.86f, h * 0.428f, w * 0.902f, h * 0.444f)
                cubicTo(w * 0.936f, h * 0.457f, w * 0.912f, h * 0.486f, w * 0.858f, h * 0.488f)
            }
            val path3 = AndroidPath().apply {
                moveTo(w * 0.205f, h * 0.501f)
                cubicTo(w * 0.34f, h * 0.519f, w * 0.50f, h * 0.527f, w * 0.65f, h * 0.505f)
                cubicTo(w * 0.79f, h * 0.485f, w * 0.900f, h * 0.445f, w * 0.925f, h * 0.413f)
                cubicTo(w * 0.953f, h * 0.379f, w * 0.972f, h * 0.454f, w * 0.900f, h * 0.490f)
            }

            val measures = listOf(
                PathMeasure(path1, false) to ribbon1Progress,
                PathMeasure(path2, false) to ribbon2Progress,
                PathMeasure(path3, false) to ribbon3Progress
            )

            measures.forEachIndexed { index, (measure, progress) ->
                if (progress <= 0.001f) return@forEachIndexed
                val segment = AndroidPath()
                measure.getSegment(0f, measure.length * progress, segment, true)
                val cp = segment.asComposePath()
                val settle = if (e < 0.94f) 1f else 0.70f + loop * 0.12f
                val intensity = when (index) {
                    0 -> 1.00f
                    1 -> 0.78f
                    else -> 0.62f
                }

                drawPath(
                    path = cp,
                    color = Color(0xFF7E3EEA).copy(alpha = 0.10f * intensity * settle),
                    style = Stroke(width = w * (0.027f - index * 0.003f))
                )
                drawPath(
                    path = cp,
                    color = Color(0xFFA85DFF).copy(alpha = 0.30f * intensity * settle),
                    style = Stroke(width = w * (0.0105f - index * 0.0015f))
                )
                drawPath(
                    path = cp,
                    color = Color(0xFFE4CEFF).copy(alpha = 0.78f * intensity * settle),
                    style = Stroke(width = w * (0.0028f - index * 0.00035f))
                )

                // A bright moving head makes the motion readable like the storyboard.
                val tip = FloatArray(2)
                if (measure.getPosTan(measure.length * progress, tip, null)) {
                    val tipOffset = Offset(tip[0], tip[1])
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.90f * intensity),
                                Color(0xFFC790FF).copy(alpha = 0.44f * intensity),
                                Color.Transparent
                            ),
                            center = tipOffset,
                            radius = w * 0.033f
                        ),
                        center = tipOffset,
                        radius = w * 0.033f
                    )
                }
            }

            // Particle train follows the main ribbon, then settles into a quiet shimmer.
            val mainMeasure = PathMeasure(path1, false)
            for (i in 0 until 18) {
                val lag = i * 0.033f
                val p = (ribbon1Progress - lag).coerceIn(0f, 1f)
                if (p <= 0.015f) continue
                val pos = FloatArray(2)
                if (mainMeasure.getPosTan(mainMeasure.length * p, pos, null)) {
                    val wobble = ((i % 5) - 2) * w * 0.0042f
                    val pulse = if (i % 2 == 0) loop else 1f - loop
                    val center = Offset(pos[0], pos[1] + wobble)
                    val radius = w * (0.0018f + (i % 4) * 0.0007f)
                    drawCircle(
                        color = Color(0xFFE7D2FF).copy(alpha = (0.28f + pulse * 0.42f) * p),
                        radius = radius,
                        center = center
                    )
                }
            }

            // Additional dust blooms progressively around the right-hand curl.
            val curlDust = listOf(
                0.55f to 0.506f,
                0.61f to 0.499f,
                0.67f to 0.489f,
                0.72f to 0.477f,
                0.77f to 0.462f,
                0.81f to 0.447f,
                0.85f to 0.433f,
                0.89f to 0.427f,
                0.91f to 0.445f,
                0.89f to 0.469f,
                0.85f to 0.484f,
                0.79f to 0.493f
            )
            curlDust.forEachIndexed { index, (nx, ny) ->
                val gate = smoothStep(phase(ribbon3Progress, index * 0.052f, index * 0.052f + 0.25f))
                if (gate > 0f) {
                    val pulse = 0.52f + 0.48f * if (index % 2 == 0) loop else 1f - loop
                    drawCircle(
                        color = Color(0xFFEAD9FF).copy(alpha = 0.58f * gate * pulse),
                        radius = w * (0.0023f + (index % 3) * 0.0010f),
                        center = Offset(w * nx, h * ny)
                    )
                }
            }

            // Logo itself stays fixed. Passing moonlight raises its local luminosity.
            val logoBreath = 0.91f + loop * 0.09f
            val logoCenter = Offset(w * 0.55f, h * 0.445f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF0E6FF).copy(alpha = 0.090f * logoProgress * logoBreath),
                        Color(0xFFAA78F2).copy(alpha = 0.032f * logoProgress * logoBreath),
                        Color.Transparent
                    ),
                    center = logoCenter,
                    radius = w * 0.40f
                ),
                center = logoCenter,
                radius = w * 0.40f
            )

            // Small star accent near the logo: one readable pulse late in the entrance.
            val starRise = smoothStep(phase(e, 0.69f, 0.79f))
            val starFall = 1f - smoothStep(phase(e, 0.82f, 0.95f))
            val starAccent = (starRise * starFall).coerceIn(0f, 1f)
            val starAlpha = (0.12f + starAccent * 0.74f + loop * 0.04f).coerceAtMost(0.88f)
            val star = Offset(w * 0.374f, h * 0.354f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f * starAlpha),
                        Color(0xFFC8A5FF).copy(alpha = 0.22f * starAlpha),
                        Color.Transparent
                    ),
                    center = star,
                    radius = w * 0.047f
                ),
                center = star,
                radius = w * 0.047f
            )
            drawLine(
                color = Color(0xFFF7EEFF).copy(alpha = starAlpha),
                start = Offset(star.x - w * 0.030f, star.y),
                end = Offset(star.x + w * 0.030f, star.y),
                strokeWidth = w * 0.0012f
            )
            drawLine(
                color = Color(0xFFF7EEFF).copy(alpha = starAlpha),
                start = Offset(star.x, star.y - w * 0.030f),
                end = Offset(star.x, star.y + w * 0.030f),
                strokeWidth = w * 0.0012f
            )

            // Quiet star micro-animation only; ornaments/clouds remain static.
            val microStars = listOf(
                0.290f to 0.181f,
                0.659f to 0.184f,
                0.748f to 0.301f,
                0.293f to 0.610f,
                0.744f to 0.603f,
                0.572f to 0.667f
            )
            microStars.forEachIndexed { index, (nx, ny) ->
                val pulse = if (index % 2 == 0) loop else 1f - loop
                drawCircle(
                    color = Color(0xFFE7D7FF).copy(alpha = 0.10f + pulse * 0.20f),
                    radius = w * 0.0021f,
                    center = Offset(w * nx, h * ny)
                )
            }

            // Exact centers measured from the approved 941x1672 artwork.
            // First erase the four baked static dots at their real coordinates,
            // then draw one animated row in exactly the same place (no ghost row).
            val dotY = h * 0.83050f
            val dotXs = floatArrayOf(0.41369f, 0.47011f, 0.52704f, 0.58453f)
            val localBackgrounds = listOf(
                Color(0xFF111432),
                Color(0xFF302D59),
                Color(0xFF19173C),
                Color(0xFF141438)
            )
            dotXs.forEachIndexed { index, nx ->
                val center = Offset(w * nx, dotY)
                val bg = localBackgrounds[index]
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(bg, bg, bg.copy(alpha = 0f)),
                        center = center,
                        radius = w * 0.033f
                    ),
                    radius = w * 0.033f,
                    center = center
                )
                drawCircle(
                    color = Color(0xFF685983).copy(alpha = 0.78f),
                    radius = w * 0.0118f,
                    center = center
                )
                if (index < activeDots) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.97f),
                                Color(0xFFDABFFF).copy(alpha = 0.88f),
                                Color(0xFF9B6BE8).copy(alpha = 0.18f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = w * 0.024f
                        ),
                        radius = w * 0.024f,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFFF4EBFF),
                        radius = w * 0.0098f,
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
