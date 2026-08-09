package com.vega.yakor

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

class AppStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("yakor_store", Context.MODE_PRIVATE)

    val characters = mutableStateListOf<CharacterCard>()
    val profiles = mutableStateListOf<UserProfile>()
    val worlds = mutableStateListOf<WorldCard>()
    val memories = mutableStateListOf<MemoryEntry>()
    val relationships = mutableStateListOf<RelationshipState>()
    val chats = mutableStateListOf<ChatThread>()
    val messages = mutableStateListOf<Message>()
    val snapshots = mutableStateListOf<Snapshot>()
    var settings by mutableStateOf(AppSettings())
        private set

    init { load() }

    private fun load() {
        runCatching {
            characters.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("characters", "[]")), JsonCodec::characterFromJson))
            profiles.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("profiles", "[]")), JsonCodec::profileFromJson))
            worlds.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("worlds", "[]")), JsonCodec::worldFromJson))
            memories.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("memories", "[]")), JsonCodec::memoryFromJson))
            relationships.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("relationships", "[]")), JsonCodec::relationshipFromJson))
            chats.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("chats", "[]")), JsonCodec::chatFromJson))
            messages.addAll(JsonCodec.decodeList(JSONArray(prefs.getString("messages", "[]")), JsonCodec::messageFromJson))
            settings = JsonCodec.settingsFromJson(JSONObject(prefs.getString("settings", "{}") ?: "{}"))
            val snaps = JSONArray(prefs.getString("snapshots", "[]"))
            for (i in 0 until snaps.length()) {
                val o = snaps.getJSONObject(i)
                snapshots.add(Snapshot(o.optString("id"), o.optString("name"), o.optLong("createdAt"), o.optString("payload")))
            }
        }
        if (profiles.isEmpty()) profiles.add(UserProfile())
        persist()
    }

    fun persist() {
        prefs.edit()
            .putString("characters", JsonCodec.encodeList(characters, JsonCodec::characterToJson).toString())
            .putString("profiles", JsonCodec.encodeList(profiles, JsonCodec::profileToJson).toString())
            .putString("worlds", JsonCodec.encodeList(worlds, JsonCodec::worldToJson).toString())
            .putString("memories", JsonCodec.encodeList(memories, JsonCodec::memoryToJson).toString())
            .putString("relationships", JsonCodec.encodeList(relationships, JsonCodec::relationshipToJson).toString())
            .putString("chats", JsonCodec.encodeList(chats, JsonCodec::chatToJson).toString())
            .putString("messages", JsonCodec.encodeList(messages, JsonCodec::messageToJson).toString())
            .putString("settings", JsonCodec.settingsToJson(settings).toString())
            .putString("snapshots", JSONArray().apply {
                snapshots.forEach { put(JSONObject().apply { put("id",it.id); put("name",it.name); put("createdAt",it.createdAt); put("payload",it.payload) }) }
            }.toString())
            .apply()
    }

    fun upsertCharacter(x: CharacterCard) { replaceOrAdd(characters, x) { it.id }; persist() }
    fun upsertProfile(x: UserProfile) { replaceOrAdd(profiles, x) { it.id }; persist() }
    fun upsertWorld(x: WorldCard) { replaceOrAdd(worlds, x) { it.id }; persist() }
    fun upsertMemory(x: MemoryEntry) { replaceOrAdd(memories, x) { it.id }; persist() }
    fun upsertRelationship(x: RelationshipState) { replaceOrAdd(relationships, x) { it.id }; persist() }
    fun upsertChat(x: ChatThread) { replaceOrAdd(chats, x) { it.id }; persist() }

    private fun <T> replaceOrAdd(list: MutableList<T>, item: T, id: (T)->String) {
        val i = list.indexOfFirst { id(it) == id(item) }
        if (i >= 0) list[i] = item else list.add(item)
    }

    fun deleteCharacter(id: String) {
        characters.removeAll { it.id == id }; memories.removeAll { it.characterId == id }; relationships.removeAll { it.characterId == id }
        val chatIds = chats.filter { it.characterId == id }.map { it.id }.toSet()
        chats.removeAll { it.id in chatIds }; messages.removeAll { it.chatId in chatIds }; persist()
    }
    fun deleteProfile(id: String) { profiles.removeAll { it.id == id }; relationships.removeAll { it.profileId == id }; persist() }
    fun deleteWorld(id: String) { worlds.removeAll { it.id == id }; persist() }
    fun deleteMemory(id: String) { memories.removeAll { it.id == id }; persist() }
    fun deleteChat(id: String) { chats.removeAll { it.id == id }; messages.removeAll { it.chatId == id }; persist() }

    fun addMessage(m: Message) {
        messages.add(m)
        val i = chats.indexOfFirst { it.id == m.chatId }
        if (i >= 0) chats[i] = chats[i].copy(updatedAt = System.currentTimeMillis())
        persist()
    }

    fun updateSettings(newSettings: AppSettings, plainApiKey: String? = null) {
        settings = if (plainApiKey != null) newSettings.copy(apiKeyEncrypted = Crypto.encrypt(plainApiKey)) else newSettings
        persist()
    }

    fun apiKey(): String = Crypto.decrypt(settings.apiKeyEncrypted)

    fun exportBackup(): String = JSONObject().apply {
        put("format", "yakor-backup-v1")
        put("characters", JsonCodec.encodeList(characters, JsonCodec::characterToJson))
        put("profiles", JsonCodec.encodeList(profiles, JsonCodec::profileToJson))
        put("worlds", JsonCodec.encodeList(worlds, JsonCodec::worldToJson))
        put("memories", JsonCodec.encodeList(memories, JsonCodec::memoryToJson))
        put("relationships", JsonCodec.encodeList(relationships, JsonCodec::relationshipToJson))
        put("chats", JsonCodec.encodeList(chats, JsonCodec::chatToJson))
        put("messages", JsonCodec.encodeList(messages, JsonCodec::messageToJson))
        put("settings", JsonCodec.settingsToJson(settings.copy(apiKeyEncrypted = "")))
    }.toString(2)

    fun importBackup(raw: String) {
        val o = JSONObject(raw)
        require(o.optString("format") == "yakor-backup-v1") { "Неизвестный формат резервной копии" }
        characters.replaceWith(JsonCodec.decodeList(o.optJSONArray("characters") ?: JSONArray(), JsonCodec::characterFromJson))
        profiles.replaceWith(JsonCodec.decodeList(o.optJSONArray("profiles") ?: JSONArray(), JsonCodec::profileFromJson))
        worlds.replaceWith(JsonCodec.decodeList(o.optJSONArray("worlds") ?: JSONArray(), JsonCodec::worldFromJson))
        memories.replaceWith(JsonCodec.decodeList(o.optJSONArray("memories") ?: JSONArray(), JsonCodec::memoryFromJson))
        relationships.replaceWith(JsonCodec.decodeList(o.optJSONArray("relationships") ?: JSONArray(), JsonCodec::relationshipFromJson))
        chats.replaceWith(JsonCodec.decodeList(o.optJSONArray("chats") ?: JSONArray(), JsonCodec::chatFromJson))
        messages.replaceWith(JsonCodec.decodeList(o.optJSONArray("messages") ?: JSONArray(), JsonCodec::messageFromJson))
        val importedSettings = JsonCodec.settingsFromJson(o.optJSONObject("settings") ?: JSONObject())
        settings = importedSettings.copy(apiKeyEncrypted = settings.apiKeyEncrypted)
        if (profiles.isEmpty()) profiles.add(UserProfile())
        persist()
    }

    private fun <T> MutableList<T>.replaceWith(items: List<T>) { clear(); addAll(items) }

    fun createSnapshot(name: String) {
        val payload = exportBackup()
        snapshots.add(0, Snapshot(name = name.ifBlank { "Снимок" }, payload = payload))
        while (snapshots.size > 20) snapshots.removeLast()
        persist()
    }

    fun restoreSnapshot(snapshot: Snapshot) { importBackup(snapshot.payload) }
}
