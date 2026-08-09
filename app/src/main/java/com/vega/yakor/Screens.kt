package com.vega.yakor

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun YakorApp(
    store: AppStore,
    route: Route,
    selectedId: String,
    navigate: (Route, String?) -> Unit,
    back: () -> Unit
) {
    when (route) {
        Route.Home -> HomeScreen(store, navigate)
        Route.Characters -> CharacterListScreen(store, navigate, back)
        Route.CharacterEdit -> CharacterEditScreen(store, selectedId, { navigate(Route.Characters, null) })
        Route.Profiles -> ProfileListScreen(store, navigate, back)
        Route.ProfileEdit -> ProfileEditScreen(store, selectedId, { navigate(Route.Profiles, null) })
        Route.Worlds -> WorldListScreen(store, navigate, back)
        Route.WorldEdit -> WorldEditScreen(store, selectedId, { navigate(Route.Worlds, null) })
        Route.Chats -> ChatListScreen(store, navigate, back)
        Route.NewChat -> NewChatScreen(store, navigate, { navigate(Route.Chats, null) })
        Route.Chat -> ChatScreen(store, selectedId, { navigate(Route.Chats, null) }, navigate)
        Route.Memories -> MemoriesScreen(store, selectedId, back)
        Route.Relationship -> RelationshipScreen(store, selectedId, back)
        Route.Settings -> SettingsScreen(store, back)
        Route.Snapshots -> SnapshotsScreen(store, back)
    }
}

@Composable
private fun Page(title: String, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Surface(shadowElevation = 2.dp) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) TextButton(onClick = onBack) { Text("←") }
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
        }
        Column(Modifier.fillMaxSize(), content = content)
    }
}

