package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PolishPrimary,
    secondary = PolishAccentOrange,
    tertiary = PolishSecondary,
    background = Color(0xFF151815),
    surface = Color(0xFF1E221E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PolishBackground,
    onSurface = PolishBackground
)

private val LightColorScheme = lightColorScheme(
    primary = PolishPrimary,
    secondary = PolishAccentOrange,
    tertiary = PolishSecondary,
    background = PolishBackground,
    surface = PolishSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PolishOnSurface,
    onSurface = PolishOnSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors to keep our organic Fruit Shop theme dominant
    content: @Composable () -> Unit,
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
