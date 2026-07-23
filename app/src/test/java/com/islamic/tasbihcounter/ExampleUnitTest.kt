package com.islamic.tasbihcounter

import com.islamic.tasbihcounter.data.model.DailyRecord
import com.islamic.tasbihcounter.util.BanglaNumber
import com.islamic.tasbihcounter.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TasbihLogicTest {

    @Test
    fun banglaNumber_convertsDigits() {
        assertEquals("১২৩", BanglaNumber.format(123))
        assertEquals("২০২৬-০৭-২৩", BanglaNumber.format("2026-07-23"))
    }

    @Test
    fun currentStreak_countsConsecutiveDaysEndingToday() {
        val today = LocalDate.now()
        val history = listOf(
            DailyRecord(today.toString(), 33),
            DailyRecord(today.minusDays(1).toString(), 10),
            DailyRecord(today.minusDays(2).toString(), 5),
            // Gap on day 3, then an older active day that must not extend the streak.
            DailyRecord(today.minusDays(5).toString(), 8)
        )
        assertEquals(3, DateUtils.currentStreak(history))
    }

    @Test
    fun currentStreak_isZeroWhenLastActiveDayIsOld() {
        val today = LocalDate.now()
        val history = listOf(DailyRecord(today.minusDays(3).toString(), 12))
        assertEquals(0, DateUtils.currentStreak(history))
    }

    @Test
    fun longestStreak_findsBestRun() {
        val base = LocalDate.of(2026, 1, 1)
        val history = listOf(
            DailyRecord(base.toString(), 1),
            DailyRecord(base.plusDays(1).toString(), 1),
            DailyRecord(base.plusDays(2).toString(), 1),
            // gap, then a 2-day run
            DailyRecord(base.plusDays(5).toString(), 1),
            DailyRecord(base.plusDays(6).toString(), 1)
        )
        assertEquals(3, DateUtils.longestStreak(history))
    }
}
