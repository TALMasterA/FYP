package com.translator.TalknLearn.data.ocr

import android.content.Context
import android.net.Uri
import com.translator.TalknLearn.model.OcrResult
import com.translator.TalknLearn.model.OcrScript
import com.translator.TalknLearn.model.TextBlock
import com.translator.TalknLearn.utils.ErrorMessageMapper
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Repository for ML Kit text recognition (OCR).
 * Uses on-device bundled models for privacy and speed.
 *
 * Supports multiple scripts:
 * - Latin (English, European languages)
 * - Chinese (Simplified and Traditional)
 * - Japanese (Kanji, Hiragana, Katakana)
 * - Korean (Hangul)
 *
 * The appropriate recognizer is automatically selected based on the language code.
 * All models are bundled with the app for reliable offline operation.
 */
@Singleton
class MLKitOcrRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        /** Length of language code prefix used for script detection */
        const val LANGUAGE_PREFIX_LENGTH = 2
    }

    private val latinRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private val chineseRecognizer by lazy {
        TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    }

    private val japaneseRecognizer by lazy {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }

    private val koreanRecognizer by lazy {
        TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    }

    /**
     * Recognize text from an image URI.
     * Automatically selects the appropriate text recognizer based on language code.
     *
     * @param uri The image URI (from camera or gallery)
     * @param languageCode The ISO language code (e.g. "zh-HK", "ja-JP") to select script
     * @return OcrResult with recognized text blocks or error message
     */
    suspend fun recognizeText(uri: Uri, languageCode: String? = null): OcrResult = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromFilePath(context, uri)

            val recognizer = when (languageCode?.lowercase()?.take(LANGUAGE_PREFIX_LENGTH)) {
                "zh" -> chineseRecognizer
                "ja" -> japaneseRecognizer
                "ko" -> koreanRecognizer
                else -> latinRecognizer
            }

            suspendCancellableCoroutine<OcrResult> { continuation ->
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val blocks = visionText.textBlocks.map { block ->
                            TextBlock(
                                text = block.text,
                                boundingBox = block.boundingBox,
                                language = block.recognizedLanguage
                            )
                        }

                        val result = if (visionText.text.isNotBlank()) {
                            OcrResult.Success(
                                text = visionText.text.trim(),
                                blocks = blocks
                            )
                        } else {
                            OcrResult.Error("No text detected in image")
                        }

                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        val errorMessage = ErrorMessageMapper.mapOcrError(exception.message ?: "OCR processing failed")
                        continuation.resume(OcrResult.Error(errorMessage))
                    }

                continuation.invokeOnCancellation {
                    // Cleanup if needed
                }
            }
        } catch (e: Exception) {
            val errorMessage = ErrorMessageMapper.mapOcrError(e.message ?: "Failed to load image")
            OcrResult.Error(errorMessage)
        }
    }
}
