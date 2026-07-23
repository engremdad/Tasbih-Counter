package com.islamic.tasbihcounter.data.model

import kotlinx.serialization.Serializable

/**
 * A dhikr phrase the user can count. [id] is stable so counts and settings can
 * reference it across sessions. Built-in presets use negative-free small ids and
 * are marked [isPreset]; user-added ones get generated ids.
 */
@Serializable
data class Dhikr(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val target: Int = 33,
    val isPreset: Boolean = false
)

/** One of the 99 Names of Allah. */
@Serializable
data class AsmaName(
    val number: Int,
    val arabic: String,
    val transliteration: String,
    val meaning: String
)

/** A supplication (dua) with Arabic, transliteration, translation and its source/benefit. */
@Serializable
data class Dua(
    val id: String,
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val category: String
)

/** Aggregate count recorded for a single calendar day (ISO yyyy-MM-dd). */
@Serializable
data class DailyRecord(
    val date: String,
    val count: Int
)

/** Visual theme styles the user can pick in settings. */
enum class ThemeStyle(val displayName: String) {
    EMERALD("Emerald"),
    MIDNIGHT("Midnight Blue"),
    CALLIGRAPHY("Calligraphy Gold"),
    PLATE("Islamic Plate"),
    ROSE("Rose Quartz")
}

/** Whether the app follows the system, or forces light/dark. */
enum class ThemeMode(val displayName: String) {
    SYSTEM("Follow system"),
    LIGHT("Light"),
    DARK("Dark")
}

/** A milestone the user can unlock through consistent dhikr. */
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val requirement: Int,
    val kind: BadgeKind
)

enum class BadgeKind { STREAK, LIFETIME_COUNT, DAILY_GOAL }
