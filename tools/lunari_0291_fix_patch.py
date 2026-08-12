from pathlib import Path
import re

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
EXPECTED_BASE_BLOB = "aaaa2fd0f4480d3358966c5978143a01338d56f7"

s = HOME_KT.read_text(encoding="utf-8")


def replace_once(old: str, new: str, label: str) -> None:
    global s
    count = s.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly 1 match, got {count}")
    s = s.replace(old, new, 1)


def regex_once(pattern: str, replacement: str, label: str) -> None:
    global s
    s2, count = re.subn(pattern, replacement, s, count=1, flags=re.S)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly 1 regex match, got {count}")
    s = s2


# Imports required by the deterministic overlay/glow implementation.
replace_once(
    "import androidx.compose.foundation.Image\n",
    "import androidx.compose.foundation.Image\nimport androidx.compose.foundation.Canvas\n",
    "Canvas import",
)
replace_once(
    "import androidx.compose.foundation.lazy.LazyColumn\n",
    "import androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.rememberLazyListState\n",
    "LazyListState import",
)
replace_once(
    "import androidx.compose.ui.graphics.vector.ImageVector\n",
    "import androidx.compose.ui.graphics.vector.ImageVector\nimport androidx.compose.ui.geometry.Offset\n",
    "Offset import",
)
replace_once(
    "import androidx.compose.ui.unit.sp\n",
    "import androidx.compose.ui.unit.sp\nimport androidx.compose.ui.zIndex\n",
    "zIndex import",
)

# Keep the hero/cards exactly where they were. Track only the list scroll so the HUD can
# live in a safe top overlay while still scrolling away with the hero as before.
replace_once(
    '    val currencyBalance = prefs.getInt("currency_balance", 1250)\n',
    '    val currencyBalance = prefs.getInt("currency_balance", 1250)\n    val listState = rememberLazyListState()\n',
    "remember list state",
)
replace_once(
    "            LazyColumn(\n                modifier = Modifier.fillMaxSize().padding(inner).statusBarsPadding(),",
    "            LazyColumn(\n                state = listState,\n                modifier = Modifier.fillMaxSize().padding(inner).statusBarsPadding(),",
    "attach list state",
)

# Remove the old HUD row from inside the hero. The 0.2.9 negative Y offset moved drawing
# above the hero item boundary, where it was clipped on the real device.
regex_once(
    r'''    Box\(Modifier\.fillMaxWidth\(\)\.height\(168\.dp\)\) \{\n        Row\(\n.*?\n        \}\n\n        Column\(''',
    '''    Box(Modifier.fillMaxWidth().height(168.dp)) {\n        Column(''',
    "remove clipped hero HUD",
)

overlay_function = r'''
@Composable
private fun LunariHudOverlay0291(
    subscriptionText: String,
    currencyText: String,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onAccount: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            // Approved reference: ~20 px gap below system area after the 75% upward move.
            // Use a safe inset + dp gap instead of a negative Y offset so the HUD cannot be clipped.
            .padding(top = 8.dp, end = 21.dp)
            .zIndex(30f)
            .graphicsLayer {
                translationY = if (listState.firstVisibleItemIndex == 0) {
                    -listState.firstVisibleItemScrollOffset.toFloat()
                } else {
                    -10000f
                }
            },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LunariCounter028(
                width = 100.dp,
                iconText = "☾",
                text = subscriptionText
            )
            LunariCounter028(
                width = 64.dp,
                iconText = "✦",
                text = currencyText
            )
            Surface(
                modifier = Modifier
                    .size(31.dp)
                    .shadow(7.dp, CircleShape, clip = false)
                    .clickable(onClick = onAccount),
                shape = CircleShape,
                color = Color(0xB8101935),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Ivory026.copy(alpha = .52f),
                            LavenderGlow026.copy(alpha = .30f),
                            Color.White.copy(alpha = .12f)
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier.background(
                        Brush.radialGradient(listOf(Color(0x38DDCFFF), Color.Transparent))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = "Аккаунт Lunari",
                        tint = Ivory026,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

'''
replace_once(
    "@Composable\nprivate fun LunariHero028(\n",
    overlay_function + "@Composable\nprivate fun LunariHero028(\n",
    "HUD overlay function",
)

# Insert the overlay as the last child of the root Box: this guarantees it is above the
# background, hero and Scaffold while the status-bar inset keeps it safely visible.
replace_once(
    '''        }
    }

    if (showCreate) {''',
    '''        }

        LunariHudOverlay0291(
            subscriptionText = "PRO · $subscriptionDays дней",
            currencyText = formatBalance028(currencyBalance),
            listState = listState,
            onAccount = { navigate(Route.Settings, "account") }
        )
    }

    if (showCreate) {''',
    "HUD overlay insertion",
)

