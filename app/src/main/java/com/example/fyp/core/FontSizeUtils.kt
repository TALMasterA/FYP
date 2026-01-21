package com.example.fyp.core

import androidx.compose.ui.unit.sp

/**
 * Font size scaling utilities for Task 8 (Font Customization).
 * Supports scaling from 80% to 150% of base size.
 */
object FontSizeUtils {

    // Valid font scale range
    const val MIN_SCALE = 0.8f   // 80%
    const val MAX_SCALE = 1.5f   // 150%
    const val DEFAULT_SCALE = 1.0f // 100% (no scaling)

    /**
     * Validate and constrain font scale to valid range.
     */
    fun validateScale(scale: Float): Float {
        return when {
            scale < MIN_SCALE -> MIN_SCALE
            scale > MAX_SCALE -> MAX_SCALE
            else -> scale
        }
    }

    /**
     * Scale a font size by the given factor.
     * Example: scale(16, 1.2f) → 19 (16 * 1.2)
     */
    fun scale(baseFontSize: Int, scaleFactor: Float): Int {
        val validated = validateScale(scaleFactor)
        return (baseFontSize * validated).toInt()
    }

    /**
     * Get scaled font size with bounds.
     */
    fun getScaledSize(baseSizeInt: Int, scaleFactor: Float): Int {
        return scale(baseSizeInt, scaleFactor)
    }
}