@Composable
private fun HomeScreen(store: AppStore, navigate: (Route, String?) -> Unit) {
    Page("Якорь 0.1") {
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Персонаж остаётся собой. Канон, память и отношения живут отдельно от последнего куска переписки.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
            }
            item { HomeButton("🎭 Персонажи", "${store.characters.size}") { navigate(Route.Characters, null) } }
            item { HomeButton("👤 Мои профили", "${store.profiles.size}") { navigate(Route.Profiles, null) } }
            item { HomeButton("🌍 Миры и канон", "${store.worlds.size}") { navigate(Route.Worlds, null) } }
            item { HomeButton("💬 Чаты", "${store.chats.size}") { navigate(Route.Chats, null) } }
            item { HomeButton("🧠 Память", "${store.memories.size}") { navigate(Route.Memories, "") } }
            item { HomeButton("📸 Снимки", "${store.snapshots.size}") { navigate(Route.Snapshots, null) } }
            item { HomeButton("⚙ Настройки ИИ", if (store.apiKey().isBlank()) "нужен ключ" else store.settings.model) { navigate(Route.Settings, null) } }
            item {
                Spacer(Modifier.height(12.dp))
                Text("Данные персонажей и переписка хранятся на устройстве. API-ключ шифруется Android Keystore.", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun HomeButton(title: String, subtitle: String, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CharacterListScreen(store: AppStore, navigate: (Route, String?) -> Unit, back: () -> Unit) {
    Page("Персонажи", back) {
        LazyColumn(Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(store.characters, key = { it.id }) { c ->
                Card(Modifier.fillMaxWidth().clickable { navigate(Route.CharacterEdit, c.id) }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Avatar(c.avatarUri, c.name, 58)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.name, style = MaterialTheme.typography.titleMedium)
                            Text(listOf(c.role, c.age).filter { it.isNotBlank() }.joinToString(" • "), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                val c = CharacterCard()
                store.upsertCharacter(c)
                navigate(Route.CharacterEdit, c.id)
            },
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) { Text("+ Создать персонажа") }
    }
}

@Composable
private fun CharacterEditScreen(store: AppStore, id: String, onBack: () -> Unit) {
    val original = store.characters.firstOrNull { it.id == id } ?: CharacterCard(id = id)
    var x by remember(id) { mutableStateOf(original) }
    var deleteConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            x = x.copy(avatarUri = uri.toString())
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { runCatching { context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } }
        x = x.copy(galleryUris = (x.galleryUris + uris.map { it.toString() }).distinct())
    }
    Page("Карточка персонажа", onBack) {
        LazyColumn(Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(x.avatarUri, x.name, 86)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        OutlinedButton(onClick = { imageLauncher.launch(arrayOf("image/*")) }) { Text("Выбрать фото") }
                        if (x.avatarUri.isNotBlank()) TextButton(onClick = { x = x.copy(avatarUri = "") }) { Text("Убрать фото") }
                    }
                }
            }
            item {
                Text("Галерея референсов", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { galleryLauncher.launch(arrayOf("image/*")) }) { Text("+ Добавить фото") }
                    if (x.galleryUris.isNotEmpty()) TextButton(onClick = { x = x.copy(galleryUris = emptyList()) }) { Text("Очистить") }
                }
                if (x.galleryUris.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(x.galleryUris) { Avatar(it, x.name, 72) } }
                }
            }
            item { LongField("Имя", x.name, { x=x.copy(name=it) }, false) }
            item { LongField("Возраст", x.age, { x=x.copy(age=it) }, false) }
            item { LongField("Роль / происхождение / раса", x.role, { x=x.copy(role=it) }) }
            item { LongField("Внешность", x.appearance, { x=x.copy(appearance=it) }) }
            item { LongField("Ядро характера", x.personality, { x=x.copy(personality=it) }) }
            item { LongField("Биография", x.biography, { x=x.copy(biography=it) }) }
            item { LongField("Ценности", x.values, { x=x.copy(values=it) }) }
            item { LongField("Страхи и уязвимости", x.fears, { x=x.copy(fears=it) }) }
            item { LongField("Манера речи", x.speech, { x=x.copy(speech=it) }) }
            item { LongField("Правила поведения", x.behaviorRules, { x=x.copy(behaviorRules=it) }) }
            item { LongField("НИКОГДА не делать", x.neverDo, { x=x.copy(neverDo=it) }) }
            item { LongField("Приветствие", x.greeting, { x=x.copy(greeting=it) }) }
            item { LongField("Примеры реплик", x.examples, { x=x.copy(examples=it) }) }
            item { LongField("Дополнительные заметки", x.extra, { x=x.copy(extra=it) }) }
            item { WorldChoice(store.worlds, x.worldId) { x=x.copy(worldId=it) } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { store.createSnapshot("Перед изменением ${x.name}") }) { Text("Снимок") }
                    OutlinedButton(onClick = { deleteConfirm = true }) { Text("Удалить") }
                }
            }
        }
        Button(onClick = { store.upsertCharacter(x); onBack() }, Modifier.fillMaxWidth().padding(12.dp)) { Text("Сохранить") }
    }
    if (deleteConfirm) ConfirmDelete("Удалить персонажа и его чаты/память?") {
        if (it) { store.deleteCharacter(id); onBack() }
        deleteConfirm = false
    }
}

