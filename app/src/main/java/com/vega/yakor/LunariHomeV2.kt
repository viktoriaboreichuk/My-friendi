package com.vega.yakor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night024 = Color(0xFF071020)
private val DeepNight024 = Color(0xFF040817)
private val Lavender024 = Color(0xFFD9CCFF)
private val LavenderGlow024 = Color(0xFFA881FF)
private val Muted024 = Color(0xFFB9B4D9)
private val Ivory024 = Color(0xFFF5EFF9)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    Box(Modifier.fillMaxSize().background(Night024)) {
        Image(
            painter = painterResource(R.drawable.lunari_home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x0803091E),
                    .28f to Color(0x18102036),
                    .52f to Color(0x76101A35),
                    .76f to Color(0xD4070D20),
                    1f to DeepNight024
                )
            )
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LunariBottom024(
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { LunariHero024(onMagic = { navigate(Route.Settings, null) }) }
                item { LunariCard024(R.drawable.lunari_character, "Персонажи", "твои герои, их характеры\nи истории") { navigate(Route.Characters, null) } }
                item { LunariCard024(R.drawable.lunari_world, "Миры", "создай свои вселенные\nи локации") { navigate(Route.Worlds, null) } }
                item { LunariCard024(R.drawable.lunari_chat, "Чаты", "общайся с персонажами\nи развивай истории") { navigate(Route.Chats, null) } }
                item { LunariCard024(R.drawable.lunari_memory, "Память", "всё важное, что стоит\nсохранить") { navigate(Route.Memories, "") } }
                item { LunariCard024(R.drawable.lunari_profiles, "Профили", "твоя роль, голос\nи образ в историях") { navigate(Route.Profiles, null) } }
                item { LunariCard024(R.drawable.lunari_snapshots, "Снимки", "точки сохранения\nдля важных моментов") { navigate(Route.Snapshots, null) } }
            }
        }
    }
}

@Composable
private fun LunariHero024(onMagic: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(194.dp)) {
        Surface(
            onClick = onMagic,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 6.dp, end = 2.dp)
                .size(40.dp),
            shape = CircleShape,
            color = Color(0x24101831),
            border = BorderStroke(1.dp, Lavender024.copy(alpha = .34f)),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = "Настройки ИИ",
                    tint = Ivory024,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Text(
                    "Lunari",
                    color = Ivory024,
                    fontFamily = FontFamily.Serif,
                    fontSize = 58.sp,
                    lineHeight = 61.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = (-0.8).sp
                )
                Text(
                    "✦",
                    color = Ivory024,
                    fontSize = 17.sp,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-4).dp)
                )
            }
            Text(
                "твои персонажи, миры и истории",
                color = Lavender024.copy(alpha = .93f),
                fontSize = 14.5.sp,
                letterSpacing = .18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun LunariCard024(
    imageRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(116.dp),
        shape = RoundedCornerShape(27.dp),
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Color(0xFFE7DDF5).copy(alpha = .43f),
                    Color(0xFFB9A6D7).copy(alpha = .21f),
                    Color(0xFF6B5A91).copy(alpha = .28f)
                )
            )
        ),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xDD0B1530),
                            Color(0xD40B1430),
                            Color(0xC80A1128)
                        )
                    )
                )
                .padding(start = 12.dp, end = 13.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(92.dp),
                shape = CircleShape,
                color = Color(0xFF11162E),
                border = BorderStroke(1.dp, Color(0xFFD6BEE8).copy(alpha = .64f)),
                shadowElevation = 5.dp
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }

            Spacer(Modifier.width(17.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    title,
                    color = Ivory024,
                    fontFamily = FontFamily.Serif,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    subtitle,
                    color = Muted024,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }

            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Lavender024.copy(alpha = .92f),
                modifier = Modifier.size(27.dp)
            )
        }
    }
}

@Composable
private fun LunariBottom024(
    onAdd: () -> Unit,
    onMagic: () -> Unit,
    onProfile: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 15.dp, vertical = 7.dp)
            .height(76.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(30.dp),
            color = Color(0xEE071025),
            border = BorderStroke(1.dp, Lavender024.copy(alpha = .17f)),
            shadowElevation = 11.dp
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Home, "Главная", tint = Ivory024, modifier = Modifier.size(24.dp))
                        Text("Главная", color = Ivory024.copy(alpha = .90f), fontSize = 9.sp, lineHeight = 11.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onMagic, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, "ИИ", tint = Lavender024, modifier = Modifier.size(25.dp))
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(onClick = onProfile, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.Outlined.PersonOutline, "Профиль", tint = Lavender024.copy(alpha = .88f), modifier = Modifier.size(25.dp))
                    }
                }
            }
        }

        Surface(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.Center).size(59.dp),
            shape = CircleShape,
            color = Color(0xFF7150D2),
            border = BorderStroke(1.dp, Color(0xFFE0D4FF).copy(alpha = .78f)),
            shadowElevation = 12.dp
        ) {
            Box(
                Modifier.background(
                    Brush.radialGradient(
                        listOf(Color(0xFFA78AFF), Color(0xFF7652D8), Color(0xFF583DB1))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Создать", tint = Ivory024, modifier = Modifier.size(31.dp))
            }
        }
    }
}
