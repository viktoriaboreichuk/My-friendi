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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night = Color(0xFF070B1B)
private val Glass = Color(0xCC15162E)
private val GlassSoft = Color(0xB81C1C38)
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
            .background(Night)
    ) {
        Image(
            painter = painterResource(R.drawable.lunari_home_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
            alpha = 0.82f
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color(0x22060A1A),
                        0.55f to Color(0x55070B1B),
                        1f to Night
                    )
                )
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
                            .height(218.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "☾",
                                color = Lavender,
                                fontSize = 34.sp,
                                lineHeight = 34.sp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Lunari",
                                color = Ivory,
                                fontSize = 38.sp,
                                lineHeight = 42.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            "твои персонажи, миры и истории",
                            color = Lavender.copy(alpha = 0.92f),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 3.dp, top = 2.dp, bottom = 12.dp)
                        )
                    }
                }

                item {
                    Text(
                        "Продолжить историю",
                        color = Ivory,
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
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
            .height(86.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Glass,
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Lavender2.copy(alpha = 0.34f),
                    Color.White.copy(alpha = 0.07f)
                )
            )
        ),
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Lavender2.copy(alpha = 0.25f),
                                Color(0xFF262342)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Lavender,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(Modifier.width(13.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = Ivory,
                    fontFamily = FontFamily.Serif,
                    fontSize = 23.sp,
                    lineHeight = 25.sp
                )
                Text(
                    subtitle,
                    color = Muted,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp,
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
            .height(82.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = GlassSoft,
        border = BorderStroke(1.dp, Lavender2.copy(alpha = 0.20f))
    ) {
        Column(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
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
                    modifier = Modifier.size(21.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    count.toString(),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            Text(
                title,
                color = Ivory,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp
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
                .height(72.dp)
                .padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Главная",
                    tint = Ivory,
                    modifier = Modifier.size(25.dp)
                )
            }

            FilledIconButton(
                onClick = onAdd,
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(19.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF8D74E8),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Создать",
                    modifier = Modifier.size(29.dp)
                )
            }

            IconButton(onClick = onMagic) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = "ИИ",
                    tint = Lavender,
                    modifier = Modifier.size(25.dp)
                )
            }

            IconButton(onClick = onProfile) {
                Icon(
                    Icons.Outlined.PersonOutline,
                    contentDescription = "Профиль",
                    tint = Lavender,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}
