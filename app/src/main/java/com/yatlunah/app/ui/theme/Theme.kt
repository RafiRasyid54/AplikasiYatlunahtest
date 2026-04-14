package com.yatlunah.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// rafirasyid54/aplikasiyatlunahtest/.../ui/theme/Theme.kt

// rafirasyid54/aplikasiyatlunahtest/.../ui/theme/Theme.kt

private val DarkColorScheme = darkColorScheme(
    primary = BrightGreen,
    onPrimary = Color.Black, // Teks hitam di atas tombol hijau agar terbaca
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextDark,
    onSurface = TextDark,
    // Slot yang sering digunakan untuk NavigationBar/TopAppBar
    secondaryContainer = BrightGreen,
    onSecondaryContainer = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = BrightGreen,
    onPrimary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextLight,
    onSurface = TextLight,
    secondaryContainer = Color.White,
    onSecondaryContainer = Color.Black
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