@Composable
private fun ProfileListScreen(store: AppStore, navigate: (Route, String?) -> Unit, back: () -> Unit) {
    Page("Мои профили", back) {
        LazyColumn(Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(store.profiles, key={it.id}) { p ->
                Card(Modifier.fillMaxWidth().clickable { navigate(Route.ProfileEdit,p.id) }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Avatar(p.avatarUri,p.name,58); Spacer(Modifier.width(12.dp)); Text(p.name, style=MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
        Button(onClick={ val p=UserProfile(); store.upsertProfile(p); navigate(Route.ProfileEdit,p.id) }, Modifier.fillMaxWidth().padding(12.dp)) { Text("+ Создать мой образ") }
    }
}

@Composable
private fun ProfileEditScreen(store: AppStore, id: String, onBack: () -> Unit) {
    val original=store.profiles.firstOrNull{it.id==id}?:UserProfile(id=id)
    var x by remember(id){ mutableStateOf(original) }
    val context=LocalContext.current
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri-> if(uri!=null){
        runCatching{context.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)}
        x=x.copy(avatarUri=uri.toString())
    }}
    val galleryLauncher=rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()){uris->
        uris.forEach{runCatching{context.contentResolver.takePersistableUriPermission(it,Intent.FLAG_GRANT_READ_URI_PERMISSION)}}
        x=x.copy(galleryUris=(x.galleryUris+uris.map{it.toString()}).distinct())
    }
    Page("Мой профиль",onBack){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            item { Row(verticalAlignment=Alignment.CenterVertically){ Avatar(x.avatarUri,x.name,86); Spacer(Modifier.width(12.dp)); OutlinedButton(onClick={launcher.launch(arrayOf("image/*"))}){Text("Выбрать фото")} } }
            item {
                Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={galleryLauncher.launch(arrayOf("image/*"))}){Text("+ Фото в галерею")};if(x.galleryUris.isNotEmpty())TextButton(onClick={x=x.copy(galleryUris=emptyList())}){Text("Очистить")}}
                if(x.galleryUris.isNotEmpty())LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(x.galleryUris){Avatar(it,x.name,72)}}
            }
            item { LongField("Имя / имя образа",x.name,{x=x.copy(name=it)},false) }
            item { LongField("Внешность",x.appearance,{x=x.copy(appearance=it)}) }
            item { LongField("Биография",x.biography,{x=x.copy(biography=it)}) }
            item { LongField("Характер",x.personality,{x=x.copy(personality=it)}) }
            item { LongField("Что персонаж знает обо мне изначально",x.knownInitially,{x=x.copy(knownInitially=it)}) }
            item { LongField("Что от персонажа скрыто",x.hiddenInitially,{x=x.copy(hiddenInitially=it)}) }
            item { LongField("Моя роль в этой истории",x.roleInStory,{x=x.copy(roleInStory=it)}) }
            item { LongField("Дополнительно",x.extra,{x=x.copy(extra=it)}) }
        }
        Button(onClick={store.upsertProfile(x);onBack()},Modifier.fillMaxWidth().padding(12.dp)){Text("Сохранить")}
    }
}

@Composable
private fun WorldListScreen(store: AppStore,navigate:(Route,String?)->Unit,back:()->Unit){
    Page("Миры и канон",back){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(store.worlds,key={it.id}){w-> Card(Modifier.fillMaxWidth().clickable{navigate(Route.WorldEdit,w.id)}){Column(Modifier.padding(14.dp)){Text(w.name,style=MaterialTheme.typography.titleMedium);Text("Канон: ${w.canon.length} знаков",style=MaterialTheme.typography.bodySmall)}}}
        }
        Button(onClick={val w=WorldCard();store.upsertWorld(w);navigate(Route.WorldEdit,w.id)},Modifier.fillMaxWidth().padding(12.dp)){Text("+ Создать мир")}
    }
}

@Composable
private fun WorldEditScreen(store:AppStore,id:String,onBack:()->Unit){
    val original=store.worlds.firstOrNull{it.id==id}?:WorldCard(id=id)
    var x by remember(id){mutableStateOf(original)}
    Page("Мир / канон",onBack){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            item{LongField("Название",x.name,{x=x.copy(name=it)},false)}
            item{LongField("Канон — можно очень длинный",x.canon,{x=x.copy(canon=it)},minLines=12)}
            item{LongField("Жёсткие правила мира",x.rules,{x=x.copy(rules=it)},minLines=6)}
            item{LongField("Заметки",x.notes,{x=x.copy(notes=it)},minLines=6)}
            item{Text("Хранение не ограничено коротким лимитом Friendi. Перед запросом ИИ приложение само выбирает релевантные куски большого канона.",style=MaterialTheme.typography.bodySmall)}
        }
        Button(onClick={store.upsertWorld(x);onBack()},Modifier.fillMaxWidth().padding(12.dp)){Text("Сохранить")}
    }
}

