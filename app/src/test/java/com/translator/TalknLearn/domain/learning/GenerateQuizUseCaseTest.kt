package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.TranslationRecord
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Fake QuizGenerationRepository for testing without Mockito.
 */
private class FakeQuizGenerationRepository : QuizGenerationRepository {
    var lastDeployment: String? = null
    var lastPrimaryCode: String? = null
    var lastTargetCode: String? = null
    var lastRecords: List<TranslationRecord>? = null
    var lastLearningMaterial: String? = null
    var resultToReturn: String = ""
    var exceptionToThrow: Exception? = null

    override suspend fun generateQuiz(
        deployment: String,
        primaryLanguageCode: String,
        targetLanguageCode: String,
        records: List<TranslationRecord>,
        learningMaterial: String
    ): String {
        exceptionToThrow?.let { throw it }
        lastDeployment = deployment
        lastPrimaryCode = primaryLanguageCode
        lastTargetCode = targetLanguageCode
        lastRecords = records
        lastLearningMaterial = learningMaterial
        return resultToReturn
    }
}

/**
 * Unit tests for GenerateQuizUseCase.
 *
 * Tests:
 * 1. Delegates to repository with correct parameters
 * 2. Returns raw quiz content string
 * 3. Propagates repository exceptions
 */
class GenerateQuizUseCaseTest {

    private lateinit var repo: FakeQuizGenerationRepository
    private lateinit var useCase: GenerateQuizUseCase

    @Before
    fun setup() {
        repo = FakeQuizGenerationRepository()
        useCase = GenerateQuizUseCase(repo)
    }

    @Test
    fun `invoke delegates to repository with correct parameters`() = runTest {
        val records = listOf(TranslationRecord(sourceText = "hello", targetText = "hola"))
        val learningMaterial = "Some learning content"
        repo.resultToReturn = "{\"questions\": []}"

        val result = useCase("gpt-4o", "en-US", "es-ES", records, learningMaterial)

        assertEquals("{\"questions\": []}", result)
        assertEquals("gpt-4o", repo.lastDeployment)
        assertEquals("en-US", repo.lastPrimaryCode)
        assertEquals("es-ES", repo.lastTargetCode)
        assertEquals(records, repo.lastRecords)
        assertEquals(learningMaterial, repo.lastLearningMaterial)
    }

    @Test
    fun `invoke returns content from repository`() = runTest {
        repo.resultToReturn = "[{\"question\":\"What is hello?\"}]"

        val result = useCase("gpt-4o", "en-US", "ja-JP", emptyList(), "material")

        assertTrue(result.contains("question"))
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates repository exception`() = runTest {
        repo.exceptionToThrow = RuntimeException("Generation failed")

        useCase("gpt-4o", "en-US", "es-ES", emptyList(), "material")
    }
}
