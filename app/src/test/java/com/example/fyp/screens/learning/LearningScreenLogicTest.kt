package com.example.fyp.screens.learning

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for learning screen UI logic and state computations.
 *
 * Tests:
 * 1. Cluster display filtering logic
 * 2. Generation button state based on counts
 * 3. Regeneration eligibility UI calculations
 * 4. Sheet and quiz metadata state
 */
class LearningScreenLogicTest {

    // ── Cluster UI State ─────────────────────────────────────────────

    @Test
    fun `cluster list empty when no language counts`() {
        val clusters = buildMockClusters(emptyMap(), "en-US")
        assertTrue(clusters.isEmpty())
    }

    @Test
    fun `cluster excludes primary language when primary has records`() {
        val counts = mapOf("en-US" to 10, "ja" to 5, "zh-HK" to 3)
        val clusters = buildMockClusters(counts, "en-US")

        assertEquals(2, clusters.size)
        assertFalse(clusters.any { it.languageCode == "en-US" })
        assertTrue(clusters.any { it.languageCode == "ja" })
        assertTrue(clusters.any { it.languageCode == "zh-HK" })
    }

    @Test
    fun `cluster includes all languages when primary has no records`() {
        val counts = mapOf("en-US" to 0, "ja" to 5, "zh-HK" to 3)
        val clusters = buildMockClusters(counts, "en-US")

        // When primary has 0 records, all languages shown
        assertTrue(clusters.any { it.languageCode == "ja" })
        assertTrue(clusters.any { it.languageCode == "zh-HK" })
    }

    @Test
    fun `clusters sorted by count descending then by code`() {
        val counts = mapOf("ja" to 10, "zh-HK" to 10, "ko" to 5, "es" to 20)
        val clusters = buildMockClusters(counts, "en-US")

        assertEquals("es", clusters[0].languageCode)
        assertEquals(20, clusters[0].count)
        // ja and zh-HK both have 10, sorted alphabetically
        assertTrue(clusters[1].languageCode == "ja" || clusters[1].languageCode == "zh-HK")
        assertEquals("ko", clusters[3].languageCode)
    }

    @Test
    fun `clusters exclude blank language codes`() {
        val counts = mapOf("ja" to 10, "" to 5, "   " to 3)
        val clusters = buildMockClusters(counts, "en-US")

        assertEquals(1, clusters.size)
        assertEquals("ja", clusters[0].languageCode)
    }

    // ── Generation Button State ──────────────────────────────────────

    @Test
    fun `generate button disabled when count is zero`() {
        val canGenerate = computeCanGenerate(
            countNow = 0,
            lastCount = null,
            isGenerating = false
        )
        assertFalse(canGenerate)
    }

    @Test
    fun `generate button enabled for first generation`() {
        val canGenerate = computeCanGenerate(
            countNow = 25,
            lastCount = null,
            isGenerating = false
        )
        assertTrue(canGenerate)
    }

    @Test
    fun `generate button disabled when already generating`() {
        val canGenerate = computeCanGenerate(
            countNow = 25,
            lastCount = null,
            isGenerating = true
        )
        assertFalse(canGenerate)
    }

    @Test
    fun `generate button disabled when count unchanged`() {
        val canGenerate = computeCanGenerate(
            countNow = 25,
            lastCount = 25,
            isGenerating = false
        )
        assertFalse(canGenerate)
    }

    @Test
    fun `generate button enabled when enough new records added`() {
        // MIN_RECORDS_FOR_LEARNING_SHEET = 5
        val canGenerate = computeCanGenerate(
            countNow = 30,  // 5 more than lastCount
            lastCount = 25,
            isGenerating = false
        )
        assertTrue(canGenerate)
    }

    @Test
    fun `generate button disabled when fewer than threshold added`() {
        // MIN_RECORDS_FOR_LEARNING_SHEET = 5
        val canGenerate = computeCanGenerate(
            countNow = 29,  // only 4 more than lastCount
            lastCount = 25,
            isGenerating = false
        )
        assertFalse(canGenerate)
    }

    // ── Quiz Generation State ────────────────────────────────────────

    @Test
    fun `quiz generate enabled when no quiz exists`() {
        val canGenerateQuiz = computeCanGenerateQuiz(
            sheetHistoryCount = 25,
            quizHistoryCount = null,
            isGeneratingQuiz = false
        )
        assertTrue(canGenerateQuiz)
    }