@Composable
private fun ChatListScreen(store:AppStore,navigate:(Route,String?)->Unit,back:()->Unit){
    Page("Чаты",back){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(store.chats.sortedByDescending{it.updatedAt},key={it.id}){c->
                val char=store.characters.firstOrNull{it.id==c.characterId}
                val profile=store.profiles.firstOrNull{it.id==c.profileId}
                Card(Modifier.fillMaxWidth().clickable{navigate(Route.Chat,c.id)}){Column(Modifier.padding(14.dp)){Text(c.title,style=MaterialTheme.typography.titleMedium);Text("${char?.name ?: "?"} × ${profile?.name ?: "?"}",style=MaterialTheme.typography.bodySmall)}}
            }
        }
        Button(onClick={navigate(Route.NewChat,null)},enabled=store.characters.isNotEmpty()&&store.profiles.isNotEmpty(),modifier=Modifier.fillMaxWidth().padding(12.dp)){Text("+ Новый чат")}
    }
}

@Composable
private fun NewChatScreen(store:AppStore,navigate:(Route,String?)->Unit,back:()->Unit){
    var title by remember{mutableStateOf("Новый чат")}
    var characterId by remember{mutableStateOf(store.characters.firstOrNull()?.id.orEmpty())}
    var profileId by remember{mutableStateOf(store.profiles.firstOrNull()?.id.orEmpty())}
    var worldId by remember{mutableStateOf(store.characters.firstOrNull{it.id==characterId}?.worldId.orEmpty())}
    Page("Создать чат",back){
        Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            LongField("Название чата",title,{title=it},false)
            ChoiceField("Персонаж",store.characters.map{it.id to it.name},characterId){characterId=it;worldId=store.characters.firstOrNull{c->c.id==it}?.worldId.orEmpty()}
            ChoiceField("Мой профиль",store.profiles.map{it.id to it.name},profileId){profileId=it}
            ChoiceField("Мир",listOf("" to "Без отдельного мира")+store.worlds.map{it.id to it.name},worldId){worldId=it}
            Button(onClick={
                val c=ChatThread(title=title.ifBlank{"Новый чат"},characterId=characterId,profileId=profileId,worldId=worldId)
                store.upsertChat(c)
                val greeting=store.characters.firstOrNull{it.id==characterId}?.greeting.orEmpty()
                if(greeting.isNotBlank())store.addMessage(Message(chatId=c.id,role="assistant",text=greeting))
                navigate(Route.Chat,c.id)
            },enabled=characterId.isNotBlank()&&profileId.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text("Создать")}
        }
    }
}

@Composable
private fun ChatScreen(store:AppStore,id:String,onBack:()->Unit,navigate:(Route,String?)->Unit){
    val chat=store.chats.firstOrNull{it.id==id}
    if(chat==null){Page("Чат не найден",onBack){};return}
    val character=store.characters.firstOrNull{it.id==chat.characterId}
    var input by remember{mutableStateOf("")}
    var busy by remember{mutableStateOf(false)}
    var error by remember{mutableStateOf("")}
    val scope=rememberCoroutineScope()
    val messages=store.messages.filter{it.chatId==id}.sortedBy{it.createdAt}
    Page(character?.name ?: chat.title,onBack){
        Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),horizontalArrangement=Arrangement.spacedBy(4.dp)){
            TextButton(onClick={navigate(Route.Memories,chat.characterId)}){Text("Память")}
            TextButton(onClick={navigate(Route.Relationship,chat.id)}){Text("Отношения")}
            TextButton(onClick={store.createSnapshot("${chat.title} — ${formatTime(System.currentTimeMillis())}")}){Text("Снимок")}
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(horizontal=10.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(messages,key={it.id}){m-> MessageBubble(m,character?.name ?: "Персонаж") }
            if(busy)item{Text("… персонаж отвечает",style=MaterialTheme.typography.bodySmall)}
            if(error.isNotBlank())item{Text(error,color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.bodySmall)}
        }
        Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.Bottom){
            OutlinedTextField(value=input,onValueChange={input=it},placeholder={Text("Сообщение…")},modifier=Modifier.weight(1f),maxLines=6)
            Spacer(Modifier.width(6.dp))
            Button(onClick={
                val text=input.trim(); if(text.isBlank()||busy)return@Button
                input="";error="";store.addMessage(Message(chatId=id,role="user",text=text));busy=true
                scope.launch{
                    runCatching{AiClient(store).reply(chat,text)}.onSuccess{r->
                        store.addMessage(Message(chatId=id,role="assistant",text=r.reply))
                        r.extracted?.let{e->
                            e.memories.forEach{store.upsertMemory(it)}
                            val old=store.relationships.firstOrNull{it.characterId==chat.characterId&&it.profileId==chat.profileId}
                            val rel=(old?:RelationshipState(characterId=chat.characterId,profileId=chat.profileId)).copy(
                                relationshipSummary=e.relationshipSummary?:old?.relationshipSummary.orEmpty(),
                                currentMood=e.currentMood?:old?.currentMood.orEmpty(),
                                unresolvedLines=e.unresolvedLines?:old?.unresolvedLines.orEmpty(),
                                privateNotes=e.privateNotes?:old?.privateNotes.orEmpty()
                            )
                            store.upsertRelationship(rel)
                        }
                    }.onFailure{error=it.message?:"Ошибка ИИ"}
                    busy=false
                }
            },enabled=!busy&&input.isNotBlank()){Text("➤")}
        }
    }
}

