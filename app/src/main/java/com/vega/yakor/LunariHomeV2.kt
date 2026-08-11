package com.vega.yakor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night025 = Color(0xFF071020)
private val Lavender025 = Color(0xFFD9CCFF)
private val LavenderSoft025 = Color(0xFFB9B4D9)
private val LavenderGlow025 = Color(0xFFA881FF)
private val Ivory025 = Color(0xFFF7F1FB)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    Box(Modifier.fillMaxSize().background(Night025)) {
        // 0.2.6 rework: restore the approved full-resolution moon/cloud/castle background.
        // Do not place a dark full-screen overlay over it: the background is part of the composition.
        Image(
            painter = painterResource(R.drawable.lunari_home_bg_v025),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LunariBottom025(
                    onAdd = {
                        val c = CharacterCard()
                        store.upsertCharacter(c)
                        navigate(Route.CharacterEdit, c.id)
                    },
                    onMagic = { navigate(Route.Settings, null) },
                    onProfile = { navigate(Route.Profiles, null) }
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(start = 17.dp, end = 17.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { LunariHero026(onMagic = { navigate(Route.Settings, null) }) }
                item { LunariCard026(R.drawable.lunari_character, "Персонажи", "твои герои, их характеры\nи истории", 0) { navigate(Route.Characters, null) } }
                item { LunariCard026(R.drawable.lunari_world, "Миры", "создай свои вселенные\nи локации", 1) { navigate(Route.Worlds, null) } }
                item { LunariCard026(R.drawable.lunari_chat, "Чаты", "общайся с персонажами\nи развивай истории", 2) { navigate(Route.Chats, null) } }
                item { LunariCard026(R.drawable.lunari_memory, "Память", "всё важное, что стоит\nсохранить", 3) { navigate(Route.Memories, "") } }
                item { LunariCard026(R.drawable.lunari_profiles, "Профили", "твоя роль, голос\nи образ в историях", 4) { navigate(Route.Profiles, null) } }
                item { LunariCard026(R.drawable.lunari_snapshots, "Снимки", "точки сохранения\nдля важных моментов", 5) { navigate(Route.Snapshots, null) } }
            }
        }
    }
}

