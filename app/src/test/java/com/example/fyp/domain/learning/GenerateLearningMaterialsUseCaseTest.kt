package com.example.fyp.domain.learning

import com.example.fyp.model.DeploymentName
import com.example.fyp.model.LanguageCode
import com.example.fyp.model.TranslationRecord
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Fake LearningContentRepository for testing without Mockito.
 * Mockito cannot handle @JvmInline value class parameters with any() matchers.
 */
private class FakeLearningContentRepository : LearningContentRepository {
    var lastDeployment: DeploymentName? = null
    var lastPrimaryCode: LanguageCode? = null
    var lastTargetCode: LanguageCode? = null
    var lastRecords: List<TranslationRecord>? = null
    var resultToReturn: String = ""
    var exceptionToThrow: Exception? = null

    override suspend fun generateForLanguage(
        deployment: DeploymentName,
        primaryLanguageCode: LanguageCode,
        targetLanguageCode: LanguageCode,
        records: List<TranslationRecord>
    ): String {
        exceptionToThrow?.let { throw it }
        lastDeployment = deployment
        lastPrimaryCode = primaryLanguageCode
        lastTargetCode = targetLanguageCode
        lastRecords = records
        return resultToReturn
    }
}

/**
 * Unit tests for GenerateLearningMaterialsUseCase.
 *
 * Tests:
 * 1. Delegates to repository with correct parameters (wrapped as value types)
 * 2. Returns generated content string
 * 3. Propagates repository exceptions
 */
class GenerateLearningMaterialsUseCaseTest {

    private lateinit var repo: FakeLearningContentRepository
    private lateinit var useCase: GenerateLearningMaterialsUseCase

    @Before
    fun setup() {
        repo = FakeLearningContentRepository()
        useCase = GenerateLearningMaterialsUseCase(repo)
    }

    @Test
    fun `invoke delegates to repository with correct parameters`() = runTest {
        val records = listOf(
            TranslationRecord(sourceText = "hello", targetText = "hola")
        )
        repo.resultToReturn = "Generated content"

        val result = useCase("gpt-4o", "en-US", "es-ES", records)

        assertEquals("Generated content", result)
        assertEquals(DeploymentName("gpt-4o"), repo.lastDeployment)
        assertEquals(LanguageCode("en-US"), repo.lastPrimaryCode)
        assertEquals(LanguageCode("es-ES"), repo.lastTargetCode)
        assertEquals(records, repo.lastRecords)
    }

    @Test
    fun `invoke returns content from repository`() = runTest {
        val records = listOf(
            TranslationRecord(sourceText = "cat", targetText = "gato")
        )
        repo.resultToReturn = "Learning sheet content here"

        val result = useCase("gpt-4o", "en-US", "ja-JP", records)

        assertEquals("Learning sheet content here", result)
    }

    @Test(expected = RuntimeException::class)
    fun `invoke propagates repository exception`() = runTest {
        repo.exceptionToThrow = RuntimeException("API error")

        useCase("gpt-4o", "en-US", "es-ES", emptyList())
    }
}