@Composable
private fun MessageBubble(m:Message,characterName:String){
    val user=m.role=="user"
    Row(Modifier.fillMaxWidth(),horizontalArrangement=if(user)Arrangement.End else Arrangement.Start){
        Surface(
            shape=RoundedCornerShape(16.dp),
            tonalElevation=if(user)3.dp else 1.dp,
            modifier=Modifier.fillMaxWidth(0.88f)
        ){
            Column(Modifier.padding(12.dp)){
                Text(if(user)"Вы" else characterName,style=MaterialTheme.typography.labelSmall,fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(3.dp));Text(m.text)
            }
        }
    }
}

@Composable
private fun MemoriesScreen(store:AppStore,characterFilter:String,back:()->Unit){
    var filter by remember(characterFilter){mutableStateOf(characterFilter)}
    var newText by remember{mutableStateOf("")}
    var newType by remember{mutableStateOf("эпизод")}
    val list=store.memories.filter{filter.isBlank()||it.characterId==filter}.sortedWith(compareByDescending<MemoryEntry>{it.importance}.thenByDescending{it.createdAt})
    Page("Память",back){
        Column(Modifier.padding(horizontal=12.dp)){
            ChoiceField("Персонаж",listOf("" to "Все персонажи")+store.characters.map{it.id to it.name},filter){filter=it}
            if(filter.isNotBlank()){
                Spacer(Modifier.height(8.dp));LongField("Добавить вручную",newText,{newText=it},minLines=2)
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically){
                    ChoiceField("Тип",listOf("факт" to "факт","эпизод" to "эпизод","отношение" to "отношение","обещание" to "обещание","секрет" to "секрет","незакрытая линия" to "незакрытая линия"),newType,{newType=it},Modifier.weight(1f))
                    Button(onClick={if(newText.isNotBlank()){store.upsertMemory(MemoryEntry(characterId=filter,type=newType,text=newText.trim()));newText=""}}){Text("+")}
                }
            }
        }
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(list,key={it.id}){m->
                var editing by remember(m.id){mutableStateOf(false)}
                var text by remember(m.id){mutableStateOf(m.text)}
                Card(Modifier.fillMaxWidth()){
                    Column(Modifier.padding(12.dp)){
                        Text("${m.type} • важность ${m.importance}/5 • ${m.source}",style=MaterialTheme.typography.labelSmall)
                        if(editing){LongField("Текст",text,{text=it},minLines=2);Row{TextButton(onClick={store.upsertMemory(m.copy(text=text));editing=false}){Text("Сохранить")};TextButton(onClick={editing=false}){Text("Отмена")}}}
                        else{Text(m.text);Row{TextButton(onClick={editing=true}){Text("Править")};TextButton(onClick={store.deleteMemory(m.id)}){Text("Удалить")}}}
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationshipScreen(store:AppStore,chatId:String,back:()->Unit){
    val chat=store.chats.firstOrNull{it.id==chatId}
    if(chat==null){Page("Отношения",back){};return}
    val old=store.relationships.firstOrNull{it.characterId==chat.characterId&&it.profileId==chat.profileId}
        ?:RelationshipState(characterId=chat.characterId,profileId=chat.profileId)
    var x by remember(chatId){mutableStateOf(old)}
    Page("Отношения и состояние",back){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            item{LongField("Состояние отношений",x.relationshipSummary,{x=x.copy(relationshipSummary=it)},minLines=5)}
            item{LongField("Текущее настроение персонажа",x.currentMood,{x=x.copy(currentMood=it)},minLines=3)}
            item{LongField("Незакрытые линии",x.unresolvedLines,{x=x.copy(unresolvedLines=it)},minLines=5)}
            item{LongField("Личные заметки персонажа",x.privateNotes,{x=x.copy(privateNotes=it)},minLines=4)}
            item{Text("Этот слой может развиваться. Ядро характера из карточки персонажа автоматически не переписывается.",style=MaterialTheme.typography.bodySmall)}
        }
        Button(onClick={store.upsertRelationship(x);back()},Modifier.fillMaxWidth().padding(12.dp)){Text("Сохранить")}
    }
}

@Composable
private fun SettingsScreen(store:AppStore,back:()->Unit){
    var s by remember{mutableStateOf(store.settings)}
    var key by remember{mutableStateOf(store.apiKey())}
    var status by remember{mutableStateOf("")}
    val context=LocalContext.current
    val exportLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")){uri-> if(uri!=null){runCatching{context.contentResolver.openOutputStream(uri)?.use{it.write(store.exportBackup().toByteArray())}}.onSuccess{status="Резервная копия сохранена"}.onFailure{status=it.message.orEmpty()}}}
    val importLauncher=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri-> if(uri!=null){runCatching{context.contentResolver.openInputStream(uri)?.bufferedReader()?.use{it.readText()}?:error("Файл не читается")}.mapCatching{store.importBackup(it)}.onSuccess{s=store.settings;status="Резервная копия импортирована"}.onFailure{status="Ошибка импорта: ${it.message}"}}}
    Page("Настройки",back){
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            item{ChoiceField("Режим API",listOf("openai_responses" to "OpenAI Responses API","chat_completions" to "OpenAI-compatible Chat Completions"),s.apiMode){mode->s=if(mode=="openai_responses")s.copy(apiMode=mode,endpoint="https://api.openai.com/v1/responses")else s.copy(apiMode=mode)}}
            item{LongField("Endpoint",s.endpoint,{s=s.copy(endpoint=it)},false)}
            item{LongField("Модель",s.model,{s=s.copy(model=it)},false)}
            item{OutlinedTextField(value=key,onValueChange={key=it},label={Text("API-ключ")},visualTransformation=PasswordVisualTransformation(),modifier=Modifier.fillMaxWidth(),singleLine=true)}
            item{SwitchLine("Антидрейф: перепроверять ответ",s.antiDrift){s=s.copy(antiDrift=it)}}
            item{SwitchLine("Автопамять после диалога",s.autoMemory){s=s.copy(autoMemory=it)}}
            item{LongField("Бюджет служебного контекста (знаков)",s.maxContextChars.toString(),{v->s=s.copy(maxContextChars=v.filter{it.isDigit()}.toIntOrNull()?.coerceIn(10000,500000)?:60000)},false)}
            item{Text("Антидрейф делает дополнительный запрос к модели. Автопамять — ещё один. Это повышает устойчивость, но увеличивает расход API.",style=MaterialTheme.typography.bodySmall)}
            item{HorizontalDivider()}
            item{Text("Резервная копия",style=MaterialTheme.typography.titleMedium)}
            item{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={exportLauncher.launch("yakor-backup.json")}){Text("Экспорт JSON")};OutlinedButton(onClick={importLauncher.launch(arrayOf("application/json","text/plain"))}){Text("Импорт")}}}
            if(status.isNotBlank())item{Text(status,style=MaterialTheme.typography.bodySmall)}
        }
        Button(onClick={store.updateSettings(s,key);back()},Modifier.fillMaxWidth().padding(12.dp)){Text("Сохранить настройки")}
    }
}

