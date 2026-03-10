package com.example.fyp.screens.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LearningModels (LanguageClusterUi data class).
 */
class LearningModelsTest {

    @Test
    fun `LanguageClusterUi stores languageCode and count`() {
        val cluster = LanguageClusterUi(languageCode = "ja-JP", count = 42)
        assertEquals("ja-JP", cluster.languageCode)
        assertEquals(42, cluster.count)
    }

    @Test
    fun `LanguageClusterUi equality based on all fields`() {
        val a = LanguageClusterUi("en-US", 10)
        val b = LanguageClusterUi("en-US", 10)
        assertEquals(a, b)
    }

    @Test
    fun `LanguageClusterUi inequality when count differs`() {
        val a = LanguageClusterUi("en-US", 10)
        val b = LanguageClusterUi("en-US", 20)
        assertNotEquals(a, b)
    }

    @Test
    fun `LanguageClusterUi inequality when languageCode differs`() {
        val a = LanguageClusterUi("en-US", 10)
        val b = LanguageClusterUi("ja-JP", 10)
        assertNotEquals(a, b)
    }

    @Test
    fun `LanguageClusterUi with zero count`() {
        val cluster = LanguageClusterUi("zh-HK", 0)
        assertEquals(0, cluster.count)
    }

    @Test
    fun `LanguageClusterUi copy updates count`() {
        val original = LanguageClusterUi("en-US", 5)
        val updated = original.copy(count = 15)
        assertEquals(15, updated.count)
        assertEquals("en-US", updated.languageCode)
    }

    @Test
    fun `LanguageClusterUi can be sorted by count`() {
        val clusters = listOf(
            LanguageClusterUi("ja-JP", 5),
            LanguageClusterUi("en-US", 20),
            LanguageClusterUi("zh-TW", 10)
        ).sortedByDescending { it.count }

        assertEquals("en-US", clusters[0].languageCode)
        assertEquals("zh-TW", clusters[1].languageCode)
        assertEquals("ja-JP", clusters[2].languageCode)
    }
}

