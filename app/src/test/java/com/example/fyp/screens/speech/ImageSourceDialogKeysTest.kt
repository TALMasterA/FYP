package com.example.fyp.screens.speech

import com.example.fyp.model.ui.UiTextKey
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for ImageCaptureComponents logic.
 * Verifies that all required UI text keys for the image source dialog exist
 * and that the camera language hint key is properly defined.
 */
class ImageSourceDialogKeysTest {

    @Test
    fun `camera language hint UiTextKey exists`() {
        // Ensures the CameraLanguageHint key was properly added to the enum
        val key = UiTextKey.CameraLanguageHint
        assertNotNull(key)
        assertTrue(key.ordinal > 0)
    }

    @Test
    fun `all image source dialog keys exist in UiTextKey`() {
        val requiredKeys = listOf(
            UiTextKey.ImageRecognitionButton,
            UiTextKey.ImageSourceTitle,
            UiTextKey.ImageSourceCamera,
            UiTextKey.ImageSourceGallery,
            UiTextKey.ImageSourceCancel,
            UiTextKey.ImageRecognitionAccuracyWarning,
            UiTextKey.CameraLanguageHint,
        )
        requiredKeys.forEach { key ->
            assertNotNull("UiTextKey.${key.name} must exist", key)
        }
    }

    @Test
    fun `camera permission keys exist in UiTextKey`() {
        val requiredKeys = listOf(
            UiTextKey.CameraPermissionTitle,
            UiTextKey.CameraPermissionMessage,
            UiTextKey.CameraPermissionGrant,
        )
        requiredKeys.forEach { key ->
            assertNotNull("UiTextKey.${key.name} must exist", key)
        }
    }
}
