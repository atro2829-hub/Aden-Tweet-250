package com.adentweets.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AdenBlue,
    onPrimary = Color.White,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkOnSurface,
    secondary = DarkOnSurfaceVariant,
    onSecondary = DarkOnSurface,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkOnSurface,
    tertiary = LikeRed,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkDivider,
    outlineVariant = DarkDivider,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0x22FF4444),
    onErrorContainer = DarkOnSurface,
    inverseSurface = DarkSurfaceElevated,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = AdenBlue,
    surfaceTint = Color.Transparent
)

private val LightColorScheme = lightColorScheme(
    primary = AdenBlue,
    onPrimary = Color.White,
    primaryContainer = LightSurface,
    onPrimaryContainer = LightOnSurface,
    secondary = LightOnSurfaceVariant,
    onSecondary = LightOnSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightOnSurface,
    tertiary = LikeRed,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceVariant = LightSurfaceVariant,
    outline = LightDivider,
    outlineVariant = LightDivider,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0x22FF4444),
    onErrorContainer = LightOnSurface,
    inverseSurface = LightSurfaceVariant,
    inverseOnSurface = LightOnSurface,
    inversePrimary = AdenBlue,
    surfaceTint = Color.Transparent
)

@Composable
fun AdenTweetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdenTweetTypography,
        content = content
    )
}