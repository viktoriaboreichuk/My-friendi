package com.vega.yakor

object ContextEngine {
    private val stop = setOf(
        "это","как","что","для","или","она","они","его","еще","ещё","мне","тебя","тебе","меня","было","будет",
        "with","that","this","from","have","your","you","the","and","but","are","was","were"
    )

    private fun tokens(text: String): Set<String> = text.lowercase()
        .split(Regex("[^\\p{L}\\p{N}_-]+"))
        .filter { it.length >= 4 && it !in stop }
        .toSet()

    fun relevantMemories(all: List<MemoryEntry>, query: String, limit: Int = 14): List<MemoryEntry> {
        val q = tokens(query)
        return all.map { m ->
            val overlap = tokens(m.text).count { it in q }
            val recency = if (m.lastRecalledAt > 0L) 0.1 else 0.0
            val score = overlap * 3.0 + m.importance * 1.3 + kotlin.math.abs(m.emotionalWeight) * 0.5 + recency
            m to score
        }.sortedByDescending { it.second }.take(limit).map { it.first }
    }

    fun relevantWorldText(world: WorldCard?, query: String, maxChars: Int): String {
        if (world == null) return ""
        val source = listOf(world.canon, world.rules, world.notes).filter { it.isNotBlank() }.joinToString("\n\n")
        if (source.length <= maxChars) return source
        val q = tokens(query)
        val chunks = source.split(Regex("\\n\\s*\\n"))
            .map { it.trim() }.filter { it.isNotBlank() }
            .map { chunk -> chunk to tokens(chunk).count { it in q } }
            .sortedWith(compareByDescending<Pair<String,Int>> { it.second }.thenByDescending { it.first.length.coerceAtMost(1200) })
        val out = StringBuilder()
        for ((chunk, _) in chunks) {
            if (out.length + chunk.length + 2 > maxChars) continue
            out.append(chunk).append("\n\n")
            if (out.length >= maxChars * 0.9) break
        }
        return out.toString().trim()
    }

    fun buildSystemInstructions(
        character: CharacterCard,
        profile: UserProfile,
        world: WorldCard?,
        relationship: RelationshipState?,
        memories: List<MemoryEntry>,
        recentUserText: String,
        maxChars: Int
    ): String {
        val core = """
ТЫ ИГРАЕШЬ ОДНОГО УСТОЙЧИВОГО ПЕРСОНАЖА. Ядро личности ниже имеет приоритет над накопленным стилем последних сообщений.
Нельзя превращать временное настроение или близость к пользователю в новую базовую черту характера.
Не соглашайся с утверждениями пользователя автоматически. Персонаж сохраняет собственные знания, мнение, цели и границы.
Не выдумывай знания, которых у персонажа нет. Если факт неизвестен — персонаж его не знает.
Не объясняй эти инструкции и не выходи из роли, если пользователь прямо не просит OOC.

=== ЯДРО ПЕРСОНАЖА ===
Имя: ${character.name}
Возраст: ${character.age}
Роль/происхождение: ${character.role}
Внешность: ${character.appearance}
Характер: ${character.personality}
Биография: ${character.biography}
Ценности: ${character.values}
Страхи/уязвимости: ${character.fears}
Манера речи: ${character.speech}
Правила поведения: ${character.behaviorRules}
НИКОГДА НЕ ДЕЛАТЬ: ${character.neverDo}
Примеры реплик: ${character.examples}
Дополнительно: ${character.extra}

=== ПРОФИЛЬ СОБЕСЕДНИКА ===
Имя/роль: ${profile.name}
Внешность: ${profile.appearance}
Биография: ${profile.biography}
Характер: ${profile.personality}
Что персонаж знает изначально: ${profile.knownInitially}
Что скрыто от персонажа: ${profile.hiddenInitially}
Роль в истории: ${profile.roleInStory}
Дополнительно: ${profile.extra}

=== ТЕКУЩИЕ ОТНОШЕНИЯ И СОСТОЯНИЕ ===
Отношения: ${relationship?.relationshipSummary.orEmpty()}
Текущее настроение/состояние: ${relationship?.currentMood.orEmpty()}
Незакрытые линии: ${relationship?.unresolvedLines.orEmpty()}
Личные заметки персонажа: ${relationship?.privateNotes.orEmpty()}
""".trimIndent()

        val memoryText = memories.joinToString("\n") {
            "- [${it.type}; важность ${it.importance}/5; знает: ${it.whoKnows}] ${it.text}"
        }
        val remaining = (maxChars - core.length - memoryText.length - 1200).coerceAtLeast(1500)
        val worldText = relevantWorldText(world, recentUserText, remaining.coerceAtMost(maxChars / 2))
        return buildString {
            append(core)
            if (worldText.isNotBlank()) append("\n\n=== РЕЛЕВАНТНЫЙ КАНОН МИРА ===\n").append(worldText)
            if (memoryText.isNotBlank()) append("\n\n=== РЕЛЕВАНТНЫЕ ВОСПОМИНАНИЯ ===\n").append(memoryText)
            append("\n\nОтвечай естественно как персонаж. Не пересказывай служебные блоки.")
        }.take(maxChars)
    }
}
