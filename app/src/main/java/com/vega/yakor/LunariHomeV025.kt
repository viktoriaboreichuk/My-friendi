package com.vega.yakor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val V25Night = Color(0xFF060B1B)
private val V25GlassTop = Color(0xE3111730)
private val V25GlassBottom = Color(0xE5080D20)
private val V25Ivory = Color(0xFFF4EFF8)
private val V25Lavender = Color(0xFFD7C9FF)
private val V25LavenderSoft = Color(0xFFB9B4D9)
private val V25Purple = Color(0xFF7657D8)

@Composable
fun LunariHomeV025(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    Box(Modifier.fillMaxSize().background(V25Night)) {
        // Exact approved 51474 artwork. No procedural moon, blur or replacement art.
        Image(
            painter = painterResource(R.drawable.lunari_home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        // Only a readability veil; the approved artwork remains visible.
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.00f to Color(0x08030A20),
                    0.24f to Color(0x16050B22),
                    0.44f to Color(0x5C071024),
                    0.70f to Color(0xB8070D1F),
                    1.00f to Color(0xEE050916)
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .statusBarsPadding(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                item {
                    LunariHero025(onMagic = { navigate(Route.Settings, null) })
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_character,
                        "Персонажи",
                        "твои герои, их характеры\nи истории"
                    ) { navigate(Route.Characters, null) }
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_world,
                        "Миры",
                        "создай свои вселенные\nи локации"
                    ) { navigate(Route.Worlds, null) }
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_chat,
                        "Чаты",
                        "общайся с персонажами\nи развивай истории"
                    ) { navigate(Route.Chats, null) }
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_memory,
                        "Память",
                        "всё важное, что стоит\nсохранить"
                    ) { navigate(Route.Memories, "") }
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_profiles,
                        "Профили",
                        "твоя роль, голос\nи образ в историях"
                    ) { navigate(Route.Profiles, null) }
                }
                item {
                    LunariCard025(
                        R.drawable.lunari_snapshots,
                        "Снимки",
                        "точки сохранения\nдля важных моментов"
                    ) { navigate(Route.Snapshots, null) }
                }
            }
        }
    }
}

@Composable
private fun LunariHero025(onMagic: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        // The crescent is part of the approved background, not drawn by Compose.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 1.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0x43101938))
                .border(1.dp, V25Lavender.copy(alpha = .42f), CircleShape)
                .clickable(onClick = onMagic),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(35.dp)
                    .border(1.dp, Color.White.copy(alpha = .06f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = "Магия",
                    tint = V25Ivory,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 17.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lunari",
                color = V25Ivory,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 59.sp,
                letterSpacing = .15.sp
            )
            Row(
                modifier = Modifier.padding(top = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .width(57.dp)
                        .height(.7.dp)
                        .background(V25Lavender.copy(alpha = .31f))
                )
                Text("  ✦  ", color = V25Lavender, fontSize = 10.sp)
                Box(
                    Modifier
                        .width(57.dp)
                        .height(.7.dp)
                        .background(V25Lavender.copy(alpha = .31f))
                )
            }
            Text(
                text = "твои персонажи, миры и истории",
                color = V25Lavender.copy(alpha = .91f),
                fontSize = 13.5.sp,
                lineHeight = 17.sp,
                letterSpacing = .18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun LunariCard025(
    imageRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(25.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(106.dp)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(V25GlassTop, Color(0xE90D1430), V25GlassBottom)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        V25Lavender.copy(alpha = .45f),
                        Color.White.copy(alpha = .10f),
                        Color(0xFF7861B8).copy(alpha = .25f)
                    )
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
    ) {
        // Very subtle decorative light on the right, similar to the reference.
        Text(
            text = "✦",
            color = V25Lavender.copy(alpha = .055f),
            fontSize = 44.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 3.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 13.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LunariPreviewRim025(imageRes)
            Spacer(Modifier.width(15.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = V25Ivory,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    fontSize = 26.sp,
                    lineHeight = 28.sp
                )
                Text(
                    text = subtitle,
                    color = V25LavenderSoft,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.width(7.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = V25Lavender.copy(alpha = .83f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(2.dp))
        }
    }
}

@Composable
private fun LunariPreviewRim025(imageRes: Int) {
    // The image content is intentionally untouched. Only the frame is upgraded.
    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        Color(0xFFDECFFF).copy(alpha = .52f),
                        Color(0xFF8C6FCE).copy(alpha = .31f),
                        Color(0xFF251B4A).copy(alpha = .72f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFFF0E7FF).copy(alpha = .74f),
                            Color(0xFF725B9F).copy(alpha = .32f),
                            Color(0xFFD3BCFF).copy(alpha = .67f),
                            Color(0xFF45345F).copy(alpha = .38f),
                            Color(0xFFF0E7FF).copy(alpha = .74f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(77.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF080D21)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .border(1.dp, Color.White.copy(alpha = .17f), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun LunariBottom025(
    onAdd: () -> Unit,
    onMagic: () -> Unit,
    onProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .height(76.dp)
    ) {
        val barShape = RoundedCornerShape(29.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .align(Alignment.BottomCenter)
                .clip(barShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xF10C132A), Color(0xF6070C1C))
                    )
                )
                .border(1.dp, V25Lavender.copy(alpha = .18f), barShape)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Home,
                        contentDescription = "Главная",
                        tint = V25Ivory,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "Главная",
                        color = V25Ivory.copy(alpha = .87f),
                        fontSize = 9.sp,
                        lineHeight = 11.sp
                    )
                }
            }
            Spacer(Modifier.width(72.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onMagic),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AutoAwesome,
                        contentDescription = "Магия",
                        tint = V25Lavender,
                        modifier = Modifier.size(23.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onProfile),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PersonOutline,
                        contentDescription = "Профиль",
                        tint = V25Lavender.copy(alpha = .91f),
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        }

        // Geometric center of the complete bottom component.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(58.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFFA78AFF),
                            Color(0xFF7657D8),
                            Color(0xFF5A3DB7)
                        )
                    )
                )
                .border(1.dp, Color(0xFFE0D5FF).copy(alpha = .70f), CircleShape)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .size(52.dp)
                    .border(1.dp, Color.White.copy(alpha = .08f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Создать",
                    tint = V25Ivory,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
