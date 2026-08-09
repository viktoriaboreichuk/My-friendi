package com.vega.yakor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = AppStore(this)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    var route by remember { mutableStateOf(Route.Home) }
                    var selectedId by remember { mutableStateOf("") }
                    YakorApp(
                        store = store,
                        route = route,
                        selectedId = selectedId,
                        navigate = { next, id -> route = next; selectedId = id ?: "" },
                        back = { route = Route.Home; selectedId = "" }
                    )
                }
            }
        }
    }
}

enum class Route {
    Home, Characters, CharacterEdit, Profiles, ProfileEdit, Worlds, WorldEdit,
    Chats, NewChat, Chat, Memories, Relationship, Settings, Snapshots
}
