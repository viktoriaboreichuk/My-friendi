package com.vega.yakor

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AccountBg028 = Color(0xFF071020)
private val AccountSurface028 = Color(0xE6111A31)
private val AccountText028 = Color(0xFFF7F1FB)
private val AccountSoft028 = Color(0xFFB9B4D9)
private val AccountLavender028 = Color(0xFFD9CCFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunariAccount028(
    store: AppStore,
    onBack: () -> Unit,
    onAiSettings: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("yakor_store", Context.MODE_PRIVATE) }
    var nickname by remember { mutableStateOf(prefs.getString("account_nickname", "") ?: "") }
    var email by remember { mutableStateOf(prefs.getString("account_email", "") ?: "") }
    var lastBackupAt by remember { mutableLongStateOf(prefs.getLong("account_last_backup", 0L)) }
    var pendingRestore by remember { mutableStateOf<String?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val createBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
                    it.write(store.exportBackup())
                } ?: error("Не удалось открыть файл для записи")
            }.onSuccess {
                val now = System.currentTimeMillis()
                prefs.edit().putLong("account_last_backup", now).apply()
                lastBackupAt = now
                scope.launch { snackbar.showSnackbar("Резервная копия сохранена") }
            }.onFailure {
                scope.launch { snackbar.showSnackbar("Не удалось сохранить резервную копию") }
            }
        }
    }

    val openBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Не удалось прочитать файл")
            }.onSuccess { pendingRestore = it }
                .onFailure {
                    scope.launch { snackbar.showSnackbar("Не удалось прочитать резервную копию") }
                }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AccountBg028,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Аккаунт", color = AccountText028, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Назад", tint = AccountText028)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .navigationBarsPadding()
                .imePadding()
                .background(
                    Brush.verticalGradient(
                        listOf(AccountBg028, Color(0xFF0B1028), Color(0xFF080D20))
                    )
                ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AccountSection028("Данные аккаунта") {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Ник Lunari") }
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("E-mail для будущей привязки") }
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            prefs.edit()
                                .putString("account_nickname", nickname.trim())
                                .putString("account_email", email.trim())
                                .apply()
                            scope.launch { snackbar.showSnackbar("Данные аккаунта сохранены на устройстве") }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить данные")
                    }
                }
            }

            item {
                AccountSection028("Привязка аккаунта") {
                    AccountLinkRow028(
                        title = "Google",
                        subtitle = "Не подключено · серверная синхронизация будет добавлена позже"
                    )
                    HorizontalDivider(color = AccountLavender028.copy(alpha = .12f))
                    AccountLinkRow028(
                        title = "E-mail",
                        subtitle = if (email.isBlank()) {
                            "Не указан · адрес можно сохранить выше"
                        } else {
                            "Адрес сохранён локально · подтверждение и вход будут добавлены позже"
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Lunari не имитирует облачную привязку: пока нет серверной авторизации, здесь показывается её реальный статус.",
                        color = AccountSoft028,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                AccountSection028("Резервные копии") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Backup, contentDescription = null, tint = AccountLavender028)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Файл прогресса", color = AccountText028, fontWeight = FontWeight.Medium)
                            Text(
                                if (lastBackupAt == 0L) "Локальная копия ещё не создавалась"
                                else "Последняя копия: ${formatAccountTime028(lastBackupAt)}",
                                color = AccountSoft028,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
                            createBackup.launch("lunari-backup-$stamp.json")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Backup, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Создать резервную копию")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { openBackup.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Restore, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Восстановить из файла")
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CloudOff,
                            contentDescription = null,
                            tint = AccountSoft028,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Сейчас резервная копия сохраняется в выбранный пользователем файл. Облачная синхронизация появится после подключения аккаунта.",
                            color = AccountSoft028,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                AccountSection028("Сервис") {
                    OutlinedButton(onClick = onAiSettings, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Настройки ИИ")
                    }
                }
            }
        }
    }

    if (pendingRestore != null) {
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = { Text("Восстановить данные?") },
            text = {
                Text("Текущие персонажи, миры, чаты, память и другие данные будут заменены содержимым резервной копии. API-ключ не импортируется.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val raw = pendingRestore
                        pendingRestore = null
                        if (raw != null) {
                            runCatching { store.importBackup(raw) }
                                .onSuccess {
                                    scope.launch { snackbar.showSnackbar("Данные восстановлены") }
                                }
                                .onFailure {
                                    scope.launch { snackbar.showSnackbar("Файл не является резервной копией Lunari") }
                                }
                        }
                    }
                ) { Text("Восстановить") }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun AccountSection028(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AccountSurface028),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccountLavender028.copy(alpha = .16f))
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                title,
                color = AccountText028,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun AccountLinkRow028(title: String, subtitle: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = AccountText028, fontWeight = FontWeight.Medium)
            Text(subtitle, color = AccountSoft028, style = MaterialTheme.typography.bodySmall)
        }
        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text("Скоро") }
        )
    }
}

private fun formatAccountTime028(value: Long): String =
    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(value))
