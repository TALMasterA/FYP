package com.example.fyp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun FYPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorPaletteId: String = "default",
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    typography: androidx.compose.material3.Typography = Typography,
    content: @Composable () -> Unit
) {
    val palette = getPaletteById(colorPaletteId)
    // Disable dynamic color if using a non-default palette
    val useDynamicColor = dynamicColor && colorPaletteId == "default"

    val lightPrimary = hexStringToColor(palette.lightPrimary)
    val lightSecondary = hexStringToColor(palette.lightSecondary)
    val lightTertiary = hexStringToColor(palette.lightTertiary)

    val darkPrimary = hexStringToColor(palette.darkPrimary)
    val darkSecondary = hexStringToColor(palette.darkSecondary)
    val darkTertiary = hexStringToColor(palette.darkTertiary)

    // Blend with background to create opaque containers (no translucent layer)
    val lightPrimaryContainer = lerp(BackgroundLight, lightPrimary, 0.15f)
    val lightSecondaryContainer = lerp(BackgroundLight, lightSecondary, 0.15f)
    val lightTertiaryContainer = lerp(BackgroundLight, lightTertiary, 0.15f)

    val darkPrimaryContainer = lerp(BackgroundDark, darkPrimary, 0.30f)
    val darkSecondaryContainer = lerp(BackgroundDark, darkSecondary, 0.30f)
    val darkTertiaryContainer = lerp(BackgroundDark, darkTertiary, 0.30f)

    val customDarkColorScheme = darkColorScheme(
        primary = darkPrimary,
        onPrimary = Color(0xFF003258),
        primaryContainer = darkPrimaryContainer,
        onPrimaryContainer = Color(0xFFD1E4FF),

        secondary = darkSecondary,
        onSecondary = Color(0xFF00363D),
        secondaryContainer = darkSecondaryContainer,
        onSecondaryContainer = Color(0xFFB2EBF2),

        tertiary = darkTertiary,
        onTertiary = Color(0xFF4A148C),
        tertiaryContainer = darkTertiaryContainer,
        onTertiaryContainer = Color(0xFFF3E5F5),

        error = Color(0xFFEF5350),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        background = BackgroundDark,
        onBackground = Color(0xFFE6E1E5),

        surface = SurfaceDark,
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = Color(0xFFC4C7C5),

        outline = Color(0xFF8E918F),
        outlineVariant = Color(0xFF44474E)
    )

    val customLightColorScheme = lightColorScheme(
        primary = lightPrimary,
        onPrimary = Color.White,
        primaryContainer = lightPrimaryContainer,
        onPrimaryContainer = Color(0xFF001D35),

        secondary = lightSecondary,
        onSecondary = Color.White,
        secondaryContainer = lightSecondaryContainer,
        onSecondaryContainer = Color(0xFF001F24),

        tertiary = lightTertiary,
        onTertiary = Color.White,
        tertiaryContainer = lightTertiaryContainer,
        onTertiaryContainer = Color(0xFF31005D),

        error = Error,
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),

        background = BackgroundLight,
        onBackground = Color(0xFF1A1C1E),

        surface = SurfaceLight,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = Color(0xFF42474E),

        outline = Color(0xFF72787E),
        outlineVariant = Color(0xFFC2C7CE)
    )

    val colorScheme = when {
        useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> customDarkColorScheme
        else -> customLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}