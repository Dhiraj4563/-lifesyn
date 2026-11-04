package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = TextPrimaryDark,
    primaryContainer = PurpleSecondary,
    onPrimaryContainer = TextPrimaryDark,
    secondary = BlueAccent,
    onSecondary = TextPrimaryDark,
    secondaryContainer = NavyCardElevated,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = GreenAccent,
    onTertiary = TextPrimaryDark,
    background = NavyBackground,
    onBackground = TextPrimaryDark,
    surface = NavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = NavyCardElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = NavyBorder,
    outlineVariant = NavyBorderSubtle,
    error = RedAccent,
    onError = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleSecondary,
    onPrimary = LightSurface,
    primaryContainer = PurpleAccent,
    onPrimaryContainer = TextPrimaryLight,
    secondary = BlueAccent,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceElevated,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = GreenAccent,
    onTertiary = LightSurface,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = RedAccent,
    onError = LightSurface
)

@Composable
fun LifeSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
