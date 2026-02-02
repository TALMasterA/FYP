package com.example.fyp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color(0xFF003258),
    primaryContainer = PrimaryVariantDark,
    onPrimaryContainer = Color(0xFFD1E4FF),

    secondary = SecondaryDark,
    onSecondary = Color(0xFF00363D),
    secondaryContainer = SecondaryVariantDark,
    onSecondaryContainer = Color(0xFFB2EBF2),

    tertiary = TertiaryDark,
    onTertiary = Color(0xFF4A148C),
    tertiaryContainer = TertiaryVariantDark,
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

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D35),

    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF001F24),

    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF3E5F5),
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

@Composable
fun FYPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    typography: androidx.compose.material3.Typography = Typography,
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
        typography = typography,
        content = content
    )
}