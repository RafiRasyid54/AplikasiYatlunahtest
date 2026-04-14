package com.yatlunah.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrightGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextDark,
    onSurface = TextDark
)

private val LightColorScheme = lightColorScheme(
    primary = BrightGreen,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun AplikasiYatlunahtestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // MATIKAN dynamicColor agar tema hijau Yatlunah tidak ditimpa warna HP user!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}