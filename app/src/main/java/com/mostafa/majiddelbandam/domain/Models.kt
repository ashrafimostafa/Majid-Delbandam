package com.mostafa.majiddelbandam.domain

enum class Difficulty(val raw: String) {
    VERY_EASY("very_easy"),
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard"),
    EXPERT("expert"),
    DAILY("daily");

    companion object {
        fun fromRaw(value: String): Difficulty =
            entries.firstOrNull { it.raw == value } ?: MEDIUM
    }
}

data class Puzzle(
    val id: Int,
    val startWord: String,
    val endWord: String,
    val steps: Int,
    val path: List<String>,
    val difficulty: Difficulty,
    val isDaily: Boolean
)

enum class Neighborhood(
    val title: String,
    val subtitle: String,
    val ids: IntRange
) {
    HOWZ("محله سرچشمه", "عمارت قجری", 1..100),
    GERANIUM("کوچهٔ شمعدانی", "حیاط گلدان‌ها", 101..300),
    OROSI("گذر ارسی", "تالار نور رنگی", 301..500),
    BAZAAR("بازار آجری", "راستهٔ واژه‌ها", 501..700),
    PALACE("کاخ فیروزه", "ایوان ادیبان", 701..900);

    companion object {
        fun of(id: Int): Neighborhood = entries.firstOrNull { id in it.ids } ?: HOWZ
    }
}

data class PlayerProgress(
    val ashrafi: Int = 48,
    val starsByLevel: Map<Int, Int> = emptyMap(),
    val scrolls: Int = 1,
    val candles: Int = 1,
    val fals: Int = 1,
    val lastWheelEpochDay: Int = -1,
    val lastDailyEpochDay: Int = -1,
    val soundEnabled: Boolean = true
) {
    val completedIds: Set<Int> get() = starsByLevel.keys

    fun nextCampaignId(): Int {
        for (id in 1..900) {
            if (id !in completedIds) return id
        }
        return 900
    }

    fun isUnlocked(id: Int): Boolean {
        if (id == 1) return true
        if (id in 901..950) return true
        return (id - 1) in completedIds
    }
}

data class Helpers(
    val scrolls: Int,
    val candles: Int,
    val fals: Int
)

data class Victory(
    val stars: Int,
    val coins: Int,
    val elapsedMs: Long,
    val quote: String,
    val stepsTaken: Int,
    val optimalSteps: Int,
    val startWord: String,
    val endWord: String,
    val usedHelp: Boolean,
    val wordCount: Int
)

fun starsForSteps(stepsTaken: Int, shortest: Int): Int = when {
    stepsTaken <= shortest -> 3
    stepsTaken <= shortest + 2 -> 2
    else -> 1
}
