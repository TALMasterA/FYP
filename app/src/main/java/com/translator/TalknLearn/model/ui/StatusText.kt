package com.translator.TalknLearn.model.ui

/**
 * A short status line that a ViewModel emits and a screen shows in the
 * user's UI language.
 *
 * [key] points to a template in the UiText tables. Each `{name}` in the
 * template is replaced by `args[name]`. Args carry data only (an error
 * detail from a service, a language name, a number), never English UI text,
 * so the whole line follows the selected UI language.
 *
 * Example: `StatusText(UiTextKey.SpeechStatusDetected, mapOf("language" to "Japanese"))`
 * with the template "Detected: {language}" shows "Detected: Japanese".
 */
data class StatusText(
    val key: UiTextKey,
    val args: Map<String, String> = emptyMap(),
) {
    /** Builds the final text with the screen's translator [t]. */
    fun resolve(t: (UiTextKey) -> String): String =
        args.entries.fold(t(key)) { text, (name, value) -> text.replace("{$name}", value) }
}
