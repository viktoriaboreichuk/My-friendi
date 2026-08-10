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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night025 = Color(0xFF071020)
private val DeepNight025 = Color(0xFF040817)
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
        Image(
            painter = painterResource(R.drawable.lunari_home_bg_v025),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    .30f to Color(0x12050A20),
                    .52f to Color(0x39071027),
                    .74f to Color(0x76050B1F),
                    1f to DeepNight025.copy(alpha = .88f)
                )
            )
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
                modifier = Modifier.fillMaxSize().padding(inner).statusBarsPadding(),
                contentPadding = PaddingValues(start = 17.dp, end = 17.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { LunariHero026(onMagic = { navigate(Route.Settings, null) }) }
                item { LunariCard026(R.drawable.lunari_character, "Персонажи", "твои герои, их характеры\nи истории") { navigate(Route.Characters, null) } }
                item { LunariCard026(R.drawable.lunari_world, "Миры", "создай свои вселенные\nи локации") { navigate(Route.Worlds, null) } }
                item { LunariCard026(R.drawable.lunari_chat, "Чаты", "общайся с персонажами\nи развивай истории") { navigate(Route.Chats, null) } }
                item { LunariCard026(R.drawable.lunari_memory, "Память", "всё важное, что стоит\nсохранить") { navigate(Route.Memories, "") } }
                item { LunariCard026(R.drawable.lunari_profiles, "Профили", "твоя роль, голос\nи образ в историях") { navigate(Route.Profiles, null) } }
                item { LunariCard026(R.drawable.lunari_snapshots, "Снимки", "точки сохранения\nдля важных моментов") { navigate(Route.Snapshots, null) } }
            }
        }
    }
}

@Composable
private fun LunariHero026(onMagic: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(183.dp)) {
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
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "Lunari",
                    color = LavenderGlow025.copy(alpha = .22f),
                    fontFamily = FontFamily.Serif,
                    fontSize = 54.sp,
                    lineHeight = 56.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.25).sp,
                    modifier = Modifier.offset(y = 1.dp)
                )
                Text(
                    "Lunari",
                    color = Ivory025,
                    fontFamily = FontFamily.Serif,
                    fontSize = 54.sp,
                    lineHeight = 56.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.25).sp
                )
                Text(
                    "✦",
                    color = Ivory025,
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-4).dp)
                )
            }

            LunariDivider026()

            Text(
                "твои персонажи, миры и истории",
                color = Lavender025.copy(alpha = .94f),
                fontSize = 14.sp,
                letterSpacing = .16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
private fun LunariDivider026() {
    Row(
        modifier = Modifier.padding(top = 1.dp, bottom = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .width(72.dp)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Lavender025.copy(alpha = .58f))
                    )
                )
        )
        Text(
            "☾",
            color = Lavender025.copy(alpha = .90f),
            fontSize = 15.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(horizontal = 9.dp)
        )
        Box(
            Modifier
                .width(72.dp)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Lavender025.copy(alpha = .58f), Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun LunariCard026(
    imageRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(26.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(107.dp)
            .shadow(8.dp, cardShape, clip = false)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Ivory025.copy(alpha = .58f),
                    Lavender025.copy(alpha = .38f),
                    Color(0xFF8A70D8).copy(alpha = .28f),
                    Ivory025.copy(alpha = .30f)
                )
            )
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
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
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(31.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = .065f), Color.Transparent)
                        )
                    )
            )

            LunariCardOrnament026(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(155.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 11.dp, end = 14.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Keep the approved 0.2.5 circular artwork and its frame unchanged.
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
private fun LunariCardOrnament026(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.padding(vertical = 5.dp, horizontal = 3.dp)) {
        val vineColor = Lavender025.copy(alpha = .27f)
        val softVine = LavenderGlow025.copy(alpha = .19f)
        val stroke = 1.dp.toPx()

        val main = Path().apply {
            moveTo(size.width * .98f, size.height * .05f)
            cubicTo(
                size.width * .70f, size.height * .08f,
                size.width * .84f, size.height * .42f,
                size.width * .58f, size.height * .48f
            )
            cubicTo(
                size.width * .82f, size.height * .54f,
                size.width * .64f, size.height * .88f,
                size.width * .98f, size.height * .95f
            )
        }
        drawPath(main, vineColor, style = Stroke(width = stroke))

        val curlTop = Path().apply {
            moveTo(size.width * .88f, size.height * .17f)
            cubicTo(
                size.width * .72f, size.height * .09f,
                size.width * .62f, size.height * .20f,
                size.width * .74f, size.height * .28f
            )
        }
        drawPath(curlTop, softVine, style = Stroke(width = stroke))

        val curlBottom = Path().apply {
            moveTo(size.width * .86f, size.height * .76f)
            cubicTo(
                size.width * .68f, size.height * .68f,
                size.width * .58f, size.height * .81f,
                size.width * .73f, size.height * .88f
            )
        }
        drawPath(curlBottom, softVine, style = Stroke(width = stroke))

        fun leaf(x: Float, y: Float, dx: Float, dy: Float) {
            val center = Offset(size.width * x, size.height * y)
            val tip = Offset(center.x + size.width * dx, center.y + size.height * dy)
            val wing = Offset(center.x + size.width * dy * .38f, center.y - size.height * dx * .38f)
            drawLine(vineColor, center, tip, strokeWidth = stroke)
            drawLine(vineColor, center, wing, strokeWidth = stroke)
        }

        leaf(.84f, .20f, -.07f, -.04f)
        leaf(.74f, .33f, -.06f, .02f)
        leaf(.69f, .48f, -.07f, -.03f)
        leaf(.72f, .62f, -.06f, .04f)
        leaf(.82f, .78f, -.07f, .02f)
        leaf(.91f, .88f, -.05f, -.04f)

        drawCircle(Ivory025.copy(alpha = .44f), radius = 1.7.dp.toPx(), center = Offset(size.width * .96f, size.height * .07f))
        drawCircle(LavenderGlow025.copy(alpha = .34f), radius = 1.2.dp.toPx(), center = Offset(size.width * .64f, size.height * .52f))
        drawLine(
            Ivory025.copy(alpha = .30f),
            Offset(size.width * .90f, size.height * .05f),
            Offset(size.width * .90f, size.height * .15f),
            strokeWidth = .7.dp.toPx()
        )
        drawLine(
            Ivory025.copy(alpha = .30f),
            Offset(size.width * .86f, size.height * .10f),
            Offset(size.width * .94f, size.height * .10f),
            strokeWidth = .7.dp.toPx()
        )
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
