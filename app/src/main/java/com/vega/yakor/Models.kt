package com.vega.yakor

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class CharacterCard(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Новый персонаж",
    val avatarUri: String = "",
    val galleryUris: List<String> = emptyList(),
    val age: String = "",
    val role: String = "",
    val appearance: String = "",
    val personality: String = "",
    val biography: String = "",
    val values: String = "",
    val fears: String = "",
    val speech: String = "",
    val behaviorRules: String = "",
    val neverDo: String = "",
    val greeting: String = "",
    val examples: String = "",
    val extra: String = "",
    val worldId: String = ""
)

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Мой профиль",
    val avatarUri: String = "",
    val galleryUris: List<String> = emptyList(),
    val appearance: String = "",
    val biography: String = "",
    val personality: String = "",
    val knownInitially: String = "",
    val hiddenInitially: String = "",
    val roleInStory: String = "",
    val extra: String = ""
)

data class WorldCard(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Новый мир",
    val canon: String = "",
    val rules: String = "",
    val notes: String = ""
)

data class MemoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val characterId: String,
    val profileId: String = "",
    val type: String = "эпизод",
    val text: String,
    val importance: Int = 3,
    val emotionalWeight: Int = 0,
    val source: String = "ручная",
    val whoKnows: String = "персонаж",
    val createdAt: Long = System.currentTimeMillis(),
    val lastRecalledAt: Long = 0L
)

data class RelationshipState(
    val id: String = UUID.randomUUID().toString(),
    val characterId: String,
    val profileId: String,
    val relationshipSummary: String = "",
    val currentMood: String = "",
    val unresolvedLines: String = "",
    val privateNotes: String = ""
)

