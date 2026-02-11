package com.example.fyp.model

import android.graphics.Rect

/**
 * Result from OCR processing
 */
sealed class OcrResult {
    data class Success(
        val text: String,
        val blocks: List<TextBlock> = emptyList()
    ) : OcrResult()

    data class Error(val message: String) : OcrResult()
}

/**
 * A block of recognized text with bounding box
 */
data class TextBlock(
    val text: String,
    val boundingBox: Rect? = null,
    val language: String? = null
)
