package com.vega.yakor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night025 = Color(0xFF071020)
private val Lavender025 = Color(0xFFD9CCFF)
private val LavenderGlow025 = Color(0xFFA881FF)
private val Ivory025 = Color(0xFFF7F1FB)

private data class LunariCardData027(
    val imageRes: Int,
    val overlayRes: Int,
    val title: String,
    val subtitle: String,
    val route: Route,
    val argument: String? = null
)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    val cards = listOf(
        LunariCardData027(R.drawable.lunari_character, R.drawable.lunari_card_overlay_01, "Персонажи", "твои герои, их характеры\nи истории", Route.Characters),
        LunariCardData027(R.drawable.lunari_world, R.drawable.lunari_card_overlay_02, "Миры", "создай свои вселенные\nи локации", Route.Worlds),
        LunariCardData027(R.drawable.lunari_chat, R.drawable.lunari_card_overlay_03, "Чаты", "общайся с персонажами\nи развивай истории", Route.Chats),
        LunariCardData027(R.drawable.lunari_memory, R.drawable.lunari_card_overlay_04, "Память", "всё важное, что стоит\nсохранить", Route.Memories, ""),
        LunariCardData027(R.drawable.lunari_profiles, R.drawable.lunari_card_overlay_05, "Профили", "твоя роль, голос\nи образ в историях", Route.Profiles),
        LunariCardData027(R.drawable.lunari_snapshots, R.drawable.lunari_card_overlay_06, "Снимки", "точки сохранения\nдля важных моментов", Route.Snapshots)
    )

    Box(Modifier.fillMaxSize().background(Night025)) {
        Image(
            painter = painterResource(R.drawable.lunari_home_bg_027),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
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
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .statusBarsPadding()
            ) {
                // UI-14: logo/header is fixed. Cards scroll only in the clipped area below it.
                LunariHero027(onMagic = { navigate(Route.Settings, null) })

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clipToBounds(),
                    contentPadding = PaddingValues(start = 17.dp, end = 17.dp, top = 4.dp, bottom = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cards) { card ->
                        LunariCard027(
                            imageRes = card.imageRes,
                            overlayRes = card.overlayRes,
                            title = card.title,
                            subtitle = card.subtitle,
                            onClick = { navigate(card.route, card.argument) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LunariHero027(onMagic: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(236.dp)
            .padding(horizontal = 18.dp)
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .size(40.dp)
                .shadow(7.dp, CircleShape, clip = false)
                .clickable(onClick = onMagic),
            shape = CircleShape,
            color = Color(0x40101935),
            border = BorderStroke(1.dp, Lavender025.copy(alpha = .30f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AutoAwesome, "Настройки ИИ", tint = Ivory025, modifier = Modifier.size(18.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // UI-11: exact approved graphic asset, not a reconstructed font/logo.
            Image(
                painter = painterResource(R.drawable.lunari_logo_027),
                contentDescription = "Lunari",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(.86f)
                    .aspectRatio(800f / 246f)
            )
            Text(
                "твои персонажи, миры и истории",
                color = Lavender025.copy(alpha = .94f),
                fontSize = 14.sp,
                letterSpacing = .16.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun LunariCard027(
    imageRes: Int,
    overlayRes: Int,
    title: String,
    subtitle: String,
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
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0x560A1430), Color(0x420A132C), Color(0x2B080F24))
                    )
                )
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 15.dp)
                    .size(76.dp)
                    .clip(CircleShape)
            )

            // UI-12: one approved transparent frame/ornament file per card.
            Image(
                painter = painterResource(overlayRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 112.dp, end = 46.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    title,
                    color = Ivory025,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    fontSize = 27.sp,
                    lineHeight = 30.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    subtitle,
                    color = Lavender025.copy(alpha = .93f),
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
            }

            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = LavenderGlow025,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(28.dp)
            )
        }
    }
}

@Composable
private fun LunariBottom025(onAdd: () -> Unit, onMagic: () -> Unit, onProfile: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 15.dp, vertical = 6.dp)
            .height(71.dp)
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
                modifier = Modifier.background(
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
