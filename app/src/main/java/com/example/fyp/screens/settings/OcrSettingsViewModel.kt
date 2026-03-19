package com.example.fyp.screens.settings

import androidx.lifecycle.ViewModel
import com.example.fyp.data.ocr.MLKitOcrRepository
import com.example.fyp.model.OcrScript
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * UI state for OCR Settings screen.
 * Shows information about bundled OCR models.
 */
data class OcrSettingsUiState(
    val supportedScripts: List<OcrScript> = OcrScript.entries,
    val totalSizeMb: Int = OcrScript.entries.sumOf { it.estimatedSizeMb }
)

/**
 * ViewModel for displaying OCR model information.
 * All models are bundled with the app, so this is informational only.
 */
@HiltViewModel
class OcrSettingsViewModel @Inject constructor(
    private val ocrRepository: MLKitOcrRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrSettingsUiState())
    val uiState: StateFlow<OcrSettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = OcrSettingsUiState(
            supportedScripts = ocrRepository.getSupportedScripts(),
            totalSizeMb = ocrRepository.getSupportedScripts().sumOf { it.estimatedSizeMb }
        )
    }
}
