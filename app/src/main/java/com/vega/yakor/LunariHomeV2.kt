package com.vega.yakor

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF081020)
private val DeepNight = Color(0xFF060A19)
private val Glass = Color(0xD0121832)
private val Lavender = Color(0xFFD8CBFF)
private val Lavender2 = Color(0xFF9D7BFF)
private val Muted = Color(0xFFB9B4D9)
private val Ivory = Color(0xFFF4EEF8)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    Box(Modifier.fillMaxSize().background(Night)) {
        Image(
            painter = painterResource(R.drawable.lunari_home_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color(0x12040A1C),
                    .34f to Color(0x44101836),
                    .58f to Color(0xB0081020),
                    1f to DeepNight
                )
            )
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LunariBottomV3(
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { LunariHero(onMagic = { navigate(Route.Settings, null) }) }
                item { LunariReferenceCard(R.drawable.lunari_character, "Персонажи", "твои герои, их характеры\nи истории") { navigate(Route.Characters, null) } }
                item { LunariReferenceCard(R.drawable.lunari_world, "Миры", "создай свои вселенные\nи локации") { navigate(Route.Worlds, null) } }
                item { LunariReferenceCard(R.drawable.lunari_chat, "Чаты", "общайся с персонажами\nи развивай истории") { navigate(Route.Chats, null) } }
                item { LunariReferenceCard(R.drawable.lunari_memory, "Память", "всё важное, что стоит\nсохранить") { navigate(Route.Memories, "") } }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SecondaryLink(Modifier.weight(1f), "Профили", Icons.Outlined.AccountCircle) { navigate(Route.Profiles, null) }
                        SecondaryLink(Modifier.weight(1f), "Снимки", Icons.Outlined.PhotoCamera) { navigate(Route.Snapshots, null) }
                    }
                }
            }
        }
    }
}

@Composable
private fun LunariHero(onMagic: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(315.dp)) {
        Surface(
            onClick = onMagic,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 12.dp, end = 2.dp).size(42.dp),
            shape = CircleShape,
            color = Color(0x35111932),
            border = BorderStroke(1.dp, Lavender.copy(alpha = .34f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = Lavender, modifier = Modifier.size(21.dp))
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Lunari", color = Ivory, fontFamily = FontFamily.Serif, fontSize = 56.sp, lineHeight = 60.sp, fontWeight = FontWeight.Normal)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(76.dp).height(1.dp).background(Lavender.copy(alpha = .44f)))
                Text("  ☾  ", color = Lavender, fontSize = 15.sp)
                Box(Modifier.width(76.dp).height(1.dp).background(Lavender.copy(alpha = .44f)))
            }
            Text(
                "твои персонажи, миры и истории",
                color = Lavender.copy(alpha = .92f),
                fontSize = 14.sp,
                letterSpacing = .25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Composable
private fun LunariReferenceCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(108.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(25.dp),
        color = Glass,
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(Lavender.copy(alpha=.34f), Color.White.copy(alpha=.08f), Lavender2.copy(alpha=.16f)))),
        shadowElevation = 2.dp
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(82.dp).clip(CircleShape).drawBehind {
                    drawCircle(color = Color(0xFFB69BFF).copy(alpha=.26f), radius = size.minDimension/2)
                },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }
            Spacer(Modifier.width(15.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(title, color = Ivory, fontFamily = FontFamily.Serif, fontSize = 26.sp, lineHeight = 28.sp)
                Text(subtitle, color = Muted, fontSize = 13.5.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = Lavender.copy(alpha=.88f), modifier = Modifier.size(27.dp))
        }
    }
}

@Composable
private fun SecondaryLink(modifier: Modifier, title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(50.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color(0x8A11172D),
        border = BorderStroke(1.dp, Lavender.copy(alpha=.12f))
    ) {
        Row(Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Lavender.copy(alpha=.75f), modifier = Modifier.size(19.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LunariBottomV3(onAdd: () -> Unit, onMagic: () -> Unit, onProfile: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(Color.Transparent).navigationBarsPadding().padding(horizontal = 14.dp, vertical = 8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(76.dp),
            shape = RoundedCornerShape(31.dp),
            color = Color(0xF20A1024),
            border = BorderStroke(1.dp, Lavender.copy(alpha=.14f)),
            shadowElevation = 12.dp
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 22.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Outlined.Home, "Главная", tint = Ivory, modifier = Modifier.size(24.dp))
                    Text("Главная", color = Ivory.copy(alpha=.9f), fontSize = 9.sp)
                }
                Surface(
                    onClick = onAdd,
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    color = Color(0xFF7456D7),
                    border = BorderStroke(1.dp, Color(0xFFCEBDFF).copy(alpha=.75f)),
                    shadowElevation = 9.dp
                ) {
                    Box(Modifier.background(Brush.radialGradient(listOf(Color(0xFF9B7BFF), Color(0xFF6244C2)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Add, "Создать", tint = Ivory, modifier = Modifier.size(31.dp))
                    }
                }
                IconButton(onClick = onMagic) { Icon(Icons.Outlined.AutoAwesome, "ИИ", tint = Lavender, modifier = Modifier.size(25.dp)) }
                IconButton(onClick = onProfile) { Icon(Icons.Outlined.PersonOutline, "Профиль", tint = Lavender.copy(alpha=.88f), modifier = Modifier.size(25.dp)) }
            }
        }
    }
}
