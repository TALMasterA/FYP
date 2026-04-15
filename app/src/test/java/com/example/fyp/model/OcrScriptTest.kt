package com.example.fyp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for OcrScript enum.
 * Verifies language code to script mapping and script properties.
 */
class OcrScriptTest {

    @Test
    fun `all scripts have positive estimated size`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have positive size", script.estimatedSizeMb > 0)
        }
    }

    @Test
    fun `all scripts have non-empty display names`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have non-empty display name", script.displayName.isNotBlank())
        }
    }

    @Test
    fun `all scripts have non-empty language prefixes`() {
        OcrScript.entries.forEach { script ->
            assertTrue("${script.name} should have language prefixes", script.languagePrefixes.isNotEmpty())
        }
    }

    @Test
    fun `there are exactly 4 OCR scripts`() {
        assertEquals(4, OcrScript.entries.size)
    }

    @Test
    fun `total estimated size is reasonable`() {
        val totalSize = OcrScript.entries.sumOf { it.estimatedSizeMb }
        // Expected: Latin (5) + Chinese (10) + Japanese (10) + Korean (8) = 33MB
        assertEquals(33, totalSize)
    }

    @Test
    fun `LATIN script covers most languages`() {
        assertTrue(OcrScript.LATIN.languagePrefixes.size > OcrScript.CHINESE.languagePrefixes.size)
    }
}