@Composable
private fun LunariHero026(onMagic: () -> Unit) {
    // The height is intentionally larger than 0.2.5/first 0.2.6 so the exact
    // approved wordmark sits in the same relationship to the large background crescent.
    Box(Modifier.fillMaxWidth().height(245.dp)) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 5.dp, end = 1.dp)
                .size(38.dp)
                .shadow(7.dp, CircleShape)
                .clickable(onClick = onMagic),
            shape = CircleShape,
            color = Color(0x40101935),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Ivory025.copy(alpha = .42f),
                        LavenderGlow025.copy(alpha = .22f),
                        Color.White.copy(alpha = .10f)
                    )
                )
            )
        ) {
            Box(
                modifier = Modifier.background(
                    Brush.radialGradient(listOf(Color(0x2EDDCFFF), Color.Transparent))
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = "Настройки ИИ",
                    tint = Ivory025,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Exact artwork cropped from the user's approved reference:
            // ornate LUNARI wordmark + crescent divider + fading side lines.
            // This deliberately replaces the system Serif text, separate star and glyph crescent.
            Image(
                painter = painterResource(R.drawable.lunari_logo_026),
                contentDescription = "Lunari",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(.76f)
                    .aspectRatio(480f / 152f)
            )

            Text(
                "твои персонажи, миры и истории",
                color = Lavender025.copy(alpha = .94f),
                fontSize = 14.sp,
                letterSpacing = .16.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun LunariCard026(
    imageRes: Int,
    title: String,
    subtitle: String,
    ornamentVariant: Int,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(26.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(107.dp)
            .shadow(5.dp, cardShape, clip = false)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Ivory025.copy(alpha = .72f),
                    Lavender025.copy(alpha = .50f),
                    LavenderGlow025.copy(alpha = .38f),
                    Ivory025.copy(alpha = .48f)
                )
            )
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                // HARD LOCK: keep the transparency that the user approved in the first 0.2.6 pass.
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0x560A1430),
                            Color(0x420A132C),
                            Color(0x2B080F24)
                        )
                    )
                )
        ) {
            // Soft glass highlight near the top edge.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(29.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = .055f), Color.Transparent)
                        )
                    )
            )

            // Fine luminous inner edge: closer to the second reference than a flat grey outline.
            Canvas(Modifier.matchParentSize()) {
                val radius = 26.dp.toPx()
                drawRoundRect(
                    color = LavenderGlow025.copy(alpha = .12f),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = 2.2.dp.toPx())
                )
                drawRoundRect(
                    color = Ivory025.copy(alpha = .18f),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = .55.dp.toPx())
                )
            }

            LunariCardOrnament026(
                variant = ornamentVariant,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(148.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 11.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HARD LOCK: approved circular artwork and its ring stay exactly as in 0.2.5.
                LunariArtworkFrame025(imageRes)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(
                        title,
                        color = Ivory025,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        subtitle,
                        color = LavenderSoft025,
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Icon(
                    Icons.Outlined.ArrowForwardIos,
                    contentDescription = null,
                    tint = Lavender025.copy(alpha = .92f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LunariCardOrnament026(
    variant: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(vertical = 4.dp, horizontal = 3.dp)) {
        val vine = Lavender025.copy(alpha = .34f)
        val fine = Ivory025.copy(alpha = .25f)
        val glow = LavenderGlow025.copy(alpha = .22f)
        val stroke = .72.dp.toPx()
        val fineStroke = .52.dp.toPx()
        val v = variant % 3

        fun leaf(x: Float, y: Float, sx: Float, sy: Float, flip: Float = 1f) {
            val cx = size.width * x
            val cy = size.height * y
            val rx = size.width * sx
            val ry = size.height * sy
            val p = Path().apply {
                moveTo(cx, cy)
                cubicTo(
                    cx + rx * .75f * flip, cy - ry * .18f,
                    cx + rx * .95f * flip, cy + ry * .72f,
                    cx, cy + ry
                )
                cubicTo(
                    cx - rx * .30f * flip, cy + ry * .70f,
                    cx - rx * .18f * flip, cy + ry * .20f,
                    cx, cy
                )
            }
            drawPath(p, vine, style = Stroke(width = stroke))
            drawLine(
                fine,
                Offset(cx, cy + ry * .10f),
                Offset(cx, cy + ry * .83f),
                strokeWidth = fineStroke
            )
        }

        fun sparkle(x: Float, y: Float, r: Float) {
            val c = Offset(size.width * x, size.height * y)
            val rr = r.dp.toPx()
            drawLine(fine, Offset(c.x - rr, c.y), Offset(c.x + rr, c.y), strokeWidth = fineStroke)
            drawLine(fine, Offset(c.x, c.y - rr), Offset(c.x, c.y + rr), strokeWidth = fineStroke)
            drawCircle(Ivory025.copy(alpha = .42f), radius = .7.dp.toPx(), center = c)
        }

        when (v) {
            0 -> {
                val stem = Path().apply {
                    moveTo(size.width * .98f, size.height * .03f)
                    cubicTo(size.width * .74f, size.height * .08f, size.width * .91f, size.height * .31f, size.width * .66f, size.height * .35f)
                    cubicTo(size.width * .47f, size.height * .39f, size.width * .56f, size.height * .58f, size.width * .74f, size.height * .58f)
                    cubicTo(size.width * .96f, size.height * .59f, size.width * .88f, size.height * .84f, size.width * .61f, size.height * .91f)
                }
                drawPath(stem, vine, style = Stroke(width = stroke))
                val curl = Path().apply {
                    moveTo(size.width * .77f, size.height * .17f)
                    cubicTo(size.width * .59f, size.height * .04f, size.width * .46f, size.height * .17f, size.width * .58f, size.height * .27f)
                    cubicTo(size.width * .66f, size.height * .33f, size.width * .73f, size.height * .25f, size.width * .67f, size.height * .20f)
                }
                drawPath(curl, glow, style = Stroke(width = stroke))
                leaf(.81f, .18f, .075f, .10f, -1f)
                leaf(.67f, .34f, .070f, .095f, -1f)
                leaf(.76f, .57f, .070f, .095f, 1f)
                leaf(.70f, .78f, .075f, .11f, -1f)
                sparkle(.94f, .08f, 4.2f)
                sparkle(.58f, .91f, 2.7f)
            }

            1 -> {
                val stem = Path().apply {
                    moveTo(size.width * .98f, size.height * .95f)
                    cubicTo(size.width * .77f, size.height * .90f, size.width * .91f, size.height * .67f, size.width * .64f, size.height * .62f)
                    cubicTo(size.width * .42f, size.height * .57f, size.width * .51f, size.height * .38f, size.width * .72f, size.height * .38f)
                    cubicTo(size.width * .94f, size.height * .37f, size.width * .90f, size.height * .13f, size.width * .68f, size.height * .08f)
                }
                drawPath(stem, vine, style = Stroke(width = stroke))
                val curl = Path().apply {
                    moveTo(size.width * .72f, size.height * .74f)
                    cubicTo(size.width * .55f, size.height * .88f, size.width * .43f, size.height * .75f, size.width * .56f, size.height * .66f)
                    cubicTo(size.width * .65f, size.height * .60f, size.width * .72f, size.height * .68f, size.width * .66f, size.height * .73f)
                }
                drawPath(curl, glow, style = Stroke(width = stroke))
                leaf(.82f, .73f, .075f, .10f, -1f)
                leaf(.66f, .60f, .070f, .095f, 1f)
                leaf(.74f, .37f, .070f, .095f, -1f)
                leaf(.69f, .15f, .075f, .105f, 1f)
                sparkle(.95f, .90f, 4.0f)
                sparkle(.55f, .10f, 2.5f)
            }

            else -> {
                val stem = Path().apply {
                    moveTo(size.width * .98f, size.height * .08f)
                    cubicTo(size.width * .69f, size.height * .08f, size.width * .84f, size.height * .31f, size.width * .59f, size.height * .39f)
                    cubicTo(size.width * .42f, size.height * .45f, size.width * .43f, size.height * .61f, size.width * .60f, size.height * .66f)
                    cubicTo(size.width * .82f, size.height * .73f, size.width * .69f, size.height * .92f, size.width * .96f, size.height * .94f)
                }
                drawPath(stem, vine, style = Stroke(width = stroke))
                val upperCurl = Path().apply {
                    moveTo(size.width * .79f, size.height * .23f)
                    cubicTo(size.width * .60f, size.height * .11f, size.width * .48f, size.height * .22f, size.width * .58f, size.height * .31f)
                }
                val lowerCurl = Path().apply {
                    moveTo(size.width * .68f, size.height * .69f)
                    cubicTo(size.width * .49f, size.height * .83f, size.width * .43f, size.height * .70f, size.width * .55f, size.height * .62f)
                }
                drawPath(upperCurl, glow, style = Stroke(width = stroke))
                drawPath(lowerCurl, glow, style = Stroke(width = stroke))
                leaf(.79f, .21f, .070f, .095f, -1f)
                leaf(.61f, .39f, .065f, .09f, -1f)
                leaf(.59f, .63f, .065f, .09f, 1f)
                leaf(.78f, .80f, .070f, .10f, 1f)
                sparkle(.94f, .10f, 3.8f)
                sparkle(.92f, .91f, 2.8f)
            }
        }

        // Tiny constellation-like accents keep the ornament airy, as in the reference.
        drawCircle(Ivory025.copy(alpha = .36f), radius = 1.0.dp.toPx(), center = Offset(size.width * .86f, size.height * .48f))
        drawCircle(LavenderGlow025.copy(alpha = .30f), radius = .8.dp.toPx(), center = Offset(size.width * .54f, size.height * .52f))
    }
}

@Composable
private fun LunariArtworkFrame025(imageRes: Int) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .shadow(9.dp, CircleShape, clip = false)
            .background(
                Brush.sweepGradient(
                    listOf(
                        Color(0xFF6D5ACF),
                        Color(0xFFE7DDF5),
                        Color(0xFF9277D0),
                        Color(0xFF493C79),
                        Color(0xFFDCCFFF),
                        Color(0xFF6D5ACF)
                    )
                ),
                CircleShape
            )
            .padding(1.6.dp)
            .background(Color(0xFF11162E), CircleShape)
            .padding(3.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape)
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = .07f),
                        Color.Transparent,
                        Color(0xFF5D42A1).copy(alpha = .08f)
                    )
                )
            )
        )
    }
}

