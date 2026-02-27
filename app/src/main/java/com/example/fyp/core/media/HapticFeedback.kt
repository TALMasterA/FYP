package com.example.fyp.core

/**
 * Haptic feedback utility for providing tactile feedback on user interactions.
 *
 * This is a utility class ready for integration into screens and components.
 * "Unused" warnings are expected until integrated.
 *
 * Usage:
 * ```
 * val haptic = rememberHapticFeedback()
 * Button(onClick = {
 *     haptic.click()
 *     onAction()
 * }) { Text("Click") }
 * ```
 */

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Haptic feedback handler that provides various tactile feedback types.
 */
class HapticFeedback(private val view: View) {

    /**
     * Perform a light click haptic feedback.
     * Use for standard button presses and selections.
     */
    fun click() {
        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
    }

    /**
     * Perform a long press haptic feedback.
     * Use for long press actions and drag operations.
     */
    fun longPress() {
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    /**
     * Perform a reject/error haptic feedback.
     * Use for error states or rejected actions.
     */
    fun reject() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            // Fallback for older versions
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    /**
     * Perform a confirm/success haptic feedback.
     * Use for successful operations and confirmations.
     */
    fun confirm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            // Fallback for older versions
            performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    /**
     * Perform a keyboard key press haptic feedback.
     * Use for virtual keyboard interactions.
     */
    fun keyboardPress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS)
        } else {
            performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    /**
     * Perform haptic feedback for virtual key release.
     */
    fun keyboardRelease() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE)
        }
    }

    /**
     * Perform a gesture start haptic feedback.
     * Use when starting drag or swipe gestures.
     */
    fun gestureStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
        } else {
            performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        }
    }

    /**
     * Perform a gesture end haptic feedback.
     * Use when completing drag or swipe gestures.
     */
    fun gestureEnd() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
        }
    }

    private fun performHapticFeedback(feedbackConstant: Int) {
        view.performHapticFeedback(feedbackConstant, 0)
    }
}

/**
 * Remember a HapticFeedback instance for the current composable context.
 */
@Composable
fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    return remember(view) { HapticFeedback(view) }
}

