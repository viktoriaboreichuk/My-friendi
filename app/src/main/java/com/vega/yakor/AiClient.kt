package com.vega.yakor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AiClient(private val store: AppStore) {
    data class TurnResult(val reply: String, val extracted: Extraction? = null)
    data class Extraction(
        val memories: List<MemoryEntry>,
        val relationshipSummary: String?,
        val currentMood: String?,
        val unresolvedLines: String?,
        val privateNotes: String?
    )

    suspend fun reply(chat: ChatThread, userText: String): TurnResult = withContext(Dispatchers.IO) {
        val character = store.characters.firstOrNull { it.id == chat.characterId } ?: error("Персонаж не найден")
        val profile = store.profiles.firstOrNull { it.id == chat.profileId } ?: error("Профиль пользователя не найден")
        val world = store.worlds.firstOrNull { it.id == chat.worldId.ifBlank { character.worldId } }
        val relationship = store.relationships.firstOrNull { it.characterId == character.id && it.profileId == profile.id }
        val relevant = ContextEngine.relevantMemories(
            store.memories.filter { it.characterId == character.id && (it.profileId.isBlank() || it.profileId == profile.id) },
            userText
        )
        val instructions = ContextEngine.buildSystemInstructions(character, profile, world, relationship, relevant, userText, store.settings.maxContextChars)
        val allHistory = store.messages.filter { it.chatId == chat.id }.takeLast(30)
        val history = if (allHistory.lastOrNull()?.role == "user" && allHistory.lastOrNull()?.text == userText) allHistory.dropLast(1) else allHistory
        val draft = callModel(instructions, history + Message(chatId = chat.id, role = "user", text = userText))
        val finalReply = if (store.settings.antiDrift) validate(character, profile, relationship, relevant, draft) else draft
        val extraction = if (store.settings.autoMemory) extract(character, profile, relationship, userText, finalReply) else null
        TurnResult(finalReply, extraction)
    }

    private fun validate(
        character: CharacterCard,
        profile: UserProfile,
        relationship: RelationshipState?,
        memories: List<MemoryEntry>,
        draft: String
    ): String {
        val system = """
Ты — редактор устойчивости ролевого персонажа. Проверь черновик ответа на дрейф личности.
Исправляй ТОЛЬКО если есть явное нарушение: несвойственная покорность/слащавость, знание неизвестного, противоречие ядру,
резкая смена отношения без причины, выход из роли или нарушение прямого запрета.
Если всё нормально — ответь ровно PASS.
Если нужно исправить — ответь REWRITE: и затем только исправленный ответ персонажа.

ПЕРСОНАЖ: ${character.name}
ХАРАКТЕР: ${character.personality}
РЕЧЬ: ${character.speech}
ПРАВИЛА: ${character.behaviorRules}
НИКОГДА: ${character.neverDo}
ОТНОШЕНИЯ: ${relationship?.relationshipSummary.orEmpty()}
НЕЗАКРЫТОЕ: ${relationship?.unresolvedLines.orEmpty()}
СОБЕСЕДНИК: ${profile.name}
ВАЖНЫЕ ВОСПОМИНАНИЯ:
${memories.take(8).joinToString("\n") { "- ${it.text}" }}
""".trimIndent()
        val checked = callModel(system, listOf(Message(chatId="check", role="user", text="ЧЕРНОВИК:\n$draft")))
        return when {
            checked.trim() == "PASS" -> draft
            checked.startsWith("REWRITE:") -> checked.substringAfter("REWRITE:").trim().ifBlank { draft }
            else -> draft
        }
    }

    private fun extract(
        character: CharacterCard,
        profile: UserProfile,
        relationship: RelationshipState?,
        userText: String,
        assistantText: String
    ): Extraction? {
        val system = """
Ты — модуль памяти ролевого чата. Не меняй ядро личности персонажа.
Извлеки только факты, события, обещания, конфликты, раскрытые секреты и изменения отношений, которые полезно помнить через сотни сообщений.
Не сохраняй банальные приветствия, мелкую болтовню и домыслы.
Верни ТОЛЬКО валидный JSON без markdown:
{"memories":[{"type":"факт|эпизод|отношение|обещание|секрет|незакрытая линия","text":"...","importance":1,"emotionalWeight":0,"whoKnows":"персонаж"}],"relationshipSummary":"","currentMood":"","unresolvedLines":"","privateNotes":""}
importance 1..5, emotionalWeight -5..5. Пустой memories допустим.
Старое состояние отношений:
${relationship?.relationshipSummary.orEmpty()}
Старые незакрытые линии:
${relationship?.unresolvedLines.orEmpty()}
""".trimIndent()
        val raw = runCatching {
            callModel(system, listOf(Message(chatId="memory", role="user", text="${profile.name}: $userText\n${character.name}: $assistantText")))
        }.getOrNull() ?: return null
        return runCatching {
            val clean = raw.substringAfter("```json", raw).substringAfter("```", raw).substringBeforeLast("```", raw).trim()
            val o = JSONObject(clean)
            val a = o.optJSONArray("memories") ?: JSONArray()
            val list = (0 until a.length()).mapNotNull { i ->
                val m = a.optJSONObject(i) ?: return@mapNotNull null
                val text = m.optString("text").trim()
                if (text.isBlank()) null else MemoryEntry(
                    characterId = character.id,
                    profileId = profile.id,
                    type = m.optString("type","эпизод"),
                    text = text,
                    importance = m.optInt("importance",3).coerceIn(1,5),
                    emotionalWeight = m.optInt("emotionalWeight",0).coerceIn(-5,5),
                    source = "авто",
                    whoKnows = m.optString("whoKnows","персонаж")
                )
            }
            Extraction(
                memories = list,
                relationshipSummary = o.optString("relationshipSummary").takeIf { it.isNotBlank() },
                currentMood = o.optString("currentMood").takeIf { it.isNotBlank() },
                unresolvedLines = o.optString("unresolvedLines").takeIf { it.isNotBlank() },
                privateNotes = o.optString("privateNotes").takeIf { it.isNotBlank() }
            )
        }.getOrNull()
    }

    private fun callModel(instructions: String, messages: List<Message>): String {
        val key = store.apiKey()
        require(key.isNotBlank()) { "В настройках не указан API-ключ" }
        val s = store.settings
        return if (s.apiMode == "openai_responses") callResponses(s.endpoint, key, s.model, instructions, messages)
        else callChatCompletions(s.endpoint, key, s.model, instructions, messages)
    }

    private fun post(endpoint: String, key: String, body: JSONObject): JSONObject {
        val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30_000
            readTimeout = 120_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $key")
        }
        conn.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val text = BufferedReader(InputStreamReader(stream)).use { it.readText() }
        if (code !in 200..299) {
            val msg = runCatching { JSONObject(text).optJSONObject("error")?.optString("message") }.getOrNull()
            error("API $code: ${msg ?: text.take(500)}")
        }
        return JSONObject(text)
    }

    private fun callResponses(endpoint: String, key: String, model: String, instructions: String, messages: List<Message>): String {
        val input = JSONArray()
        messages.forEach { input.put(JSONObject().put("role", it.role).put("content", it.text)) }
        val body = JSONObject().put("model", model).put("instructions", instructions).put("input", input)
        val response = post(endpoint, key, body)
        val out = response.optJSONArray("output") ?: JSONArray()
        val text = StringBuilder()
        for (i in 0 until out.length()) {
            val item = out.optJSONObject(i) ?: continue
            val content = item.optJSONArray("content") ?: continue
            for (j in 0 until content.length()) {
                val part = content.optJSONObject(j) ?: continue
                if (part.optString("type") == "output_text") text.append(part.optString("text"))
            }
        }
        return text.toString().trim().ifBlank { error("API вернул пустой ответ") }
    }

    private fun callChatCompletions(endpoint: String, key: String, model: String, instructions: String, messages: List<Message>): String {
        val a = JSONArray().put(JSONObject().put("role","system").put("content",instructions))
        messages.forEach { a.put(JSONObject().put("role",it.role).put("content",it.text)) }
        val response = post(endpoint, key, JSONObject().put("model",model).put("messages",a))
        return response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").optString("content").trim()
            .ifBlank { error("API вернул пустой ответ") }
    }
}
