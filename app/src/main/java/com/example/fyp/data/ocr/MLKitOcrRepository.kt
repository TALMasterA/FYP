package com.example.fyp.data.ocr

import android.content.Context
import android.net.Uri
import com.example.fyp.model.OcrResult
import com.example.fyp.model.TextBlock
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
 * Repository for ML Kit text recognition (OCR)
 * Uses on-device processing for privacy and speed
 */
@Singleton
class MLKitOcrRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
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
     * Recognize text from an image URI
     * @param uri The image URI (from camera or gallery)
     * @param languageCode The ISO language code (e.g. "zh-HK", "ja-JP") to select script
     * @return OcrResult with recognized text or error
     */
    suspend fun recognizeText(uri: Uri, languageCode: String? = null): OcrResult = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            
            val recognizer = when (languageCode?.lowercase()?.take(2)) {
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
                        continuation.resume(
                            OcrResult.Error(exception.message ?: "OCR processing failed")
                        )
                    }
                
                continuation.invokeOnCancellation {
                    // Cleanup if needed
                }
            }
        } catch (e: Exception) {
            OcrResult.Error(e.message ?: "Failed to load image")
        }
    }

    /**
     * Clean up resources when repository is no longer needed
     */
    fun close() {
        if (latinRecognizer != null) latinRecognizer.close()
        // Lazy-initialized instances need checking if they were initialized,
        // but Kotlin lazy properties don't expose 'isInitialized' easily without reflection
        // or keeping a separate flag. For simplicity in Singleton we can just close them if we track them.
        // However, standard ML Kit clients auto-close or are lightweight enough if app is killed.
        // But for correctness:
        try { latinRecognizer.close() } catch(e: Exception) {}
        try { chineseRecognizer.close() } catch(e: Exception) {}
        try { japaneseRecognizer.close() } catch(e: Exception) {}
        try { koreanRecognizer.close() } catch(e: Exception) {}
    }
}