@Composable
private fun SnapshotsScreen(store:AppStore,back:()->Unit){
    var name by remember{mutableStateOf("")}
    Page("Снимки состояния",back){
        Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically){OutlinedTextField(value=name,onValueChange={name=it},label={Text("Название снимка")},modifier=Modifier.weight(1f));Spacer(Modifier.width(8.dp));Button(onClick={store.createSnapshot(name);name=""}){Text("+")}}
        LazyColumn(Modifier.weight(1f).padding(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            items(store.snapshots,key={it.id}){s->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(12.dp)){Text(s.name,style=MaterialTheme.typography.titleMedium);Text(formatTime(s.createdAt),style=MaterialTheme.typography.bodySmall);TextButton(onClick={store.restoreSnapshot(s)}){Text("Восстановить")}}}}
        }
        Text("Снимки сохраняют персонажей, профили, канон, память и чаты. API-ключ в снимок не входит.",Modifier.padding(12.dp),style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun LongField(label:String,value:String,onValue:(String)->Unit,singleLine:Boolean=false,minLines:Int=if(singleLine)1 else 4){
    OutlinedTextField(value=value,onValueChange=onValue,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=singleLine,minLines=minLines,maxLines=if(singleLine)1 else 40)
}

@Composable
private fun ChoiceField(label:String,options:List<Pair<String,String>>,selected:String,onSelect:(String)->Unit,modifier:Modifier=Modifier.fillMaxWidth()){
    var open by remember{mutableStateOf(false)}
    val title=options.firstOrNull{it.first==selected}?.second?:"Не выбрано"
    Box(modifier){
        OutlinedButton(onClick={open=true},modifier=Modifier.fillMaxWidth()){Column(Modifier.fillMaxWidth()){Text(label,style=MaterialTheme.typography.labelSmall);Text(title)}}
        DropdownMenu(expanded=open,onDismissRequest={open=false}){options.forEach{(id,name)->DropdownMenuItem(text={Text(name)},onClick={onSelect(id);open=false})}}
    }
}

@Composable
private fun WorldChoice(worlds:List<WorldCard>,selected:String,onSelect:(String)->Unit){ChoiceField("Мир / канон",listOf("" to "Без мира")+worlds.map{it.id to it.name},selected,onSelect)}

@Composable
private fun SwitchLine(text:String,value:Boolean,onChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){Text(text,Modifier.weight(1f));Switch(checked=value,onCheckedChange=onChange)}}

