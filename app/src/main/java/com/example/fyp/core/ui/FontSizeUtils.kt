package com.example.fyp.core

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf

const val MIN_SCALE = 0.8f
const val MAX_SCALE = 1.5f

fun validateScale(scale: Float): Float {
    return scale.coerceIn(MIN_SCALE, MAX_SCALE)
}

// CompositionLocal to provide font scale to entire app
val LocalFontSizeScale = compositionLocalOf { 1.0f }

/**
 * Creates a scaled typography based on the provided scale factor
 * @param baseTypography The original Material3 typography
 * @param scale Font size multiplier (0.8 to 1.5)
 * @return Scaled typography with all text styles multiplied by scale
 */
fun createScaledTypography(baseTypography: Typography, scale: Float): Typography {
    val validScale = validateScale(scale)

    return Typography(
        displayLarge = baseTypography.displayLarge.copy(
            fontSize = baseTypography.displayLarge.fontSize * validScale,
            lineHeight = baseTypography.displayLarge.lineHeight * validScale
        ),
        displayMedium = baseTypography.displayMedium.copy(
            fontSize = baseTypography.displayMedium.fontSize * validScale,
            lineHeight = baseTypography.displayMedium.lineHeight * validScale
        ),
        displaySmall = baseTypography.displaySmall.copy(
            fontSize = baseTypography.displaySmall.fontSize * validScale,
            lineHeight = baseTypography.displaySmall.lineHeight * validScale
        ),

        headlineLarge = baseTypography.headlineLarge.copy(
            fontSize = baseTypography.headlineLarge.fontSize * validScale,
            lineHeight = baseTypography.headlineLarge.lineHeight * validScale
        ),
        headlineMedium = baseTypography.headlineMedium.copy(
            fontSize = baseTypography.headlineMedium.fontSize * validScale,
            lineHeight = baseTypography.headlineMedium.lineHeight * validScale
        ),
        headlineSmall = baseTypography.headlineSmall.copy(
            fontSize = baseTypography.headlineSmall.fontSize * validScale,
            lineHeight = baseTypography.headlineSmall.lineHeight * validScale
        ),

        titleLarge = baseTypography.titleLarge.copy(
            fontSize = baseTypography.titleLarge.fontSize * validScale,
            lineHeight = baseTypography.titleLarge.lineHeight * validScale
        ),
        titleMedium = baseTypography.titleMedium.copy(
            fontSize = baseTypography.titleMedium.fontSize * validScale,
            lineHeight = baseTypography.titleMedium.lineHeight * validScale
        ),
        titleSmall = baseTypography.titleSmall.copy(
            fontSize = baseTypography.titleSmall.fontSize * validScale,
            lineHeight = baseTypography.titleSmall.lineHeight * validScale
        ),

        bodyLarge = baseTypography.bodyLarge.copy(
            fontSize = baseTypography.bodyLarge.fontSize * validScale,
            lineHeight = baseTypography.bodyLarge.lineHeight * validScale
        ),
        bodyMedium = baseTypography.bodyMedium.copy(
            fontSize = baseTypography.bodyMedium.fontSize * validScale,
            lineHeight = baseTypography.bodyMedium.lineHeight * validScale
        ),
        bodySmall = baseTypography.bodySmall.copy(
            fontSize = baseTypography.bodySmall.fontSize * validScale,
            lineHeight = baseTypography.bodySmall.lineHeight * validScale
        ),

        labelLarge = baseTypography.labelLarge.copy(
            fontSize = baseTypography.labelLarge.fontSize * validScale,
            lineHeight = baseTypography.labelLarge.lineHeight * validScale
        ),
        labelMedium = baseTypography.labelMedium.copy(
            fontSize = baseTypography.labelMedium.fontSize * validScale,
            lineHeight = baseTypography.labelMedium.lineHeight * validScale
        ),
        labelSmall = baseTypography.labelSmall.copy(
            fontSize = baseTypography.labelSmall.fontSize * validScale,
            lineHeight = baseTypography.labelSmall.lineHeight * validScale
        ),
    )
}