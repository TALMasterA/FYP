package com.translator.TalknLearn.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class ColorPaletteTest {

    // ── hexStringToColor ────────────────────────────────────────────

    @Test
    fun `hexStringToColor - converts valid hex to Color`() {
        // FF2196F3 → alpha=0xFF, R=0x21, G=0x96, B=0xF3
        val color = hexStringToColor("FF2196F3")
        assertEquals(Color(0xFF2196F3), color)
    }

    @Test
    fun `hexStringToColor - black color`() {
        val color = hexStringToColor("FF000000")
        assertEquals(Color(0xFF000000), color)
    }

    @Test
    fun `hexStringToColor - white color`() {
        val color = hexStringToColor("FFFFFFFF")
        assertEquals(Color(0xFFFFFFFF), color)
    }

    @Test
    fun `hexStringToColor - red color`() {
        val color = hexStringToColor("FFFF0000")
        assertEquals(Color(0xFFFF0000), color)
    }

    // ── getPaletteById ──────────────────────────────────────────────

    @Test
    fun `getPaletteById - returns default palette for default id`() {
        val palette = getPaletteById("default")
        assertEquals(DEFAULT_PALETTE, palette)
    }

    @Test
    fun `getPaletteById - returns ocean palette`() {
        val palette = getPaletteById("ocean")
        assertEquals(OCEAN_PALETTE, palette)
    }

    @Test
    fun `getPaletteById - returns sunset palette`() {
        val palette = getPaletteById("sunset")
        assertEquals(SUNSET_PALETTE, palette)
    }

    @Test
    fun `getPaletteById - unknown id falls back to DEFAULT_PALETTE`() {
        val palette = getPaletteById("nonexistent")
        assertEquals(DEFAULT_PALETTE, palette)
    }

    @Test
    fun `getPaletteById - empty id falls back to DEFAULT_PALETTE`() {
        val palette = getPaletteById("")
        assertEquals(DEFAULT_PALETTE, palette)
    }

    @Test
    fun `getPaletteById - all known palette ids resolve correctly`() {
        for (palette in ALL_PALETTES) {
            val found = getPaletteById(palette.id)
            assertEquals("Palette with id '${palette.id}' should resolve", palette, found)
        }
    }

    // ── ALL_PALETTES consistency ────────────────────────────────────

    @Test
    fun `ALL_PALETTES has correct count`() {
        assertEquals(11, ALL_PALETTES.size)
    }

    @Test
    fun `ALL_PALETTES has unique ids`() {
        val ids = ALL_PALETTES.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `ALL_PALETTES has unique names`() {
        val names = ALL_PALETTES.map { it.name }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `default palette is free (cost zero)`() {
        assertEquals(0, DEFAULT_PALETTE.cost)
    }

    @Test
    fun `non-default palettes cost 10 coins`() {
        for (palette in ALL_PALETTES.filter { it.id != "default" }) {
            assertEquals(
                "Palette '${palette.name}' should cost 10",
                10,
                palette.cost
            )
        }
    }

    @Test
    fun `all palette hex strings are 8 characters`() {
        for (palette in ALL_PALETTES) {
            assertEquals("lightPrimary of ${palette.id}", 8, palette.lightPrimary.length)
            assertEquals("lightSecondary of ${palette.id}", 8, palette.lightSecondary.length)
            assertEquals("lightTertiary of ${palette.id}", 8, palette.lightTertiary.length)
            assertEquals("darkPrimary of ${palette.id}", 8, palette.darkPrimary.length)
            assertEquals("darkSecondary of ${palette.id}", 8, palette.darkSecondary.length)
            assertEquals("darkTertiary of ${palette.id}", 8, palette.darkTertiary.length)
        }
    }

    @Test
    fun `all palette hex strings are valid hex`() {
        val hexRegex = Regex("^[0-9A-F]{8}$")
        for (palette in ALL_PALETTES) {
            listOf(
                palette.lightPrimary, palette.lightSecondary, palette.lightTertiary,
                palette.darkPrimary, palette.darkSecondary, palette.darkTertiary
            ).forEach { hex ->
                assertTrue(
                    "Hex '$hex' in palette '${palette.id}' should be valid",
                    hexRegex.matches(hex)
                )
            }
        }
    }
}
