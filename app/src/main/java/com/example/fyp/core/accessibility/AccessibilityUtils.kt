package com.example.fyp.core.accessibility

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

/**
 * Accessibility helpers for Compose UI.
 * Provides convenience modifiers and composable utilities
 * to improve screen-reader support throughout the app.
 */

// ── Modifier helpers ───────────────────────────────────────────────

/**
 * Sets a [contentDescription] on the node so screen readers can
 * announce the purpose of a decorative or custom element.
 *
 * @param description The text spoken by the screen reader
 */
fun Modifier.accessibilityDescription(description: String): Modifier =
    semantics { contentDescription = description }

/**
 * Marks a custom composable as a semantic button with the given description.
 * Use this on `Box` / `Row` wrappers that act as tap targets but are not
 * native Material buttons.
 *
 * @param description The action announced to screen readers
 */
fun Modifier.accessibilityButton(description: String): Modifier =
    semantics {
        role = Role.Button
        contentDescription = description
    }

/**
 * Marks a node as a semantic image with the given description.
 *
 * @param description The meaning of the image for screen readers
 */
fun Modifier.accessibilityImage(description: String): Modifier =
    semantics {
        role = Role.Image
        contentDescription = description
    }

// ── Composable helpers ─────────────────────────────────────────────

/**
 * Returns whether a screen reader (e.g. TalkBack) is currently enabled.
 * Useful for conditionally showing extra UI hints.
 */
@Composable
fun rememberIsScreenReaderEnabled(): Boolean {
    val context = LocalContext.current
    return remember {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        am?.isTouchExplorationEnabled == true
    }
}