@Composable
private fun Avatar(uri:String,name:String,size:Int){
    val context=LocalContext.current
    val bitmap by produceState<android.graphics.Bitmap?>(initialValue=null,uri){value=if(uri.isBlank())null else runCatching{context.contentResolver.openInputStream(Uri.parse(uri))?.use{BitmapFactory.decodeStream(it)}}.getOrNull()}
    Surface(shape=CircleShape,tonalElevation=2.dp,modifier=Modifier.size(size.dp)){
        if(bitmap!=null)Image(bitmap!!.asImageBitmap(),contentDescription=name,contentScale=ContentScale.Crop,modifier=Modifier.fillMaxSize())
        else Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text(name.take(1).uppercase().ifBlank{"?"},style=MaterialTheme.typography.titleLarge)}
    }
}

@Composable
private fun ConfirmDelete(text:String,onResult:(Boolean)->Unit){AlertDialog(onDismissRequest={onResult(false)},title={Text("Подтверждение")},text={Text(text)},confirmButton={TextButton(onClick={onResult(true)}){Text("Удалить")}},dismissButton={TextButton(onClick={onResult(false)}){Text("Отмена")}})}

private fun formatTime(ms:Long):String=SimpleDateFormat("dd.MM.yyyy HH:mm",Locale.getDefault()).format(Date(ms))
