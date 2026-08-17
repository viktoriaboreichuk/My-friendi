package com.vega.yakor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LunariColors = darkColorScheme(
    primary = Color(0xFF8D74E8),
    onPrimary = Color(0xFFF7F2FF),
    primaryContainer = Color(0xFF2B2352),
    onPrimaryContainer = Color(0xFFE8DFFF),
    secondary = Color(0xFFB9B4D9),
    onSecondary = Color(0xFF171326),
    background = Color(0xFF081020),
    onBackground = Color(0xFFF2EDF9),
    surface = Color(0xFF10182E),
    onSurface = Color(0xFFF2EDF9),
    surfaceVariant = Color(0xFF171F38),
    onSurfaceVariant = Color(0xFFB9B4D9),
    outline = Color(0xFF514B70),
    error = Color(0xFFFFB4AB)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = AppStore(this)
        setContent {
            MaterialTheme(colorScheme = LunariColors) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var route by remember { mutableStateOf(Route.Home) }
                    var selectedId by remember { mutableStateOf("") }
                    var showSplash by remember { mutableStateOf(true) }
                    val navigate: (Route, String?) -> Unit = { next, id ->
                        route = next
                        selectedId = id ?: ""
                    }
                    val back: () -> Unit = {
                        route = Route.Home
                        selectedId = ""
                    }

                    Box(Modifier.fillMaxSize()) {
                        if (route == Route.Settings && selectedId == "account") {
                            LunariAccount028(
                                store = store,
                                onBack = back,
                                onAiSettings = { navigate(Route.Settings, null) }
                            )
                        } else {
                            YakorApp(
                                store = store,
                                route = route,
                                selectedId = selectedId,
                                navigate = navigate,
                                back = back
                            )
                        }

                        AnimatedVisibility(
                            visible = showSplash,
                            exit = fadeOut(animationSpec = tween(durationMillis = 450))
                        ) {
                            LunariSplash0299(
                                onFinished = { showSplash = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Route {
    Home, Characters, CharacterEdit, Profiles, ProfileEdit, Worlds, WorldEdit,
    Chats, NewChat, Chat, Memories, Relationship, Settings, Snapshots
}
