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
private val DeepNight = Color(0xFF050815)
private val Glass = Color(0xC9111730)
private val Lavender = Color(0xFFDCCFFF)
private val Lavender2 = Color(0xFF9D7BFF)
private val Muted = Color(0xFFBDB7D9)
private val Ivory = Color(0xFFF7F1FB)

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
                    0f to Color(0x08030819),
                    .30f to Color(0x28101739),
                    .52f to Color(0x8C081020),
                    .76f to Color(0xD9081020),
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { LunariHero(onMagic = { navigate(Route.Settings, null) }) }
                item { LunariReferenceCard(R.drawable.lunari_character, "Персонажи", "твои герои, их характеры\nи истории") { navigate(Route.Characters, null) } }
                item { LunariReferenceCard(R.drawable.lunari_world, "Миры", "создай свои вселенные\nи локации") { navigate(Route.Worlds, null) } }
                item { LunariReferenceCard(R.drawable.lunari_chat, "Чаты", "общайся с персонажами\nи развивай истории") { navigate(Route.Chats, null) } }
                item { LunariReferenceCard(R.drawable.lunari_memory, "Память", "всё важное, что стоит\nсохранить") { navigate(Route.Memories, "") } }
                item { LunariReferenceCard(R.drawable.lunari_profiles, "Профили", "твоя роль, голос\nи образ в историях") { navigate(Route.Profiles, null) } }
                item { LunariReferenceCard(R.drawable.lunari_snapshots, "Снимки", "точки сохранения\nдля важных моментов") { navigate(Route.Snapshots, null) } }
            }
        }
    }
}

@Composable
private fun LunariHero(onMagic: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(286.dp)) {
        Surface(
            onClick = onMagic,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 2.dp).size(40.dp),
            shape = CircleShape,
            color = Color(0x32111932),
            border = BorderStroke(1.dp, Lavender.copy(alpha = .30f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = Lavender, modifier = Modifier.size(20.dp))
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 17.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Lunari",
                color = Ivory,
                fontFamily = FontFamily.Serif,
                fontSize = 52.sp,
                lineHeight = 55.sp,
                fontWeight = FontWeight.Normal
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(66.dp).height(1.dp).background(Lavender.copy(alpha = .40f)))
                Text("  ✦  ", color = Lavender, fontSize = 12.sp)
                Box(Modifier.width(66.dp).height(1.dp).background(Lavender.copy(alpha = .40f)))
            }
            Text(
                "твои персонажи, миры и истории",
                color = Lavender.copy(alpha = .92f),
                fontSize = 14.sp,
                letterSpacing = .22.sp,
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
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Lavender.copy(alpha = .36f),
                    Color.White.copy(alpha = .07f),
                    Lavender2.copy(alpha = .18f)
                )
            )
        ),
        shadowElevation = 3.dp
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(82.dp).clip(CircleShape).drawBehind {
                    drawCircle(color = Color(0xFFB69BFF).copy(alpha = .27f), radius = size.minDimension / 2)
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
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Lavender.copy(alpha = .86f),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun LunariBottomV3(onAdd: () -> Unit, onMagic: () -> Unit, onProfile: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .height(80.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(70.dp).align(Alignment.BottomCenter),
            shape = RoundedCornerShape(29.dp),
            color = Color(0xF20A1024),
            border = BorderStroke(1.dp, Lavender.copy(alpha = .14f)),
            shadowElevation = 12.dp
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Home, "Главная", tint = Ivory, modifier = Modifier.size(23.dp))
                        Text("Главная", color = Ivory.copy(alpha = .88f), fontSize = 9.sp)
                    }
                }
                Spacer(Modifier.width(70.dp))
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMagic, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, "ИИ", tint = Lavender, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = onProfile, modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Outlined.PersonOutline, "Профиль", tint = Lavender.copy(alpha = .88f), modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        Surface(
            onClick = onAdd,
            modifier = Modifier.align(Alignment.Center).size(60.dp),
            shape = CircleShape,
            color = Color(0xFF7456D7),
            border = BorderStroke(1.dp, Color(0xFFCEBDFF).copy(alpha = .78f)),
            shadowElevation = 10.dp
        ) {
            Box(
                Modifier.background(Brush.radialGradient(listOf(Color(0xFFA58AFF), Color(0xFF6546C7)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Создать", tint = Ivory, modifier = Modifier.size(31.dp))
            }
        }
    }
}
