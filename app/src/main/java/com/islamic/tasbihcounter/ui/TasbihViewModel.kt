package com.islamic.tasbihcounter.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.islamic.tasbihcounter.data.BadgeData
import com.islamic.tasbihcounter.data.TasbihRepository
import com.islamic.tasbihcounter.data.TasbihState
import com.islamic.tasbihcounter.data.model.Badge
import com.islamic.tasbihcounter.data.model.BadgeKind
import com.islamic.tasbihcounter.data.model.Dhikr
import com.islamic.tasbihcounter.data.model.ThemeMode
import com.islamic.tasbihcounter.data.model.ThemeStyle
import com.islamic.tasbihcounter.util.DateUtils
import com.islamic.tasbihcounter.util.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Snapshot of gamification stats derived from persisted state. */
data class Stats(
    val currentStreak: Int,
    val longestStreak: Int,
    val lifetimeCount: Int,
    val todayCount: Int,
    val goalsMet: Int,
    val earnedBadgeIds: Set<String>
)

class TasbihViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = TasbihRepository(app)

    val state: StateFlow<TasbihState> = repo.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasbihState()
    )

    /** True the moment an increment call brings the active dhikr to its target. */
    var lastReachedGoal: Boolean = false
        private set

    fun stats(s: TasbihState): Stats {
        val streak = DateUtils.currentStreak(s.history)
        val longest = DateUtils.longestStreak(s.history)
        val today = s.history.firstOrNull { it.date == DateUtils.today() }?.count ?: 0
        val earned = BadgeData.list.filter { badge ->
            when (badge.kind) {
                BadgeKind.STREAK -> longest >= badge.requirement
                BadgeKind.LIFETIME_COUNT -> s.lifetimeCount >= badge.requirement
                BadgeKind.DAILY_GOAL -> s.goalsMet >= badge.requirement
            }
        }.map { it.id }.toSet()
        return Stats(streak, longest, s.lifetimeCount, today, s.goalsMet, earned)
    }

    fun increment() {
        val s = state.value
        val dhikr = s.selectedDhikr
        val before = s.countFor(dhikr.id)
        lastReachedGoal = dhikr.target > 0 && before + 1 == dhikr.target
        viewModelScope.launch {
            repo.increment(dhikr.id, DateUtils.today(), dhikr.target)
        }
    }

    fun resetCurrent() {
        viewModelScope.launch { repo.resetCount(state.value.selectedDhikr.id) }
    }

    fun selectDhikr(id: String) {
        viewModelScope.launch { repo.setSelectedDhikr(id) }
    }

    fun addCustomDhikr(arabic: String, transliteration: String, translation: String, target: Int) {
        val id = "custom_${transliteration.hashCode()}_${arabic.length}_$target"
        viewModelScope.launch {
            repo.addCustomDhikr(Dhikr(id, arabic, transliteration, translation, target, isPreset = false))
        }
    }

    fun updateCustomDhikr(dhikr: Dhikr) {
        viewModelScope.launch { repo.updateCustomDhikr(dhikr) }
    }

    fun deleteCustomDhikr(id: String) {
        viewModelScope.launch { repo.deleteCustomDhikr(id) }
    }

    // --- Asmaul Husna counter ---
    fun setAsmaProgress(value: Int) {
        viewModelScope.launch { repo.setAsmaProgress(value) }
    }

    // --- Settings ---
    fun setHaptics(v: Boolean) = updateSettings { it.copy(hapticsEnabled = v) }
    fun setSound(v: Boolean) = updateSettings { it.copy(soundEnabled = v) }
    fun setKeepScreenOn(v: Boolean) = updateSettings { it.copy(keepScreenOn = v) }
    fun setVoice(v: Boolean) = updateSettings { it.copy(voiceEnabled = v) }
    fun setThemeStyle(v: ThemeStyle) = updateSettings { it.copy(themeStyle = v) }
    fun setThemeMode(v: ThemeMode) = updateSettings { it.copy(themeMode = v) }
    fun setDynamicColor(v: Boolean) = updateSettings { it.copy(dynamicColor = v) }
    fun setMorningHour(v: Int) = updateSettings { it.copy(morningHour = v) }
    fun setEveningHour(v: Int) = updateSettings { it.copy(eveningHour = v) }

    fun setReminders(enabled: Boolean) {
        updateSettings { it.copy(remindersEnabled = enabled) }
        val ctx = getApplication<Application>()
        if (enabled) {
            val s = state.value
            ReminderScheduler.schedule(ctx, s.morningHour, s.eveningHour)
        } else {
            ReminderScheduler.cancel(ctx)
        }
    }

    /** Re-apply reminder scheduling after the user changes reminder times. */
    fun rescheduleReminders() {
        val s = state.value
        if (s.remindersEnabled) {
            ReminderScheduler.schedule(getApplication(), s.morningHour, s.eveningHour)
        }
    }

    private fun updateSettings(transform: (TasbihState) -> TasbihState) {
        viewModelScope.launch { repo.updateSettings(transform) }
    }

    fun allBadges(): List<Badge> = BadgeData.list
}
