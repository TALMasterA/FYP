package com.example.fyp.model

import kotlinx.serialization.Serializable

/**
 * Value classes for type safety in the FYP Translation & Learning App.
 *
 * These inline value classes provide compile-time type safety with zero runtime overhead.
 * They prevent accidental misuse of string parameters (e.g., swapping userId and languageCode).
 *
 * Benefits:
 * - Compile-time type checking
 * - Zero runtime cost (inlined to underlying type)
 * - Self-documenting code
 * - Validation at construction time
 *
 * @see <a href="https://kotlinlang.org/docs/inline-classes.html">Kotlin Inline Classes</a>
 */

/**
 * Wraps user ID strings to prevent accidental misuse.
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId cannot be blank" }
    }
}

/**
 * Wraps language code strings (e.g., "en-US", "ja-JP").
 * @throws IllegalArgumentException if the value doesn't match the expected format
 */
@JvmInline
@Serializable
value class LanguageCode(val value: String) {
    init {
        require(value.isNotBlank()) { "LanguageCode cannot be blank" }
        require(value.matches(Regex("^[a-z]{2}-[A-Z]{2}$"))) {
            "LanguageCode must match format 'xx-XX' (e.g., 'en-US'), got: $value"
        }
    }
}

/**
 * Wraps document/record ID strings.
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class RecordId(val value: String) {
    init {
        require(value.isNotBlank()) { "RecordId cannot be blank" }
    }
}

/**
 * Wraps conversation session ID strings.
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class SessionId(val value: String) {
    init {
        require(value.isNotBlank()) { "SessionId cannot be blank" }
    }
}

/**
 * Wraps color palette identifier strings.
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class PaletteId(val value: String) {
    init {
        require(value.isNotBlank()) { "PaletteId cannot be blank" }
    }
}

/**
 * Wraps Azure TTS voice identifier strings (e.g., "en-US-JennyNeural").
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class VoiceName(val value: String) {
    init {
        require(value.isNotBlank()) { "VoiceName cannot be blank" }
    }
}

/**
 * Wraps AI model deployment name strings (e.g., "gpt-4o").
 * @throws IllegalArgumentException if the value is blank
 */
@JvmInline
@Serializable
value class DeploymentName(val value: String) {
    init {
        require(value.isNotBlank()) { "DeploymentName cannot be blank" }
    }
}

// Extension functions for common conversions
fun String.toUserId() = UserId(this)
fun String.toLanguageCode() = LanguageCode(this)
fun String.toRecordId() = RecordId(this)
fun String.toSessionId() = SessionId(this)
fun String.toPaletteId() = PaletteId(this)
fun String.toVoiceName() = VoiceName(this)
fun String.toDeploymentName() = DeploymentName(this)
