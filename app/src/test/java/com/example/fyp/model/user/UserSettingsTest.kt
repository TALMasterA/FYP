package com.example.fyp.model.user

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for UserSettings data model.
 *
 * Tests validation logic for:
 * - Language code format
 * - Font size scale range
 * - Theme mode values
 * - Color palette unlocking
 * - Voice settings per language
 * - History view limits and expansion
 */
class UserSettingsTest {

    // --- Default Values Tests ---

    @Test
    fun `default settings have expected values`() {
        val settings = UserSettings()

        assertEquals("en-US", settings.primaryLanguageCode)
        assertEquals(1.0f, settings.fontSizeScale, 0.01f)
        assertEquals("system", settings.themeMode)
        assertEquals("default", settings.colorPaletteId)
        assertEquals(listOf("default"), settings.unlockedPalettes)
        assertTrue(settings.voiceSettings.isEmpty())
        assertEquals(100, settings.historyViewLimit)
    }

    // --- Primary Language Tests ---

    @Test
    fun `primary language can be changed`() {
        val settings = UserSettings(primaryLanguageCode = "es-ES")

        assertEquals("es-ES", settings.primaryLanguageCode)
    }

    @Test
    fun `language code follows locale format`() {
        val settings = UserSettings(primaryLanguageCode = "fr-FR")

        assertTrue(settings.primaryLanguageCode.contains("-"))
        assertTrue(settings.primaryLanguageCode.length >= 5)
    }

    // --- Font Size Scale Tests ---

    @Test
    fun `font size scale can be increased`() {
        val settings = UserSettings(fontSizeScale = 1.5f)

        assertEquals(1.5f, settings.fontSizeScale, 0.01f)
        assertTrue(settings.fontSizeScale > 1.0f)
    }

    @Test
    fun `font size scale can be decreased`() {
        val settings = UserSettings(fontSizeScale = 0.8f)

        assertEquals(0.8f, settings.fontSizeScale, 0.01f)
        assertTrue(settings.fontSizeScale < 1.0f)
    }

    @Test
    fun `font size scale accepts typical accessibility values`() {
        val scales = listOf(0.8f, 1.0f, 1.2f, 1.5f, 2.0f)

        scales.forEach { scale ->
            val settings = UserSettings(fontSizeScale = scale)
            assertEquals(scale, settings.fontSizeScale, 0.01f)
        }
    }

    // --- Theme Mode Tests ---

    @Test
    fun `theme mode supports system setting`() {
        val settings = UserSettings(themeMode = "system")
        assertEquals("system", settings.themeMode)
    }

    @Test
    fun `theme mode supports light mode`() {
        val settings = UserSettings(themeMode = "light")
        assertEquals("light", settings.themeMode)
    }

    @Test
    fun `theme mode supports dark mode`() {
        val settings = UserSettings(themeMode = "dark")
        assertEquals("dark", settings.themeMode)
    }

    @Test
    fun `valid theme modes are system light or dark`() {
        val validModes = listOf("system", "light", "dark")

        validModes.forEach { mode ->
            val settings = UserSettings(themeMode = mode)
            assertTrue(validModes.contains(settings.themeMode))
        }
    }

    // --- Color Palette Tests ---

    @Test
    fun `default palette is always unlocked`() {
        val settings = UserSettings()

        assertTrue(settings.unlockedPalettes.contains("default"))
        assertEquals("default", settings.colorPaletteId)
    }

    @Test
    fun `additional palettes can be unlocked`() {
        val settings = UserSettings(
            unlockedPalettes = listOf("default", "ocean", "sunset")
        )

        assertEquals(3, settings.unlockedPalettes.size)
        assertTrue(settings.unlockedPalettes.contains("ocean"))
        assertTrue(settings.unlockedPalettes.contains("sunset"))
    }

    @Test
    fun `current palette must be in unlocked list`() {
        val settings = UserSettings(
            colorPaletteId = "ocean",
            unlockedPalettes = listOf("default", "ocean", "sunset")
        )

        assertTrue(settings.unlockedPalettes.contains(settings.colorPaletteId))
    }

    @Test
    fun `user can switch between unlocked palettes`() {
        val unlockedPalettes = listOf("default", "ocean", "sunset", "forest")

        unlockedPalettes.forEach { paletteId ->
            val settings = UserSettings(
                colorPaletteId = paletteId,
                unlockedPalettes = unlockedPalettes
            )

            assertEquals(paletteId, settings.colorPaletteId)
            assertTrue(settings.unlockedPalettes.contains(paletteId))
        }
    }

    // --- Voice Settings Tests ---

    @Test
    fun `voice settings can be configured per language`() {
        val settings = UserSettings(
            voiceSettings = mapOf(
                "en-US" to "en-US-JennyNeural",
                "es-ES" to "es-ES-ElviraNeural",
                "fr-FR" to "fr-FR-DeniseNeural"
            )
        )

        assertEquals(3, settings.voiceSettings.size)
        assertEquals("en-US-JennyNeural", settings.voiceSettings["en-US"])
        assertEquals("es-ES-ElviraNeural", settings.voiceSettings["es-ES"])
    }