data class ChatThread(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Новый чат",
    val characterId: String,
    val profileId: String,
    val worldId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val role: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class Snapshot(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val payload: String
)

data class AppSettings(
    val apiMode: String = "openai_responses",
    val endpoint: String = "https://api.openai.com/v1/responses",
    val apiKeyEncrypted: String = "",
    val model: String = "gpt-5-mini",
    val antiDrift: Boolean = true,
    val autoMemory: Boolean = true,
    val maxContextChars: Int = 60000
)

object JsonCodec {
    private fun JSONObject.s(key: String) = optString(key, "")

    fun characterToJson(x: CharacterCard) = JSONObject().apply {
        put("id", x.id); put("name", x.name); put("avatarUri", x.avatarUri); put("galleryUris", JSONArray(x.galleryUris)); put("age", x.age)
        put("role", x.role); put("appearance", x.appearance); put("personality", x.personality)
        put("biography", x.biography); put("values", x.values); put("fears", x.fears)
        put("speech", x.speech); put("behaviorRules", x.behaviorRules); put("neverDo", x.neverDo)
        put("greeting", x.greeting); put("examples", x.examples); put("extra", x.extra); put("worldId", x.worldId)
    }

    fun characterFromJson(o: JSONObject) = CharacterCard(
        id=o.s("id"), name=o.s("name"), avatarUri=o.s("avatarUri"), galleryUris=(o.optJSONArray("galleryUris") ?: JSONArray()).let { a -> (0 until a.length()).map { a.optString(it) } }, age=o.s("age"), role=o.s("role"),
        appearance=o.s("appearance"), personality=o.s("personality"), biography=o.s("biography"),
        values=o.s("values"), fears=o.s("fears"), speech=o.s("speech"), behaviorRules=o.s("behaviorRules"),
        neverDo=o.s("neverDo"), greeting=o.s("greeting"), examples=o.s("examples"), extra=o.s("extra"), worldId=o.s("worldId")
    )

    fun profileToJson(x: UserProfile) = JSONObject().apply {
        put("id",x.id); put("name",x.name); put("avatarUri",x.avatarUri); put("galleryUris",JSONArray(x.galleryUris)); put("appearance",x.appearance)
        put("biography",x.biography); put("personality",x.personality); put("knownInitially",x.knownInitially)
        put("hiddenInitially",x.hiddenInitially); put("roleInStory",x.roleInStory); put("extra",x.extra)
    }

    fun profileFromJson(o: JSONObject) = UserProfile(
        id=o.s("id"), name=o.s("name"), avatarUri=o.s("avatarUri"), galleryUris=(o.optJSONArray("galleryUris") ?: JSONArray()).let { a -> (0 until a.length()).map { a.optString(it) } }, appearance=o.s("appearance"),
        biography=o.s("biography"), personality=o.s("personality"), knownInitially=o.s("knownInitially"),
        hiddenInitially=o.s("hiddenInitially"), roleInStory=o.s("roleInStory"), extra=o.s("extra")
    )

    fun worldToJson(x: WorldCard) = JSONObject().apply {
        put("id",x.id); put("name",x.name); put("canon",x.canon); put("rules",x.rules); put("notes",x.notes)
    }
    fun worldFromJson(o: JSONObject) = WorldCard(o.s("id"),o.s("name"),o.s("canon"),o.s("rules"),o.s("notes"))

    fun memoryToJson(x: MemoryEntry) = JSONObject().apply {
        put("id",x.id); put("characterId",x.characterId); put("profileId",x.profileId); put("type",x.type); put("text",x.text)
        put("importance",x.importance); put("emotionalWeight",x.emotionalWeight); put("source",x.source); put("whoKnows",x.whoKnows)
        put("createdAt",x.createdAt); put("lastRecalledAt",x.lastRecalledAt)
    }
    fun memoryFromJson(o: JSONObject) = MemoryEntry(
        id=o.s("id"), characterId=o.s("characterId"), profileId=o.s("profileId"), type=o.s("type"), text=o.s("text"),
        importance=o.optInt("importance",3), emotionalWeight=o.optInt("emotionalWeight",0), source=o.s("source"),
        whoKnows=o.s("whoKnows"), createdAt=o.optLong("createdAt",System.currentTimeMillis()), lastRecalledAt=o.optLong("lastRecalledAt",0L)
    )

    fun relationshipToJson(x: RelationshipState) = JSONObject().apply {
        put("id",x.id); put("characterId",x.characterId); put("profileId",x.profileId)
        put("relationshipSummary",x.relationshipSummary); put("currentMood",x.currentMood)
        put("unresolvedLines",x.unresolvedLines); put("privateNotes",x.privateNotes)
    }
    fun relationshipFromJson(o: JSONObject) = RelationshipState(
        id=o.s("id"), characterId=o.s("characterId"), profileId=o.s("profileId"),
        relationshipSummary=o.s("relationshipSummary"), currentMood=o.s("currentMood"),
        unresolvedLines=o.s("unresolvedLines"), privateNotes=o.s("privateNotes")
    )

    fun chatToJson(x: ChatThread) = JSONObject().apply {
        put("id",x.id); put("title",x.title); put("characterId",x.characterId); put("profileId",x.profileId); put("worldId",x.worldId)
        put("createdAt",x.createdAt); put("updatedAt",x.updatedAt)
    }
    fun chatFromJson(o: JSONObject) = ChatThread(
        id=o.s("id"), title=o.s("title"), characterId=o.s("characterId"), profileId=o.s("profileId"), worldId=o.s("worldId"),
        createdAt=o.optLong("createdAt",System.currentTimeMillis()), updatedAt=o.optLong("updatedAt",System.currentTimeMillis())
    )

    fun messageToJson(x: Message) = JSONObject().apply {
        put("id",x.id); put("chatId",x.chatId); put("role",x.role); put("text",x.text); put("createdAt",x.createdAt)
    }
    fun messageFromJson(o: JSONObject) = Message(o.s("id"),o.s("chatId"),o.s("role"),o.s("text"),o.optLong("createdAt",System.currentTimeMillis()))

    fun settingsToJson(x: AppSettings) = JSONObject().apply {
        put("apiMode",x.apiMode); put("endpoint",x.endpoint); put("apiKeyEncrypted",x.apiKeyEncrypted); put("model",x.model)
        put("antiDrift",x.antiDrift); put("autoMemory",x.autoMemory); put("maxContextChars",x.maxContextChars)
    }
    fun settingsFromJson(o: JSONObject) = AppSettings(
        apiMode=o.optString("apiMode","openai_responses"), endpoint=o.optString("endpoint","https://api.openai.com/v1/responses"),
        apiKeyEncrypted=o.s("apiKeyEncrypted"), model=o.optString("model","gpt-5-mini"), antiDrift=o.optBoolean("antiDrift",true),
        autoMemory=o.optBoolean("autoMemory",true), maxContextChars=o.optInt("maxContextChars",60000)
    )

    fun <T> encodeList(items: List<T>, encoder: (T)->JSONObject): JSONArray = JSONArray().apply { items.forEach { put(encoder(it)) } }
    fun <T> decodeList(a: JSONArray, decoder: (JSONObject)->T): List<T> = (0 until a.length()).map { decoder(a.getJSONObject(it)) }
}
