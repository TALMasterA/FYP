package com.example.fyp.core.extensions

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.fyp.core.security.ValidationResult
import com.example.fyp.core.security.sanitizeInput
import com.example.fyp.core.security.validateEmail
import com.example.fyp.ui.theme.AppSpacing

// ── String extensions ──────────────────────────────────────────────

/**
 * Returns true when the string is a syntactically valid email address
 * according to [validateEmail].
 */
fun String.isValidEmail(): Boolean = validateEmail(this) is ValidationResult.Valid

/**
 * Returns a sanitized copy of this string with HTML-dangerous characters
 * escaped (see [sanitizeInput]).
 */
fun String.sanitized(): String = sanitizeInput(this)

/**
 * Truncates the string to [maxLength] characters, appending an ellipsis
 * if it was shortened.
 */
fun String.truncate(maxLength: Int): String =
    if (length <= maxLength) this else take(maxLength) + "\u2026"

/**
 * Returns the string with the first character capitalised and the rest
 * unchanged. Safe for empty strings.
 */
fun String.capitalizeFirst(): String =
    if (isEmpty()) this else this[0].uppercase() + substring(1)

// ── Modifier extensions ────────────────────────────────────────────

/**
 * Applies the standard large (16 dp) screen-edge padding.
 */
fun Modifier.standardPadding(): Modifier = padding(AppSpacing.large)

/**
 * Applies the standard small (8 dp) content padding.
 */
fun Modifier.smallPadding(): Modifier = padding(AppSpacing.small)

// ── Collection extensions ──────────────────────────────────────────

/**
 * Returns the list unchanged when it is non-empty, or null when empty.
 * Useful with the Elvis operator for fallback logic.
 */
fun <T> List<T>.orNullIfEmpty(): List<T>? = ifEmpty { null }
