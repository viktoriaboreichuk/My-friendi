from pathlib import Path

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
EXPECTED_BASE_BLOB = "aaaa2fd0f4480d3358966c5978143a01338d56f7"

s = HOME_KT.read_text(encoding="utf-8")


def replace_once(old: str, new: str, label: str) -> None:
    global s
    count = s.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly 1 match, got {count}")
    s = s.replace(old, new, 1)


replace_once(
    "import androidx.compose.foundation.Image\n",
    "import androidx.compose.foundation.Image\nimport androidx.compose.foundation.Canvas\n",
    "Canvas import",
)
replace_once(
    "import androidx.compose.ui.graphics.vector.ImageVector\n",
    "import androidx.compose.ui.graphics.vector.ImageVector\nimport androidx.compose.ui.geometry.Offset\n",
    "Offset import",
)

# 1. Approved HUD placement: reference move ~26 px left / ~58 px up.
# On the current Compose/density baseline this is represented as -12 dp X / -26 dp Y.
replace_once(
    "                .offset(x = 14.dp)\n                .padding(top = 10.dp),",
    "                .offset(x = 2.dp, y = (-26).dp)\n                .padding(top = 10.dp),",
    "approved HUD position",
)

# 2. Add the approved subdued silver glow and non-aligned long-edge glints.
overlay_block = '''            Box(Modifier.matchParentSize().clip(cardShape)) {
                Image(
                    painter = painterResource(overlayRes),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(scaleX = 1.061f, scaleY = 1.34f),
                    contentScale = ContentScale.FillBounds
                )
            }

'''
replace_once(
    overlay_block,
    overlay_block + "            LunariCardGlow029(title)\n\n",
    "card glow insertion",
)

# 3. Recenter only the camera artwork on the Snapshots card.
# Keep its size/content untouched: position only.
artwork_modifier = "                modifier = Modifier.fillMaxSize().clip(CircleShape)\n"
artwork_modifier_new = '''                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = if (imageRes == R.drawable.lunari_snapshots) (-4).dp else 0.dp)
                    .clip(CircleShape)
'''
replace_once(artwork_modifier, artwork_modifier_new, "snapshot artwork centering")

# 4. All four side bottom-nav icons use the same lavender tint. Labels are untouched.
replace_once(
    "                tint = if (active) Ivory026 else Lavender026.copy(alpha = .90f),",
    "                tint = Lavender026.copy(alpha = .90f),",
    "bottom nav equal icon color",
)
replace_once(
    '''            if (showSparkle) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Ivory026,
                    modifier = Modifier.align(Alignment.TopEnd).size(8.dp)
                )
            }
''',
    '''            if (showSparkle) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Lavender026.copy(alpha = .90f),
                    modifier = Modifier.align(Alignment.TopEnd).size(8.dp)
                )
            }
''',
    "bottom nav sparkle color",
)

glow_function = r'''
@Composable
private fun LunariCardGlow029(seed: String) {
    val (topGlints, bottomGlints) = when (seed) {
        "Персонажи" -> listOf(.17f, .46f, .81f) to listOf(.29f, .69f, .88f)
        "Миры" -> listOf(.24f, .57f, .91f) to listOf(.14f, .48f, .75f)
        "Чаты" -> listOf(.11f, .39f, .72f) to listOf(.26f, .61f, .84f)
        "Память" -> listOf(.21f, .63f, .86f) to listOf(.35f, .55f, .92f)
        "Профили" -> listOf(.14f, .52f, .77f) to listOf(.23f, .66f, .89f)
        "Снимки" -> listOf(.28f, .59f, .83f) to listOf(.12f, .44f, .73f)
        else -> listOf(.19f, .51f, .82f) to listOf(.31f, .64f, .90f)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val silver = Color(0xFFF1ECFF)
        val cornerRadius = 18.dp.toPx()
        val corners = listOf(
            Offset(0f, 0f),
            Offset(size.width, 0f),
            Offset(0f, size.height),
            Offset(size.width, size.height)
        )

        corners.forEach { center ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        silver.copy(alpha = .22f),
                        silver.copy(alpha = .08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = cornerRadius
                ),
                radius = cornerRadius,
                center = center
            )
        }

        val halfWidth = 8.dp.toPx()
        val strokeWidth = 1.dp.toPx()
        val pointRadius = 1.05.dp.toPx()

        fun drawGlint(fraction: Float, y: Float) {
            val x = size.width * fraction
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        silver.copy(alpha = .30f),
                        Color.Transparent
                    ),
                    startX = x - halfWidth,
                    endX = x + halfWidth
                ),
                start = Offset(x - halfWidth, y),
                end = Offset(x + halfWidth, y),
                strokeWidth = strokeWidth
            )
            drawCircle(
                color = silver.copy(alpha = .34f),
                radius = pointRadius,
                center = Offset(x, y)
            )
        }

        topGlints.forEach { drawGlint(it, 0f) }
        bottomGlints.forEach { drawGlint(it, size.height) }
    }
}

'''
marker = "@Composable\nprivate fun LunariArtworkFrame028(imageRes: Int) {\n"
replace_once(marker, glow_function + marker, "glow function")

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9 approved polish patch applied")
