package com.vega.yakor

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF070B1B)
private val Night2 = Color(0xFF11132C)
private val Glass = Color(0xD9151730)
private val GlassSoft = Color(0xC91B1C38)
private val Lavender = Color(0xFFD9CBFF)
private val Lavender2 = Color(0xFFBCA8FF)
private val Muted = Color(0xFFB7B1D0)
private val Ivory = Color(0xFFF5F0FA)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF080B1C),
                        Color(0xFF111331),
                        Color(0xFF090D20),
                        Night
                    )
                )
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x557A62C5),
                            Color(0x223A356A),
                            Color.Transparent
                        )
                    )
                )
        )

        Text(
            "☾",
            color = Lavender.copy(alpha = 0.95f),
            fontSize = 68.sp,
            lineHeight = 70.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 24.dp, top = 18.dp)
        )

        Text(
            "✦",
            color = Color(0xFFEDE4FF),
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 48.dp, top = 62.dp)
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LunariBottomV2(
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
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            "Lunari",
                            color = Ivory,
                            fontSize = 43.sp,
                            lineHeight = 46.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "твои персонажи, миры и истории",
                            color = Lavender.copy(alpha = 0.92f),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )
                    }
                }

                item {
                    Text(
                        "Продолжить историю",
                        color = Ivory,
                        fontFamily = FontFamily.Serif,
                        fontSize = 21.sp,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                    )
                }

                item {
                    LunariStoryCard(
                        icon = Icons.Outlined.PersonOutline,
                        title = "Персонажи",
                        subtitle = "герои, характеры и история",
                        count = store.characters.size
                    ) { navigate(Route.Characters, null) }
                }

                item {
                    LunariStoryCard(
                        icon = Icons.Outlined.AutoStories,
                        title = "Миры",
                        subtitle = "вселенные, правила и канон",
                        count = store.worlds.size
                    ) { navigate(Route.Worlds, null) }
                }

                item {
                    LunariStoryCard(
                        icon = Icons.Outlined.ChatBubbleOutline,
                        title = "Чаты",
                        subtitle = "живые диалоги и развитие сюжета",
                        count = store.chats.size
                    ) { navigate(Route.Chats, null) }
                }

                item {
                    LunariStoryCard(
                        icon = Icons.Outlined.Bookmarks,
                        title = "Память",
                        subtitle = "то, что персонажи не должны забыть",
                        count = store.memories.size
                    ) { navigate(Route.Memories, "") }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LunariMiniCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.AccountCircle,
                            title = "Профили",
                            count = store.profiles.size
                        ) { navigate(Route.Profiles, null) }

                        LunariMiniCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.PhotoCamera,
                            title = "Снимки",
                            count = store.snapshots.size
                        ) { navigate(Route.Snapshots, null) }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigate(Route.Settings, null) },
                        shape = RoundedCornerShape(20.dp),
                        color = GlassSoft,
                        border = BorderStroke(1.dp, Lavender2.copy(alpha = 0.22f))
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = Lavender2,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Настройки ИИ",
                                    color = Ivory,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    if (store.apiKey().isBlank()) "не настроено" else store.settings.model,
                                    color = Muted,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = Muted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LunariStoryCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(23.dp),
        color = Glass,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Lavender2.copy(alpha = 0.34f),
                    Color.White.copy(alpha = 0.07f)
                )
            )
        )
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Lavender2.copy(alpha = 0.26f),
                                Night2
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Lavender,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = Ivory,
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    lineHeight = 24.sp
                )
                Text(
                    subtitle,
                    color = Muted,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    maxLines = 1
                )
            }
            Text(
                count.toString(),
                color = Lavender,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(5.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Lavender.copy(alpha = 0.82f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun LunariMiniCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(78.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(21.dp),
        color = GlassSoft,
        border = BorderStroke(1.dp, Lavender2.copy(alpha = 0.20f))
    ) {
        Column(
            Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Lavender2,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(count.toString(), color = Muted, fontSize = 12.sp)
            }
            Text(
                title,
                color = Ivory,
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun LunariBottomV2(
    onAdd: () -> Unit,
    onMagic: () -> Unit,
    onProfile: () -> Unit
) {
    Surface(
        color = Color(0xF20A0E20),
        tonalElevation = 8.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(68.dp)
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Home, "Главная", tint = Ivory, modifier = Modifier.size(24.dp))
            }
            FilledIconButton(
                onClick = onAdd,
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF8D74E8),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Outlined.Add, "Создать", modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = onMagic) {
                Icon(Icons.Outlined.AutoAwesome, "ИИ", tint = Lavender, modifier = Modifier.size(24.dp))
            }
            IconButton(onClick = onProfile) {
                Icon(Icons.Outlined.PersonOutline, "Профиль", tint = Lavender, modifier = Modifier.size(24.dp))
            }
        }
    }
}
