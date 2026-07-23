package com.islamic.tasbihcounter.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.islamic.tasbihcounter.data.model.DailyRecord
import com.islamic.tasbihcounter.data.model.Dhikr
import com.islamic.tasbihcounter.data.model.ThemeMode
import com.islamic.tasbihcounter.data.model.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbih_prefs")

/** Immutable snapshot of everything persisted, exposed as a single Flow to the UI. */
data class TasbihState(
    val customDhikr: List<Dhikr> = emptyList(),
    val counts: Map<String, Int> = emptyMap(),
    val selectedDhikrId: String = PresetDhikr.list.first().id,
    val history: List<DailyRecord> = emptyList(),
    val lifetimeCount: Int = 0,
    val goalsMet: Int = 0,
    val asmaProgress: Int = 0,
    // Settings
    val hapticsEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val keepScreenOn: Boolean = true,
    val voiceEnabled: Boolean = false,
    val themeStyle: ThemeStyle = ThemeStyle.MUSHAF,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val remindersEnabled: Boolean = false,
    val morningHour: Int = 7,
    val eveningHour: Int = 19,
    val dynamicColor: Boolean = false
) {
    /** All selectable dhikr = presets followed by user-defined ones. */
    val allDhikr: List<Dhikr> get() = PresetDhikr.list + customDhikr

    val selectedDhikr: Dhikr
        get() = allDhikr.firstOrNull { it.id == selectedDhikrId } ?: PresetDhikr.list.first()

    fun countFor(id: String): Int = counts[id] ?: 0
}

class TasbihRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val CUSTOM_DHIKR = stringPreferencesKey("custom_dhikr")
        val COUNTS = stringPreferencesKey("counts")
        val SELECTED = stringPreferencesKey("selected_dhikr")
        val HISTORY = stringPreferencesKey("history")
        val LIFETIME = intPreferencesKey("lifetime_count")
        val GOALS_MET = intPreferencesKey("goals_met")
        val ASMA_PROGRESS = intPreferencesKey("asma_progress")
        val HAPTICS = booleanPreferencesKey("haptics")
        val SOUND = booleanPreferencesKey("sound")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val VOICE = booleanPreferencesKey("voice")
        val THEME_STYLE = stringPreferencesKey("theme_style")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val REMINDERS = booleanPreferencesKey("reminders")
        val MORNING = intPreferencesKey("morning_hour")
        val EVENING = intPreferencesKey("evening_hour")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val state: Flow<TasbihState> = context.dataStore.data.map { p ->
        TasbihState(
            customDhikr = p[Keys.CUSTOM_DHIKR]?.let { runCatching { json.decodeFromString<List<Dhikr>>(it) }.getOrNull() } ?: emptyList(),
            counts = p[Keys.COUNTS]?.let { runCatching { json.decodeFromString<Map<String, Int>>(it) }.getOrNull() } ?: emptyMap(),
            selectedDhikrId = p[Keys.SELECTED] ?: PresetDhikr.list.first().id,
            history = p[Keys.HISTORY]?.let { runCatching { json.decodeFromString<List<DailyRecord>>(it) }.getOrNull() } ?: emptyList(),
            lifetimeCount = p[Keys.LIFETIME] ?: 0,
            goalsMet = p[Keys.GOALS_MET] ?: 0,
            asmaProgress = p[Keys.ASMA_PROGRESS] ?: 0,
            hapticsEnabled = p[Keys.HAPTICS] ?: true,
            soundEnabled = p[Keys.SOUND] ?: false,
            keepScreenOn = p[Keys.KEEP_SCREEN_ON] ?: true,
            voiceEnabled = p[Keys.VOICE] ?: false,
            themeStyle = p[Keys.THEME_STYLE]?.let { runCatching { ThemeStyle.valueOf(it) }.getOrNull() } ?: ThemeStyle.MUSHAF,
            themeMode = p[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            remindersEnabled = p[Keys.REMINDERS] ?: false,
            morningHour = p[Keys.MORNING] ?: 7,
            eveningHour = p[Keys.EVENING] ?: 19,
            dynamicColor = p[Keys.DYNAMIC_COLOR] ?: false
        )
    }

    /** Increment the active dhikr count, lifetime count, and today's history total. */
    suspend fun increment(dhikrId: String, today: String, goalTarget: Int) {
        context.dataStore.edit { p ->
            val counts = readCounts(p).toMutableMap()
            val prev = counts[dhikrId] ?: 0
            val next = prev + 1
            counts[dhikrId] = next
            p[Keys.COUNTS] = json.encodeToString(counts)
            p[Keys.LIFETIME] = (p[Keys.LIFETIME] ?: 0) + 1

            // Track goal completion the moment the active dhikr crosses its target.
            if (goalTarget > 0 && next == goalTarget) {
                p[Keys.GOALS_MET] = (p[Keys.GOALS_MET] ?: 0) + 1
            }

            val history = readHistory(p).toMutableList()
            val idx = history.indexOfFirst { it.date == today }
            if (idx >= 0) history[idx] = history[idx].copy(count = history[idx].count + 1)
            else history.add(DailyRecord(today, 1))
            p[Keys.HISTORY] = json.encodeToString(history)
        }
    }

    /** Reset only the active dhikr's session count to zero (history is preserved). */
    suspend fun resetCount(dhikrId: String) {
        context.dataStore.edit { p ->
            val counts = readCounts(p).toMutableMap()
            counts[dhikrId] = 0
            p[Keys.COUNTS] = json.encodeToString(counts)
        }
    }

    suspend fun setSelectedDhikr(id: String) {
        context.dataStore.edit { it[Keys.SELECTED] = id }
    }

    suspend fun addCustomDhikr(dhikr: Dhikr) {
        context.dataStore.edit { p ->
            val list = readCustom(p).toMutableList()
            list.add(dhikr)
            p[Keys.CUSTOM_DHIKR] = json.encodeToString(list)
        }
    }

    suspend fun updateCustomDhikr(dhikr: Dhikr) {
        context.dataStore.edit { p ->
            val list = readCustom(p).toMutableList()
            val idx = list.indexOfFirst { it.id == dhikr.id }
            if (idx >= 0) list[idx] = dhikr
            p[Keys.CUSTOM_DHIKR] = json.encodeToString(list)
        }
    }

    suspend fun deleteCustomDhikr(id: String) {
        context.dataStore.edit { p ->
            val list = readCustom(p).filterNot { it.id == id }
            p[Keys.CUSTOM_DHIKR] = json.encodeToString(list)
            if ((p[Keys.SELECTED] ?: "") == id) p[Keys.SELECTED] = PresetDhikr.list.first().id
        }
    }

    /** Set the target for a preset dhikr override or custom dhikr. Custom only. */
    suspend fun setAsmaProgress(value: Int) {
        context.dataStore.edit { it[Keys.ASMA_PROGRESS] = value.coerceIn(0, 99) }
    }

    suspend fun updateSettings(transform: (TasbihState) -> TasbihState) {
        // Read current, apply transform, persist the settings-relevant fields.
        context.dataStore.edit { p ->
            val current = TasbihState(
                hapticsEnabled = p[Keys.HAPTICS] ?: true,
                soundEnabled = p[Keys.SOUND] ?: false,
                keepScreenOn = p[Keys.KEEP_SCREEN_ON] ?: true,
                voiceEnabled = p[Keys.VOICE] ?: false,
                themeStyle = p[Keys.THEME_STYLE]?.let { runCatching { ThemeStyle.valueOf(it) }.getOrNull() } ?: ThemeStyle.MUSHAF,
                themeMode = p[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
                remindersEnabled = p[Keys.REMINDERS] ?: false,
                morningHour = p[Keys.MORNING] ?: 7,
                eveningHour = p[Keys.EVENING] ?: 19,
                dynamicColor = p[Keys.DYNAMIC_COLOR] ?: false
            )
            val updated = transform(current)
            p[Keys.HAPTICS] = updated.hapticsEnabled
            p[Keys.SOUND] = updated.soundEnabled
            p[Keys.KEEP_SCREEN_ON] = updated.keepScreenOn
            p[Keys.VOICE] = updated.voiceEnabled
            p[Keys.THEME_STYLE] = updated.themeStyle.name
            p[Keys.THEME_MODE] = updated.themeMode.name
            p[Keys.REMINDERS] = updated.remindersEnabled
            p[Keys.MORNING] = updated.morningHour
            p[Keys.EVENING] = updated.eveningHour
            p[Keys.DYNAMIC_COLOR] = updated.dynamicColor
        }
    }

    private fun readCounts(p: Preferences): Map<String, Int> =
        p[Keys.COUNTS]?.let { runCatching { json.decodeFromString<Map<String, Int>>(it) }.getOrNull() } ?: emptyMap()

    private fun readHistory(p: Preferences): List<DailyRecord> =
        p[Keys.HISTORY]?.let { runCatching { json.decodeFromString<List<DailyRecord>>(it) }.getOrNull() } ?: emptyList()

    private fun readCustom(p: Preferences): List<Dhikr> =
        p[Keys.CUSTOM_DHIKR]?.let { runCatching { json.decodeFromString<List<Dhikr>>(it) }.getOrNull() } ?: emptyList()
}
