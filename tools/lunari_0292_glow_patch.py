from pathlib import Path
import re
import runpy

# Reuse the already device-approved 0.2.9.1 patch for HUD, equal nav icon tint,
# and the position-only lift of the Snapshots camera. Then replace ONLY the
# rejected corner-halo implementation with a tight contour-following silver glow.
runpy.run_path("tools/lunari_0291_fix_patch.py", run_name="__main__")

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
s = HOME_KT.read_text(encoding="utf-8")

# The 0.2.9.1 radial 56dp circles produced large fog-like blobs between cards.
# 0.2.9.2 draws three concentric 90-degree strokes directly on each rounded
# corner plus short fading tails along the adjoining edges. The effect remains
# attached to the frame and falls off quickly, matching the approved mock.
new_corner_glow = r'''
@Composable
private fun LunariCardCornerGlow0292() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val silver = Color(0xFFF4F0FF)
        val radius = 25.dp.toPx()
        val inset = 1.5.dp.toPx()
        val tail = 17.dp.toPx()

        data class Corner0292(
            val topLeft: Offset,
            val startAngle: Float,
            val horizontalStart: Offset,
            val horizontalEnd: Offset,
            val verticalStart: Offset,
            val verticalEnd: Offset
        )

        val arcSize = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        val corners = listOf(
            Corner0292(
                topLeft = Offset(inset, inset),
                startAngle = 180f,
                horizontalStart = Offset(inset + radius, inset),
                horizontalEnd = Offset(inset + radius + tail, inset),
                verticalStart = Offset(inset, inset + radius),
                verticalEnd = Offset(inset, inset + radius + tail)
            ),
            Corner0292(
                topLeft = Offset(size.width - inset - radius * 2f, inset),
                startAngle = 270f,
                horizontalStart = Offset(size.width - inset - radius, inset),
                horizontalEnd = Offset(size.width - inset - radius - tail, inset),
                verticalStart = Offset(size.width - inset, inset + radius),
                verticalEnd = Offset(size.width - inset, inset + radius + tail)
            ),
            Corner0292(
                topLeft = Offset(inset, size.height - inset - radius * 2f),
                startAngle = 90f,
                horizontalStart = Offset(inset + radius, size.height - inset),
                horizontalEnd = Offset(inset + radius + tail, size.height - inset),
                verticalStart = Offset(inset, size.height - inset - radius),
                verticalEnd = Offset(inset, size.height - inset - radius - tail)
            ),
            Corner0292(
                topLeft = Offset(size.width - inset - radius * 2f, size.height - inset - radius * 2f),
                startAngle = 0f,
                horizontalStart = Offset(size.width - inset - radius, size.height - inset),
                horizontalEnd = Offset(size.width - inset - radius - tail, size.height - inset),
                verticalStart = Offset(size.width - inset, size.height - inset - radius),
                verticalEnd = Offset(size.width - inset, size.height - inset - radius - tail)
            )
        )

        fun drawCornerLayer(corner: Corner0292, alpha: Float, widthDp: Float) {
            val width = widthDp.dp.toPx()
            drawArc(
                color = silver.copy(alpha = alpha),
                startAngle = corner.startAngle,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = corner.topLeft,
                size = arcSize,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = width,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            )
            drawLine(
                color = silver.copy(alpha = alpha * .72f),
                start = corner.horizontalStart,
                end = corner.horizontalEnd,
                strokeWidth = width,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = silver.copy(alpha = alpha * .72f),
                start = corner.verticalStart,
                end = corner.verticalEnd,
                strokeWidth = width,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }

        // Wide low-alpha strokes create a short soft aura; the narrow stroke gives
        // the silver frame-edge sparkle. This is intentionally much tighter and
        // dimmer than the rejected radial clouds.
        corners.forEach { drawCornerLayer(it, alpha = .045f, widthDp = 10f) }
        corners.forEach { drawCornerLayer(it, alpha = .085f, widthDp = 5.5f) }
        corners.forEach { drawCornerLayer(it, alpha = .34f, widthDp = 1.05f) }
    }
}

'''

pattern = re.compile(
    r'@Composable\nprivate fun LunariCardCornerGlow0291\(\) \{.*?\n\}\n\n@Composable\nprivate fun LunariCardEdgeGlints0291',
    re.S,
)
replacement = new_corner_glow + '@Composable\nprivate fun LunariCardEdgeGlints0291'
s2, count = pattern.subn(replacement, s, count=1)
if count != 1:
    raise SystemExit(f"corner glow replacement: expected 1 match, got {count}")
s = s2

old_call = "        LunariCardCornerGlow0291()\n"
new_call = "        LunariCardCornerGlow0292()\n"
if s.count(old_call) != 1:
    raise SystemExit(f"corner glow call: expected 1 match, got {s.count(old_call)}")
s = s.replace(old_call, new_call, 1)

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9.2 tight silver corner glow patch applied")
