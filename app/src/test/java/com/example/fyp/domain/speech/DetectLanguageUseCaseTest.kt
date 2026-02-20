package com.example.fyp.domain.speech

import com.example.fyp.data.clients.DetectedLanguage
import com.example.fyp.data.repositories.TranslationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify

class DetectLanguageUseCaseTest {

    private lateinit var translationRepository: TranslationRepository
    private lateinit var useCase: DetectLanguageUseCase

    @Before
    fun setup() {
        translationRepository = mock()
        useCase = DetectLanguageUseCase(translationRepository)
    }

    @Test
    fun `invoke returns detected language when successful`() = runTest {
        // Arrange
        val text = "Hello world"
        val expected = DetectedLanguage("en", 0.99, true)
        translationRepository.stub {
            onBlocking { detectLanguage(text) } doReturn expected
        }

        // Act
        val result = useCase(text)

        // Assert
        assertEquals(expected, result)
        verify(translationRepository).detectLanguage(text)
    }

    @Test
    fun `invoke returns null when detection fails`() = runTest {
        // Arrange
        val text = "Unknown text"
        translationRepository.stub {
            onBlocking { detectLanguage(text) } doReturn null
        }

        // Act
        val result = useCase(text)

        // Assert
        assertNull(result)
    }

    @Test
    fun `invoke handles different languages`() = runTest {
        // Arrange
        val testCases = listOf(
            Pair("Hello", DetectedLanguage("en", 0.99, true)),
            Pair("你好", DetectedLanguage("zh", 0.98, true)),
            Pair("こんにちは", DetectedLanguage("ja", 0.97, true)),
            Pair("안녕하세요", DetectedLanguage("ko", 0.96, true))
        )

        // Set up all stubs before executing
        testCases.forEach { (text, expected) ->
            translationRepository.stub {
                onBlocking { detectLanguage(text) } doReturn expected
            }
        }

        // Act & Assert
        testCases.forEach { (text, expected) ->
            val result = useCase(text)
            assertEquals(expected, result)
        }
    }

    @Test
    fun `invoke handles empty text`() = runTest {
        // Arrange
        val text = ""
        translationRepository.stub {
            onBlocking { detectLanguage(text) } doReturn null
        }

        // Act
        val result = useCase(text)

        // Assert
        assertNull(result)
    }
}
