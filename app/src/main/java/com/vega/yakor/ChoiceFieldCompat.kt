package com.vega.yakor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * ChoiceField overload with the callback as the final parameter.
 *
 * Screens.kt contains an older file-private variant where Modifier is last.
 * Kotlin trailing-lambda syntax binds only to the last parameter, so calls like
 * ChoiceField(..., selected) { value -> ... } need this overload.
 */
@Composable
internal fun ChoiceField(
    label: String,
    options: List<Pair<String, String>>,
    selected: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onSelect: (String) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val title = options.firstOrNull { it.first == selected }?.second ?: "Не выбрано"

    androidx.compose.foundation.layout.Box(modifier) {
        OutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(title)
            }
        }

        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false }
        ) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelect(id)
                        open = false
                    }
                )
            }
        }
    }
}
