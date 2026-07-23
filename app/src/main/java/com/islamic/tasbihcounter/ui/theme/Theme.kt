package com.islamic.tasbihcounter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.islamic.tasbihcounter.data.model.ThemeMode
import com.islamic.tasbihcounter.data.model.ThemeStyle

private fun lightScheme(primary: androidx.compose.ui.graphics.Color, secondary: androidx.compose.ui.graphics.Color, tertiary: androidx.compose.ui.graphics.Color) =
    lightColorScheme(primary = primary, secondary = secondary, tertiary = tertiary)

private fun darkScheme(primary: androidx.compose.ui.graphics.Color, secondary: androidx.compose.ui.graphics.Color, tertiary: androidx.compose.ui.graphics.Color) =
    darkColorScheme(primary = primary, secondary = secondary, tertiary = tertiary)

private fun schemeFor(style: ThemeStyle, dark: Boolean): ColorScheme = when (style) {
    ThemeStyle.EMERALD -> if (dark) darkScheme(EmeraldDarkPrimary, EmeraldDarkSecondary, EmeraldDarkTertiary)
    else lightScheme(EmeraldPrimary, EmeraldSecondary, EmeraldTertiary)
    ThemeStyle.MIDNIGHT -> if (dark) darkScheme(MidnightDarkPrimary, MidnightDarkSecondary, MidnightDarkTertiary)
    else lightScheme(MidnightPrimary, MidnightSecondary, MidnightTertiary)
    ThemeStyle.CALLIGRAPHY -> if (dark) darkScheme(CalligraphyDarkPrimary, CalligraphyDarkSecondary, CalligraphyDarkTertiary)
    else lightScheme(CalligraphyPrimary, CalligraphySecondary, CalligraphyTertiary)
    ThemeStyle.PLATE -> if (dark) darkScheme(PlateDarkPrimary, PlateDarkSecondary, PlateDarkTertiary)
    else lightScheme(PlatePrimary, PlateSecondary, PlateTertiary)
    ThemeStyle.ROSE -> if (dark) darkScheme(RoseDarkPrimary, RoseDarkSecondary, RoseDarkTertiary)
    else lightScheme(RosePrimary, RoseSecondary, RoseTertiary)
}

@Composable
fun TasbihCounterTheme(
    themeStyle: ThemeStyle = ThemeStyle.EMERALD,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> schemeFor(themeStyle, dark)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
