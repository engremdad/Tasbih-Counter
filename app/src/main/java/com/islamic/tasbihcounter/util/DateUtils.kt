package com.islamic.tasbihcounter.util

import com.islamic.tasbihcounter.data.model.DailyRecord
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateUtils {
    private val fmt: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun today(): String = LocalDate.now().format(fmt)

    /**
     * Current consecutive-day streak ending today (or yesterday). Any day with a
     * positive count keeps the streak alive; a gap breaks it.
     */
    fun currentStreak(history: List<DailyRecord>): Int {
        val days = history.filter { it.count > 0 }.map { LocalDate.parse(it.date) }.toSortedSet()
        if (days.isEmpty()) return 0
        val today = LocalDate.now()
        // A streak counts only if the most recent active day is today or yesterday.
        var cursor = when {
            days.contains(today) -> today
            days.contains(today.minusDays(1)) -> today.minusDays(1)
            else -> return 0
        }
        var streak = 0
        while (days.contains(cursor)) {
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    /** Longest streak ever recorded in history. */
    fun longestStreak(history: List<DailyRecord>): Int {
        val days = history.filter { it.count > 0 }.map { LocalDate.parse(it.date) }.toSortedSet().toList()
        if (days.isEmpty()) return 0
        var best = 1
        var run = 1
        for (i in 1 until days.size) {
            if (days[i - 1].plusDays(1) == days[i]) run++ else run = 1
            if (run > best) best = run
        }
        return best
    }
}