@Composable
private fun LunariBottom025(onAdd: () -> Unit, onMagic: () -> Unit, onProfile: () -> Unit) {
    Box(
        Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 15.dp, vertical = 6.dp).height(71.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(63.dp)
                .align(Alignment.BottomCenter)
                .shadow(12.dp, RoundedCornerShape(29.dp), clip = false),
            shape = RoundedCornerShape(29.dp),
            color = Color(0xF0071025),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Lavender025.copy(alpha = .20f),
                        Ivory025.copy(alpha = .10f),
                        LavenderGlow025.copy(alpha = .18f)
                    )
                )
            )
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Home, "Главная", tint = Ivory025, modifier = Modifier.size(22.dp))
                        Text("Главная", color = Ivory025.copy(alpha = .90f), fontSize = 8.5.sp, lineHeight = 10.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onMagic, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, "ИИ", tint = Lavender025, modifier = Modifier.size(22.dp))
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onProfile, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Outlined.PersonOutline, "Профиль", tint = Lavender025.copy(alpha = .88f), modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(55.dp)
                .shadow(13.dp, CircleShape, clip = false)
                .clickable(onClick = onAdd),
            shape = CircleShape,
            color = Color(0xFF7150D2),
            border = BorderStroke(1.dp, Ivory025.copy(alpha = .72f))
        ) {
            Box(
                Modifier.background(
                    Brush.radialGradient(
                        listOf(Color(0xFFB09AFF), Color(0xFF7958DB), Color(0xFF4F359F))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Создать", tint = Ivory025, modifier = Modifier.size(29.dp))
            }
        }
    }
}
