package com.example.fyp.domain.ocr

import android.net.Uri
import com.example.fyp.data.ocr.MLKitOcrRepository
import com.example.fyp.model.OcrResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RecognizeTextFromImageUseCaseTest {

    private lateinit var ocrRepository: MLKitOcrRepository
    private lateinit var useCase: RecognizeTextFromImageUseCase

    @Before
    fun setup() {
        ocrRepository = mock()
        useCase = RecognizeTextFromImageUseCase(ocrRepository)
    }

    @Test
    fun `invoke delegates to OCR repository with correct parameters`() = runTest {
        // Arrange
        val imageUri = mock<Uri>()
        val languageCode = "zh-CN"
        val expected = OcrResult.Success("你好世界")
        whenever(ocrRepository.recognizeText(imageUri, languageCode))
            .thenReturn(expected)

        // Act
        val result = useCase(imageUri, languageCode)

        // Assert
        assertEquals(expected, result)
        verify(ocrRepository).recognizeText(imageUri, languageCode)
    }

    @Test
    fun `invoke works with null language code`() = runTest {
        // Arrange
        val imageUri = mock<Uri>()
        val expected = OcrResult.Success("Hello World")
        whenever(ocrRepository.recognizeText(imageUri, null))
            .thenReturn(expected)

        // Act
        val result = useCase(imageUri, null)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns error when OCR fails`() = runTest {
        // Arrange
        val imageUri = mock<Uri>()
        val expected = OcrResult.Error("OCR processing failed")
        whenever(ocrRepository.recognizeText(imageUri, null))
            .thenReturn(expected)

        // Act
        val result = useCase(imageUri)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `invoke handles different language codes`() = runTest {
        // Arrange
        val imageUri = mock<Uri>()
        val languageCodes = listOf("en-US", "zh-CN", "ja-JP", "ko-KR")

        languageCodes.forEach { code ->
            val expected = OcrResult.Success("Text in $code")
            whenever(ocrRepository.recognizeText(imageUri, code))
                .thenReturn(expected)

            // Act
            val result = useCase(imageUri, code)

            // Assert
            assertEquals(expected, result)
        }
    }
}
