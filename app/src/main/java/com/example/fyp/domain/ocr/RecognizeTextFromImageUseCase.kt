package com.example.fyp.domain.ocr

import android.net.Uri
import com.example.fyp.data.ocr.MLKitOcrRepository
import com.example.fyp.model.OcrResult
import javax.inject.Inject

/**
 * Use case for recognizing text from images using ML Kit
 */
class RecognizeTextFromImageUseCase @Inject constructor(
    private val ocrRepository: MLKitOcrRepository
) {
    /**
     * Process an image and extract text
     * @param imageUri URI of the image to process
     * @param languageCode Optional language code to select script (e.g. "zh-CN", "ja-JP")
     * @return OcrResult containing extracted text or error
     */
    suspend operator fun invoke(imageUri: Uri, languageCode: String? = null): OcrResult {
        return ocrRepository.recognizeText(imageUri, languageCode)
    }
}
