package com.example.fyp.data.ocr

import android.content.Context
import android.net.Uri
import com.example.fyp.model.OcrResult
import com.example.fyp.model.TextBlock
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
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
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Recognize text from an image URI
     * @param uri The image URI (from camera or gallery)
     * @return OcrResult with recognized text or error
     */
    suspend fun recognizeText(uri: Uri): OcrResult = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            
            suspendCancellableCoroutine { continuation ->
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
        recognizer.close()
    }
}