    @Test
    fun `quiz generate disabled when sheet version equals quiz version`() {
        val canGenerateQuiz = computeCanGenerateQuiz(
            sheetHistoryCount = 25,
            quizHistoryCount = 25,
            isGeneratingQuiz = false
        )
        assertFalse(canGenerateQuiz)
    }

    @Test
    fun `quiz generate enabled when sheet version differs from quiz version`() {
        val canGenerateQuiz = computeCanGenerateQuiz(
            sheetHistoryCount = 30,
            quizHistoryCount = 25,
            isGeneratingQuiz = false
        )
        assertTrue(canGenerateQuiz)
    }

    @Test
    fun `quiz generate disabled when already generating`() {
        val canGenerateQuiz = computeCanGenerateQuiz(
            sheetHistoryCount = 30,
            quizHistoryCount = 25,
            isGeneratingQuiz = true
        )
        assertFalse(canGenerateQuiz)
    }

    // ── UI State Consistency ─────────────────────────────────────────

    @Test
    fun `ui state defaults are correct`() {
        val state = LearningUiState()

        assertTrue(state.isLoading)
        assertNull(state.error)
        assertEquals("en-US", state.primaryLanguageCode)
        assertTrue(state.records.isEmpty())
        assertTrue(state.clusters.isEmpty())
        assertNull(state.generatingLanguageCode)
        assertNull(state.generatingQuizLanguageCode)
        assertTrue(state.sheetExistsByLanguage.isEmpty())
        assertTrue(state.sheetCountByLanguage.isEmpty())
        assertTrue(state.quizCountByLanguage.isEmpty())
        assertNull(state.sheetGenerationCompleted)
        assertNull(state.quizGenerationCompleted)
    }

    @Test
    fun `ui state copy preserves other fields`() {
        val original = LearningUiState(
            isLoading = false,
            error = null,
            primaryLanguageCode = "zh-HK",
            clusters = listOf(LanguageClusterUi("ja", 10))
        )

        val updated = original.copy(generatingLanguageCode = "ja")

        assertFalse(updated.isLoading)
        assertEquals("zh-HK", updated.primaryLanguageCode)
        assertEquals(1, updated.clusters.size)
        assertEquals("ja", updated.generatingLanguageCode)
    }

    @Test
    fun `completion event is single-shot consumable`() {
        var state = LearningUiState(sheetGenerationCompleted = "ja")

        // Event is set
        assertEquals("ja", state.sheetGenerationCompleted)

        // After consumption, event is null
        state = state.copy(sheetGenerationCompleted = null)
        assertNull(state.sheetGenerationCompleted)
    }

    // ── Helper Functions (mirroring ViewModel logic) ─────────────────

    /**
     * Rebuild clusters from language counts, mimicking ViewModel logic.
     */
    private fun buildMockClusters(
        languageCounts: Map<String, Int>,
        primaryLanguageCode: String
    ): List<LanguageClusterUi> {
        val allLanguageCounts = languageCounts.toMutableMap()
        val primaryHasRecords = (allLanguageCounts[primaryLanguageCode] ?: 0) > 0

        val directionalCounts = if (primaryHasRecords) {
            allLanguageCounts.filterKeys { it != primaryLanguageCode }
        } else {
            allLanguageCounts
        }

        return directionalCounts
            .filter { (code, _) -> code.isNotBlank() }
            .map { (code, count) -> LanguageClusterUi(code, count) }
            .sortedWith(compareByDescending<LanguageClusterUi> { it.count }.thenBy { it.languageCode })
    }

    /**
     * Compute whether generation can proceed, mimicking ViewModel logic.
     */
    private fun computeCanGenerate(
        countNow: Int,
        lastCount: Int?,
        isGenerating: Boolean
    ): Boolean {
        if (countNow == 0) return false
        if (isGenerating) return false
        if (lastCount != null && lastCount == countNow) return false

        // First generation always allowed
        if (lastCount == null) return true

        // Anti-cheat: must have added MIN_RECORDS_FOR_LEARNING_SHEET
        val minRecords = 5 // GenerationEligibility.MIN_RECORDS_FOR_LEARNING_SHEET
        return (countNow - lastCount) >= minRecords
    }

    /**
     * Compute whether quiz generation can proceed, mimicking ViewModel logic.
     */
    private fun computeCanGenerateQuiz(
        sheetHistoryCount: Int,
        quizHistoryCount: Int?,
        isGeneratingQuiz: Boolean
    ): Boolean {
        if (isGeneratingQuiz) return false

        // First quiz is always allowed
        if (quizHistoryCount == null) return true

        // Quiz only allowed when sheet version changes
        return sheetHistoryCount != quizHistoryCount
    }
}
