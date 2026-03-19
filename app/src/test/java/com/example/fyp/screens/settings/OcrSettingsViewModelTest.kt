package com.example.fyp.screens.settings

import com.example.fyp.data.ocr.MLKitOcrRepository
import com.example.fyp.model.OcrScript
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * Unit tests for OcrSettingsViewModel.
 * Verifies correct initialization and state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OcrSettingsViewModelTest {

    @Mock
    private lateinit var ocrRepository: MLKitOcrRepository

    private lateinit var viewModel: OcrSettingsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(ocrRepository.getSupportedScripts()).thenReturn(OcrScript.entries)
    }

    @Test
    fun `initial state contains all supported scripts`() = runTest {
        viewModel = OcrSettingsViewModel(ocrRepository)

        val state = viewModel.uiState.value
        assertEquals(4, state.supportedScripts.size)
        assertTrue(state.supportedScripts.contains(OcrScript.LATIN))
        assertTrue(state.supportedScripts.contains(OcrScript.CHINESE))
        assertTrue(state.supportedScripts.contains(OcrScript.JAPANESE))
        assertTrue(state.supportedScripts.contains(OcrScript.KOREAN))
    }

    @Test
    fun `initial state calculates total size correctly`() = runTest {
        viewModel = OcrSettingsViewModel(ocrRepository)

        val expectedSize = OcrScript.entries.sumOf { it.estimatedSizeMb }
        assertEquals(expectedSize, viewModel.uiState.value.totalSizeMb)
    }

    @Test
    fun `total size is sum of all script sizes`() {
        val totalSize = OcrScript.entries.sumOf { it.estimatedSizeMb }
        // Latin (5) + Chinese (10) + Japanese (10) + Korean (8) = 33
        assertEquals(33, totalSize)
    }

    @Test
    fun `supported scripts list is not empty`() = runTest {
        viewModel = OcrSettingsViewModel(ocrRepository)

        assertTrue(viewModel.uiState.value.supportedScripts.isNotEmpty())
    }
}
