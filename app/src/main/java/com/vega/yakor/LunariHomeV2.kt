package com.vega.yakor

import android.content.Context
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Night026 = Color(0xFF071020)
private val DeepNight026 = Color(0xFF040817)
private val Lavender026 = Color(0xFFD9CCFF)
private val LavenderSoft026 = Color(0xFFB9B4D9)
private val LavenderGlow026 = Color(0xFFA881FF)
private val Ivory026 = Color(0xFFF7F1FB)

@Composable
fun LunariHomeV2(
    store: AppStore,
    navigate: (Route, String?) -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yakor_store", Context.MODE_PRIVATE) }
    val subscriptionDays = prefs.getInt("subscription_days", 28)
    val currencyBalance = prefs.getInt("currency_balance", 1250)

    Box(Modifier.fillMaxSize().background(Night026)) {
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
                    .52f to Color(0x4A071027),
                    .74f to Color(0x9A050B1F),
                    1f to DeepNight026.copy(alpha = .94f)
                )
            )
        )
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LunariBottom028(
                    onHome = {},
                    onCharacters = { navigate(Route.Characters, null) },
                    onAdd = { showCreate = true },
                    onChats = { navigate(Route.Chats, null) },
                    onProfiles = { navigate(Route.Profiles, null) }
                )
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(inner).statusBarsPadding(),
                contentPadding = PaddingValues(start = 17.dp, end = 17.dp, bottom = 22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    LunariHero028(
                        subscriptionText = "PRO · $subscriptionDays дней",
                        currencyText = formatBalance028(currencyBalance),
                        onAccount = { navigate(Route.Settings, "account") }
                    )
                }
                item { LunariCard026(R.drawable.lunari_character, R.drawable.lunari_card_overlay_01, "Персонажи", "твои герои, их характеры\nи истории") { navigate(Route.Characters, null) } }
                item { LunariCard026(R.drawable.lunari_world, R.drawable.lunari_card_overlay_02, "Миры", "создай свои вселенные\nи локации") { navigate(Route.Worlds, null) } }
                item { LunariCard026(R.drawable.lunari_chat, R.drawable.lunari_card_overlay_03, "Чаты", "общайся с персонажами\nи развивай истории") { navigate(Route.Chats, null) } }
                item { LunariCard026(R.drawable.lunari_memory, R.drawable.lunari_card_overlay_04, "Память", "всё важное, что стоит\nсохранить") { navigate(Route.Memories, "") } }
                item { LunariCard026(R.drawable.lunari_profiles, R.drawable.lunari_card_overlay_05, "Профили", "твоя роль, голос\nи образ в историях") { navigate(Route.Profiles, null) } }
                item { LunariCard026(R.drawable.lunari_snapshots, R.drawable.lunari_card_overlay_06, "Снимки", "точки сохранения\nдля важных моментов") { navigate(Route.Snapshots, null) } }
            }
        }
    }

    if (showCreate) {
        AlertDialog(
            onDismissRequest = { showCreate = false },
            title = { Text("Что создать?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    LunariCreateChoice028(
                        icon = Icons.Outlined.Face,
                        title = "Создать персонажа",
                        subtitle = "Новый герой и его карточка"
                    ) {
                        showCreate = false
                        val c = CharacterCard()
                        store.upsertCharacter(c)
                        navigate(Route.CharacterEdit, c.id)
                    }
                    LunariCreateChoice028(
                        icon = Icons.Outlined.Public,
                        title = "Создать мир",
                        subtitle = "Новая вселенная, локации и канон"
                    ) {
                        showCreate = false
                        val w = WorldCard()
                        store.upsertWorld(w)
                        navigate(Route.WorldEdit, w.id)
                    }
                    LunariCreateChoice028(
                        icon = Icons.Outlined.AccountBox,
                        title = "Создать свой профиль",
                        subtitle = "Твоя роль и образ в историях"
                    ) {
                        showCreate = false
                        val p = UserProfile()
                        store.upsertProfile(p)
                        navigate(Route.ProfileEdit, p.id)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCreate = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun LunariHero028(
    subscriptionText: String,
    currencyText: String,
    onAccount: () -> Unit
) {
    Box(Modifier.fillMaxWidth().height(168.dp)) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 14.dp)
                .padding(top = 10.dp),
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

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.lunari_logo_026),
                contentDescription = "Lunari",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth(.78f).aspectRatio(3.36f)
            )
            Text(
                "твои персонажи, миры и истории",
                color = Lavender026.copy(alpha = .94f),
                fontSize = 14.sp,
                letterSpacing = .16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun LunariCounter028(width: androidx.compose.ui.unit.Dp, iconText: String, text: String) {
    Surface(
        modifier = Modifier.width(width).height(27.dp).shadow(5.dp, RoundedCornerShape(16.dp), clip = false),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xC4071025),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    Lavender026.copy(alpha = .52f),
                    Ivory026.copy(alpha = .22f),
                    LavenderGlow026.copy(alpha = .34f)
                )
            )
        )
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(iconText, color = Lavender026, fontSize = 11.sp, lineHeight = 11.sp)
            Text(
                text,
                color = Ivory026.copy(alpha = .94f),
                fontSize = 8.5.sp,
                lineHeight = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LunariCard026(
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
            .shadow(7.dp, cardShape, clip = false)
            .clickable(onClick = onClick),
        shape = cardShape,
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(
                    Lavender026.copy(alpha = .36f),
                    Ivory026.copy(alpha = .22f),
                    LavenderGlow026.copy(alpha = .34f)
                )
            )
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xB80A1430),
                        Color(0xAE0A132C),
                        Color(0xA4080F24)
                    )
                )
            )
        ) {
            Box(
                Modifier.fillMaxWidth().height(31.dp).background(
                    Brush.verticalGradient(listOf(Color.White.copy(alpha = .025f), Color.Transparent))
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 11.dp, end = 50.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LunariArtworkFrame028(imageRes)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(
                        title,
                        color = Ivory026,
                        fontFamily = FontFamily.Serif,
                        fontSize = 26.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        subtitle,
                        color = LavenderSoft026,
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Box(Modifier.matchParentSize().clip(cardShape)) {
                Image(
                    painter = painterResource(overlayRes),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(scaleX = 1.061f, scaleY = 1.34f),
                    contentScale = ContentScale.FillBounds
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .size(36.dp)
                    .shadow(6.dp, CircleShape, clip = false),
                shape = CircleShape,
                color = Color(0xC0141830),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Lavender026.copy(alpha = .62f)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.ArrowForwardIos,
                        contentDescription = null,
                        tint = Ivory026.copy(alpha = .96f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LunariArtworkFrame028(imageRes: Int) {
    Box(
        modifier = Modifier.size(88.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(7.dp, CircleShape, clip = false)
                .background(Color(0x2811162E), CircleShape)
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
                            Color.White.copy(alpha = .045f),
                            Color.Transparent,
                            Color(0xFF5D42A1).copy(alpha = .05f)
                        )
                    )
                )
            )
        }
        Image(
            painter = painterResource(R.drawable.lunari_artwork_frame_028),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

@Composable
private fun LunariBottom028(
    onHome: () -> Unit,
    onCharacters: () -> Unit,
    onAdd: () -> Unit,
    onChats: () -> Unit,
    onProfiles: () -> Unit
) {
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
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Lavender026.copy(alpha = .20f),
                        Ivory026.copy(alpha = .10f),
                        LavenderGlow026.copy(alpha = .18f)
                    )
                )
            )
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LunariNavItem028(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Home,
                    label = "Главная",
                    active = true,
                    onClick = onHome
                )
                LunariNavItem028(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Face,
                    label = "Персонажи",
                    showSparkle = true,
                    onClick = onCharacters
                )
                Spacer(Modifier.weight(1f))
                LunariNavItem028(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.ChatBubbleOutline,
                    label = "Чаты",
                    onClick = onChats
                )
                LunariNavItem028(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.AccountBox,
                    label = "Профили",
                    onClick = onProfiles
                )
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
            border = androidx.compose.foundation.BorderStroke(1.dp, Ivory026.copy(alpha = .72f))
        ) {
            Box(
                Modifier.background(
                    Brush.radialGradient(
                        listOf(Color(0xFFB09AFF), Color(0xFF7958DB), Color(0xFF4F359F))
                    )
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Add, "Создать", tint = Ivory026, modifier = Modifier.size(29.dp))
            }
        }
    }
}

@Composable
private fun LunariNavItem028(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    active: Boolean = false,
    showSparkle: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxHeight().clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (active) Ivory026 else Lavender026.copy(alpha = .90f),
                modifier = Modifier.size(22.dp)
            )
            if (showSparkle) {
                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Ivory026,
                    modifier = Modifier.align(Alignment.TopEnd).size(8.dp)
                )
            }
        }
        Text(
            label,
            color = if (active) Ivory026.copy(alpha = .92f) else LavenderSoft026.copy(alpha = .92f),
            fontSize = 7.5.sp,
            lineHeight = 9.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun LunariCreateChoice028(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF111A31),
        border = androidx.compose.foundation.BorderStroke(1.dp, Lavender026.copy(alpha = .18f))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Lavender026, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = Ivory026, fontWeight = FontWeight.Medium)
                Text(subtitle, color = LavenderSoft026, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = LavenderSoft026)
        }
    }
}

private fun formatBalance028(value: Int): String =
    value.toString().reversed().chunked(3).joinToString(" ").reversed()
