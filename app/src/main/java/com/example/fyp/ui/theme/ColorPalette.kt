package com.example.fyp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Represents a complete color palette for both light and dark themes
 */
data class ColorPalette(
    val id: String,
    val name: String,
    val cost: Int = 0, // 0 = free (default), 10+ = coin cost
    val lightPrimary: String,
    val lightSecondary: String,
    val lightTertiary: String,
    val darkPrimary: String,
    val darkSecondary: String,
    val darkTertiary: String,
)

// Predefined color palettes
val DEFAULT_PALETTE = ColorPalette(
    id = "default",
    name = "Sky Blue",
    cost = 0,
    lightPrimary = "FF2196F3",
    lightSecondary = "FF00BCD4",
    lightTertiary = "FF9C27B0",
    darkPrimary = "FF90CAF9",
    darkSecondary = "FF80DEEA",
    darkTertiary = "FFCE93D8"
)

val OCEAN_PALETTE = ColorPalette(
    id = "ocean",
    name = "Ocean Green",
    cost = 10,
    lightPrimary = "FF1B5E20",
    lightSecondary = "FF00897B",
    lightTertiary = "FF0277BD",
    darkPrimary = "FF66BB6A",
    darkSecondary = "FF4DB6AC",
    darkTertiary = "FF64B5F6"
)

val SUNSET_PALETTE = ColorPalette(
    id = "sunset",
    name = "Sunset Orange",
    cost = 10,
    lightPrimary = "FFFF6F00",
    lightSecondary = "FFFF5722",
    lightTertiary = "FFFF9800",
    darkPrimary = "FFFFB74D",
    darkSecondary = "FFFF7043",
    darkTertiary = "FFFFB74D"
)

val LAVENDER_PALETTE = ColorPalette(
    id = "lavender",
    name = "Lavender Purple",
    cost = 10,
    lightPrimary = "FF6A1B9A",
    lightSecondary = "FF8E24AA",
    lightTertiary = "FF7B1FA2",
    darkPrimary = "FFCE93D8",
    darkSecondary = "FFBA68C8",
    darkTertiary = "FFCE93D8"
)

val ROSE_PALETTE = ColorPalette(
    id = "rose",
    name = "Rose Pink",
    cost = 10,
    lightPrimary = "FFC2185B",
    lightSecondary = "FFE91E63",
    lightTertiary = "FF880E4F",
    darkPrimary = "FFEF5350",
    darkSecondary = "FFEC407A",
    darkTertiary = "FFF48FB1"
)

val MINT_PALETTE = ColorPalette(
    id = "mint",
    name = "Mint Fresh",
    cost = 10,
    lightPrimary = "FF00695C",
    lightSecondary = "FF26A69A",
    lightTertiary = "FF00897B",
    darkPrimary = "FF4DB6AC",
    darkSecondary = "FF80CBC4",
    darkTertiary = "FF4DB6AC"
)

val CRIMSON_PALETTE = ColorPalette(
    id = "crimson",
    name = "Crimson Red",
    cost = 10,
    lightPrimary = "FFB71C1C",
    lightSecondary = "FFD32F2F",
    lightTertiary = "FFC62828",
    darkPrimary = "FFEF5350",
    darkSecondary = "FFE57373",
    darkTertiary = "FFEF9A9A"
)

val AMBER_PALETTE = ColorPalette(
    id = "amber",
    name = "Amber Gold",
    cost = 10,
    lightPrimary = "FFFF6F00",
    lightSecondary = "FFFFA000",
    lightTertiary = "FFFF8F00",
    darkPrimary = "FFFFCA28",
    darkSecondary = "FFFFD54F",
    darkTertiary = "FFFFE082"
)

val INDIGO_PALETTE = ColorPalette(
    id = "indigo",
    name = "Indigo Night",
    cost = 10,
    lightPrimary = "FF283593",
    lightSecondary = "FF3F51B5",
    lightTertiary = "FF303F9F",
    darkPrimary = "FF7986CB",
    darkSecondary = "FF9FA8DA",
    darkTertiary = "FFC5CAE9"
)

val EMERALD_PALETTE = ColorPalette(
    id = "emerald",
    name = "Emerald Forest",
    cost = 10,
    lightPrimary = "FF1B5E20",
    lightSecondary = "FF388E3C",
    lightTertiary = "FF2E7D32",
    darkPrimary = "FF66BB6A",
    darkSecondary = "FF81C784",
    darkTertiary = "FFA5D6A7"
)

val CORAL_PALETTE = ColorPalette(
    id = "coral",
    name = "Coral Reef",
    cost = 10,
    lightPrimary = "FFFF6E40",
    lightSecondary = "FFFF7043",
    lightTertiary = "FFFF5722",
    darkPrimary = "FFFF8A65",
    darkSecondary = "FFFFAB91",
    darkTertiary = "FFFF9E80"
)

// List of all available palettes
val ALL_PALETTES = listOf(
    DEFAULT_PALETTE,
    OCEAN_PALETTE,
    SUNSET_PALETTE,
    LAVENDER_PALETTE,
    ROSE_PALETTE,
    MINT_PALETTE,
    CRIMSON_PALETTE,
    AMBER_PALETTE,
    INDIGO_PALETTE,
    EMERALD_PALETTE,
    CORAL_PALETTE
)

// Convert hex string to Color
fun hexStringToColor(hex: String): Color = Color(0xFF000000 or hex.substring(2).toLong(16))

// Get palette by ID
fun getPaletteById(id: String): ColorPalette = ALL_PALETTES.find { it.id == id } ?: DEFAULT_PALETTE
