package com.islamic.tasbihcounter.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

/**
 * "Mushaf" design tokens — a committed, illuminated-manuscript palette: midnight
 * emerald grounds, gold-leaf ornament, warm cream text. One dark world by design.
 */
object Mushaf {
    val Ink = Color(0xFF05130F)
    val Ink2 = Color(0xFF081D16)
    val Emerald = Color(0xFF0C3A2C)
    val Emerald2 = Color(0xFF0F4C3A)
    val Page = Color(0xFF0A2019)

    val Gold = Color(0xFFD4AF37)
    val GoldSoft = Color(0xFFE8CF7E)
    val GoldDim = Color(0xFF9C8235)

    val Cream = Color(0xFFF3EAD2)
    val CreamDim = Color(0xFFC9BD97)

    /** Gold hairline at ~24% alpha, used for gilded rules and frames. */
    val Line = Color(0x3DD4AF37)

    // Card / cell gradients and borders
    val CardTop = Color(0xFF123F30)
    val CardBottom = Color(0xFF0B2B21)
    val CellTop = Color(0xFF0E3125)
    val CellBottom = Color(0xFF0A241B)
    val CellBorder = Color(0xFF17392C)
    val CellHiTop = Color(0xFF1A5541)
    val CellHiBottom = Color(0xFF0D3325)
    val SectionBg = Color(0xFF0B271E)
    val SectionBorder = Color(0xFF163A2C)
    val SearchBg = Color(0xFF0C2A20)
    val SearchBorder = Color(0xFF1E4636)

    /** Material scheme mapped onto the Mushaf palette. */
    val ColorScheme = darkColorScheme(
        primary = Gold,
        onPrimary = Color(0xFF231A05),
        primaryContainer = Emerald2,
        onPrimaryContainer = Cream,
        secondary = GoldSoft,
        onSecondary = Color(0xFF231A05),
        secondaryContainer = Emerald,
        onSecondaryContainer = Cream,
        tertiary = GoldSoft,
        onTertiary = Color(0xFF231A05),
        tertiaryContainer = Emerald2,
        onTertiaryContainer = Cream,
        background = Ink,
        onBackground = Cream,
        surface = Page,
        onSurface = Cream,
        surfaceVariant = Emerald,
        onSurfaceVariant = CreamDim,
        outline = GoldDim,
        outlineVariant = SectionBorder,
        error = Color(0xFFE0A3A3),
        onError = Color(0xFF231A05)
    )
}