# Preserve the two 0.2.9 changes the user explicitly accepted.
artwork_modifier = "                modifier = Modifier.fillMaxSize().clip(CircleShape)\n"
artwork_modifier_new = '''                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = if (imageRes == R.drawable.lunari_snapshots) (-4).dp else 0.dp)
                    .clip(CircleShape)
'''
replace_once(artwork_modifier, artwork_modifier_new, "snapshot artwork position")
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

# Card glow fix. The rejected 0.2.9 implementation drew the radial glow inside the card
# content, where Surface/shape clipping and the ornament layer made it effectively invisible.
# 0.2.9.1 wraps the unchanged card body and paints four soft halos OUTSIDE its clipped surface,
# then paints small deterministic glints on top of the long borders.
card_wrapper = r'''
@Composable
private fun LunariCard026(
    imageRes: Int,
    overlayRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(107.dp)
    ) {
        LunariCardCornerGlow0291()
        LunariCardBody0291(
            imageRes = imageRes,
            overlayRes = overlayRes,
            title = title,
            subtitle = subtitle,
            onClick = onClick
        )
        LunariCardEdgeGlints0291(title)
    }
}

@Composable
private fun LunariCardCornerGlow0291() {
    Box(Modifier.fillMaxSize()) {
        LunariCornerHalo0291(
            Modifier.align(Alignment.TopStart).offset(x = (-28).dp, y = (-28).dp)
        )
        LunariCornerHalo0291(
            Modifier.align(Alignment.TopEnd).offset(x = 28.dp, y = (-28).dp)
        )
        LunariCornerHalo0291(
            Modifier.align(Alignment.BottomStart).offset(x = (-28).dp, y = 28.dp)
        )
        LunariCornerHalo0291(
            Modifier.align(Alignment.BottomEnd).offset(x = 28.dp, y = 28.dp)
        )
    }
}

@Composable
private fun LunariCornerHalo0291(modifier: Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val silver = Color(0xFFF3EEFF)
        val radius = size.minDimension / 2f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    silver.copy(alpha = .20f),
                    silver.copy(alpha = .085f),
                    Color.Transparent
                ),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = radius
            ),
            radius = radius,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}

@Composable
private fun LunariCardEdgeGlints0291(seed: String) {
    val (topGlints, bottomGlints) = when (seed) {
        "Персонажи" -> listOf(.14f, .39f, .71f, .93f) to listOf(.27f, .48f, .76f, .88f)
        "Миры" -> listOf(.21f, .54f, .82f) to listOf(.10f, .36f, .64f, .91f)
        "Чаты" -> listOf(.16f, .43f, .67f, .89f) to listOf(.24f, .52f, .79f)
        "Память" -> listOf(.12f, .33f, .61f, .86f) to listOf(.20f, .46f, .72f, .94f)
        "Профили" -> listOf(.18f, .49f, .77f, .92f) to listOf(.13f, .38f, .66f, .84f)
        "Снимки" -> listOf(.09f, .29f, .58f, .81f) to listOf(.17f, .44f, .69f, .90f)
        else -> listOf(.15f, .47f, .79f) to listOf(.28f, .62f, .91f)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val silver = Color(0xFFF3EEFF)
        val halfWidth = 6.dp.toPx()
        val verticalHalf = 2.1.dp.toPx()
        val strokeWidth = .75.dp.toPx()
        val pointRadius = .95.dp.toPx()
        val topY = 1.6.dp.toPx()
        val bottomY = size.height - 1.6.dp.toPx()

        fun drawGlint(fraction: Float, y: Float) {
            val x = size.width * fraction
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        silver.copy(alpha = .36f),
                        Color.Transparent
                    ),
                    startX = x - halfWidth,
                    endX = x + halfWidth
                ),
                start = Offset(x - halfWidth, y),
                end = Offset(x + halfWidth, y),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = silver.copy(alpha = .30f),
                start = Offset(x, y - verticalHalf),
                end = Offset(x, y + verticalHalf),
                strokeWidth = strokeWidth
            )
            drawCircle(
                color = silver.copy(alpha = .48f),
                radius = pointRadius,
                center = Offset(x, y)
            )
        }

        topGlints.forEach { drawGlint(it, topY) }
        bottomGlints.forEach { drawGlint(it, bottomY) }
    }
}

'''
replace_once(
    "@Composable\nprivate fun LunariCard026(\n",
    card_wrapper + "@Composable\nprivate fun LunariCardBody0291(\n",
    "card wrapper and glow functions",
)

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9.1 HUD visibility and card glow fix applied")