    @Test
    fun `voice settings start empty by default`() {
        val settings = UserSettings()

        assertTrue(settings.voiceSettings.isEmpty())
    }

    @Test
    fun `voice settings can be added for new language`() {
        val settings = UserSettings(
            voiceSettings = mapOf("en-US" to "en-US-GuyNeural")
        )

        assertTrue(settings.voiceSettings.containsKey("en-US"))
        assertEquals("en-US-GuyNeural", settings.voiceSettings["en-US"])
    }

    // --- History View Limit Tests ---

    @Test
    fun `history view limit has default value of 100`() {
        val settings = UserSettings()

        assertEquals(100, settings.historyViewLimit)
    }

    @Test
    fun `history view limit can be expanded`() {
        val settings = UserSettings(historyViewLimit = 150)

        assertEquals(150, settings.historyViewLimit)
        assertTrue(settings.historyViewLimit > 100)
    }

    @Test
    fun `history limit constants are defined`() {
        assertEquals(100, UserSettings.BASE_HISTORY_LIMIT)
        assertEquals(150, UserSettings.MAX_HISTORY_LIMIT)
        assertEquals(1000, UserSettings.HISTORY_EXPANSION_COST)
        assertEquals(10, UserSettings.HISTORY_EXPANSION_INCREMENT)
    }

    @Test
    fun `history limit expansion follows increment pattern`() {
        val limits = listOf(100, 110, 120, 130, 140, 150)

        limits.forEach { limit ->
            val settings = UserSettings(historyViewLimit = limit)
            assertEquals(limit, settings.historyViewLimit)
        }
    }

    @Test
    fun `history limit should not exceed maximum`() {
        val settings = UserSettings(historyViewLimit = 150)

        assertTrue(settings.historyViewLimit <= UserSettings.MAX_HISTORY_LIMIT)
    }

    // --- Edge Cases ---

    @Test
    fun `settings can have multiple simultaneous customizations`() {
        val settings = UserSettings(
            primaryLanguageCode = "ja-JP",
            fontSizeScale = 1.8f,
            themeMode = "dark",
            colorPaletteId = "ocean",
            unlockedPalettes = listOf("default", "ocean", "sunset"),
            voiceSettings = mapOf("ja-JP" to "ja-JP-NanamiNeural"),
            historyViewLimit = 100
        )

        assertEquals("ja-JP", settings.primaryLanguageCode)
        assertEquals(1.8f, settings.fontSizeScale, 0.01f)
        assertEquals("dark", settings.themeMode)
        assertEquals("ocean", settings.colorPaletteId)
        assertEquals(100, settings.historyViewLimit)
        assertTrue(settings.voiceSettings.containsKey("ja-JP"))
    }

    // --- Scenario Tests ---

    @Test
    fun `typical user progression unlocking palettes`() {
        // New user with defaults
        val initialSettings = UserSettings()
        assertEquals(listOf("default"), initialSettings.unlockedPalettes)

        // User unlocks first premium palette
        val afterFirstUnlock = UserSettings(
            unlockedPalettes = listOf("default", "ocean")
        )
        assertEquals(2, afterFirstUnlock.unlockedPalettes.size)

        // User unlocks second premium palette
        val afterSecondUnlock = UserSettings(
            unlockedPalettes = listOf("default", "ocean", "sunset")
        )
        assertEquals(3, afterSecondUnlock.unlockedPalettes.size)
    }

    @Test
    fun `user expanding history limit step by step`() {
        val progression = listOf(100, 110, 120, 130, 140, 150)

        progression.forEachIndexed { index, limit ->
            val settings = UserSettings(historyViewLimit = limit)
            assertEquals(limit, settings.historyViewLimit)

            if (index > 0) {
                val previousLimit = progression[index - 1]
                assertEquals(
                    UserSettings.HISTORY_EXPANSION_INCREMENT,
                    limit - previousLimit
                )
            }
        }
    }

    @Test
    fun `multilingual user configures voices for each language`() {
        val settings = UserSettings(
            voiceSettings = mapOf(
                "en-US" to "en-US-JennyNeural",
                "es-ES" to "es-ES-AlvaroNeural",
                "fr-FR" to "fr-FR-HenriNeural",
                "de-DE" to "de-DE-KatjaNeural",
                "ja-JP" to "ja-JP-KeitaNeural"
            )
        )

        assertEquals(5, settings.voiceSettings.size)

        listOf("en-US", "es-ES", "fr-FR", "de-DE", "ja-JP").forEach { lang ->
            assertTrue(settings.voiceSettings.containsKey(lang))
            assertNotNull(settings.voiceSettings[lang])
        }
    }
}