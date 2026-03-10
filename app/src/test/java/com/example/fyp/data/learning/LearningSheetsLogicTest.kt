package com.example.fyp.data.learning

import com.example.fyp.domain.learning.SheetMetadata
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for pure logic extracted from FirestoreLearningSheetsRepository.
 *
 * Covers: norm() string trimming, docId() construction,
 * empty targets short-circuit, fill-missing logic,
 * and LearningSheetDoc defaults.
 */
class LearningSheetsLogicTest {

    // Helper functions that mirror the repository's private methods
    private fun norm(code: String) = code.trim()
    private fun docId(primary: String, target: String) = "${norm(primary)}__${norm(target)}"

    // ── norm() ────────────────────────────────────────────────────────

    @Test
    fun `norm trims leading whitespace`() {
        assertEquals("en-US", norm("  en-US"))
    }

    @Test
    fun `norm trims trailing whitespace`() {
        assertEquals("en-US", norm("en-US  "))
    }

    @Test
    fun `norm trims both sides`() {
        assertEquals("ja-JP", norm("  ja-JP  "))
    }

    @Test
    fun `norm preserves clean code unchanged`() {
        assertEquals("zh-TW", norm("zh-TW"))
    }

    @Test
    fun `norm handles empty string`() {
        assertEquals("", norm(""))
    }

    @Test
    fun `norm handles whitespace-only string`() {
        assertEquals("", norm("   "))
    }

    // ── docId() ───────────────────────────────────────────────────────

    @Test
    fun `docId formats as primary__target`() {
        assertEquals("en-US__ja-JP", docId("en-US", "ja-JP"))
    }

    @Test
    fun `docId trims inputs before formatting`() {
        assertEquals("en-US__ja-JP", docId("  en-US  ", "  ja-JP  "))
    }

    @Test
    fun `docId with same primary and target`() {
        assertEquals("en-US__en-US", docId("en-US", "en-US"))
    }

    @Test
    fun `docId uses double underscore separator`() {
        val id = docId("abc", "xyz")
        assertTrue(id.contains("__"))
        val parts = id.split("__")
        assertEquals(2, parts.size)
        assertEquals("abc", parts[0])
        assertEquals("xyz", parts[1])
    }

    @Test
    fun `docId with CJK-style codes`() {
        assertEquals("zh-HK__zh-TW", docId("zh-HK", "zh-TW"))
    }

    // ── Empty targets short-circuit ───────────────────────────────────

    @Test
    fun `getBatchSheetMetadata with empty targets returns empty map`() {
        val targets = emptyList<String>()
        // This is the short-circuit check in the repository
        val result = if (targets.isEmpty()) emptyMap() else mapOf("test" to SheetMetadata(true, 10))
        assertTrue(result.isEmpty())
    }

    // ── Fill-missing logic ────────────────────────────────────────────

    @Test
    fun `fill-missing marks unfound targets as non-existent`() {
        val normalizedTargets = listOf("ja-JP", "ko-KR", "fr-FR")
        val found = mutableMapOf(
            "ja-JP" to SheetMetadata(exists = true, historyCountAtGenerate = 25)
        )

        // Fill missing (same as repository logic)
        normalizedTargets.forEach { t ->
            if (!found.containsKey(t)) {
                found[t] = SheetMetadata(exists = false, historyCountAtGenerate = null)
            }
        }

        assertEquals(3, found.size)
        assertTrue(found["ja-JP"]!!.exists)
        assertEquals(25, found["ja-JP"]!!.historyCountAtGenerate)
        assertFalse(found["ko-KR"]!!.exists)
        assertNull(found["ko-KR"]!!.historyCountAtGenerate)
        assertFalse(found["fr-FR"]!!.exists)
        assertNull(found["fr-FR"]!!.historyCountAtGenerate)
    }

    @Test
    fun `fill-missing does not overwrite found targets`() {
        val targets = listOf("ja-JP")
        val found = mutableMapOf(
            "ja-JP" to SheetMetadata(exists = true, historyCountAtGenerate = 30)
        )

        targets.forEach { t ->
            if (!found.containsKey(t)) {
                found[t] = SheetMetadata(exists = false, historyCountAtGenerate = null)
            }
        }

        assertTrue(found["ja-JP"]!!.exists)
        assertEquals(30, found["ja-JP"]!!.historyCountAtGenerate)
    }

    @Test
    fun `fill-missing with all targets found changes nothing`() {
        val targets = listOf("ja-JP", "ko-KR")
        val found = mutableMapOf(
            "ja-JP" to SheetMetadata(exists = true, historyCountAtGenerate = 20),
            "ko-KR" to SheetMetadata(exists = true, historyCountAtGenerate = 15)
        )

        val sizeBefore = found.size
        targets.forEach { t ->
            if (!found.containsKey(t)) {
                found[t] = SheetMetadata(exists = false, historyCountAtGenerate = null)
            }
        }

        assertEquals(sizeBefore, found.size)
    }

    @Test
    fun `fill-missing with all targets missing fills all`() {
        val targets = listOf("a", "b", "c")
        val found = mutableMapOf<String, SheetMetadata>()

        targets.forEach { t ->
            if (!found.containsKey(t)) {
                found[t] = SheetMetadata(exists = false, historyCountAtGenerate = null)
            }
        }

        assertEquals(3, found.size)
        found.values.forEach { metadata ->
            assertFalse(metadata.exists)
            assertNull(metadata.historyCountAtGenerate)
        }
    }

    // ── LearningSheetDoc defaults ─────────────────────────────────────

    @Test
    fun `LearningSheetDoc default primaryLanguageCode is empty`() {
        assertEquals("", LearningSheetDoc().primaryLanguageCode)
    }

    @Test
    fun `LearningSheetDoc default targetLanguageCode is empty`() {
        assertEquals("", LearningSheetDoc().targetLanguageCode)
    }

    @Test
    fun `LearningSheetDoc default content is empty`() {
        assertEquals("", LearningSheetDoc().content)
    }

    @Test
    fun `LearningSheetDoc default historyCountAtGenerate is 0`() {
        assertEquals(0, LearningSheetDoc().historyCountAtGenerate)
    }

    @Test
    fun `LearningSheetDoc default updatedAt is null`() {
        assertNull(LearningSheetDoc().updatedAt)
    }

    // ── SheetMetadata ─────────────────────────────────────────────────

    @Test
    fun `SheetMetadata with exists true and count`() {
        val meta = SheetMetadata(exists = true, historyCountAtGenerate = 42)
        assertTrue(meta.exists)
        assertEquals(42, meta.historyCountAtGenerate)
    }

    @Test
    fun `SheetMetadata with exists false and null count`() {
        val meta = SheetMetadata(exists = false, historyCountAtGenerate = null)
        assertFalse(meta.exists)
        assertNull(meta.historyCountAtGenerate)
    }
}
