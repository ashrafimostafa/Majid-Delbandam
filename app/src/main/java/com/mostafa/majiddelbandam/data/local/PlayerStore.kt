package com.mostafa.majiddelbandam.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mostafa.majiddelbandam.domain.PlayerProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.playerStore by preferencesDataStore("player_progress")

class PlayerStore(context: Context) {
    private val dataStore = context.applicationContext.playerStore

    val progress: Flow<PlayerProgress> = dataStore.data.map { it.toProgress() }

    suspend fun recordVictory(levelId: Int, stars: Int, coins: Int) {
        dataStore.edit { prefs ->
            val current = prefs.toProgress()
            val best = maxOf(current.starsByLevel[levelId] ?: 0, stars)
            val starsMap = current.starsByLevel.toMutableMap()
            val firstClear = levelId !in starsMap
            starsMap[levelId] = best
            prefs[ASHRAFI] = current.ashrafi + if (firstClear) coins else coins / 2
            prefs[STARS] = encodeStars(starsMap)
        }
    }

    suspend fun addAshrafi(amount: Int) {
        dataStore.edit { prefs ->
            prefs[ASHRAFI] = (prefs.toProgress().ashrafi + amount).coerceAtLeast(0)
        }
    }

    suspend fun spendAshrafi(amount: Int): Boolean {
        var ok = false
        dataStore.edit { prefs ->
            val current = prefs.toProgress()
            if (current.ashrafi >= amount) {
                prefs[ASHRAFI] = current.ashrafi - amount
                ok = true
            }
        }
        return ok
    }

    suspend fun addHelper(scrolls: Int = 0, candles: Int = 0, fals: Int = 0) {
        dataStore.edit { prefs ->
            val p = prefs.toProgress()
            prefs[SCROLLS] = p.scrolls + scrolls
            prefs[CANDLES] = p.candles + candles
            prefs[FALS] = p.fals + fals
        }
    }

    suspend fun consumeScroll(): Boolean = consume(SCROLLS)
    suspend fun consumeCandle(): Boolean = consume(CANDLES)
    suspend fun consumeFal(): Boolean = consume(FALS)

    suspend fun markWheelSpun(epochDay: Int) {
        dataStore.edit { it[WHEEL_DAY] = epochDay }
    }

    suspend fun markDailyPlayed(epochDay: Int) {
        dataStore.edit { it[DAILY_DAY] = epochDay }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[SOUND] = enabled }
    }

    private suspend fun consume(key: androidx.datastore.preferences.core.Preferences.Key<Int>): Boolean {
        var ok = false
        dataStore.edit { prefs ->
            val value = prefs[key] ?: 1
            if (value > 0) {
                prefs[key] = value - 1
                ok = true
            }
        }
        return ok
    }

    private fun Preferences.toProgress(): PlayerProgress = PlayerProgress(
        ashrafi = this[ASHRAFI] ?: 48,
        starsByLevel = decodeStars(this[STARS] ?: ""),
        scrolls = this[SCROLLS] ?: 1,
        candles = this[CANDLES] ?: 1,
        fals = this[FALS] ?: 1,
        lastWheelEpochDay = this[WHEEL_DAY] ?: -1,
        lastDailyEpochDay = this[DAILY_DAY] ?: -1,
        soundEnabled = this[SOUND] ?: true
    )

    private fun encodeStars(map: Map<Int, Int>): String =
        map.entries.joinToString(";") { "${it.key}:${it.value}" }

    private fun decodeStars(raw: String): Map<Int, Int> {
        if (raw.isBlank()) return emptyMap()
        return raw.split(';').mapNotNull { token ->
            val parts = token.split(':')
            if (parts.size != 2) null
            else parts[0].toIntOrNull()?.let { id -> parts[1].toIntOrNull()?.let { id to it } }
        }.toMap()
    }

    private companion object {
        val ASHRAFI = intPreferencesKey("ashrafi")
        val STARS = stringPreferencesKey("stars")
        val SCROLLS = intPreferencesKey("scrolls")
        val CANDLES = intPreferencesKey("candles")
        val FALS = intPreferencesKey("fals")
        val WHEEL_DAY = intPreferencesKey("wheel_day")
        val DAILY_DAY = intPreferencesKey("daily_day")
        val SOUND = booleanPreferencesKey("sound")
    }
}
