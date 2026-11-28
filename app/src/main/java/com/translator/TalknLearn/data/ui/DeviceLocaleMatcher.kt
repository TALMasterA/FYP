package com.translator.TalknLearn.data.ui

import java.util.Locale

/**
 * Picks the UI language to use on first launch, before the user has chosen one,
 * from the device locale.
 *
 * Rules:
 * - Chinese is matched by script and region: Traditional Hong Kong / Macau use the
 *   Cantonese UI (zh-HK), other Traditional uses zh-TW, everything else uses zh-CN.
 * - Other languages are matched by language only (fr-CA -> fr-FR, pt-PT -> pt-BR).
 * - Old Java codes are handled: "in" is Indonesian, "tl" is Filipino.
 * - If nothing matches, [fallback] is used.
 */
object DeviceLocaleMatcher {

    fun match(locale: Locale, supported: Set<String>, fallback: String): String {
        val language = when (val l = locale.language.lowercase(Locale.ROOT)) {
            "in" -> "id"
            "tl" -> "fil"
            else -> l
        }
        if (language.isEmpty()) return fallback

        val candidate = if (language == "zh") {
            chineseCode(locale)
        } else {
            supported.firstOrNull { it.substringBefore('-').equals(language, ignoreCase = true) }
        }
        return candidate?.takeIf { it in supported } ?: fallback
    }

    private fun chineseCode(locale: Locale): String {
        val script = locale.script
        val region = locale.country.uppercase(Locale.ROOT)
        val traditional = script.equals("Hant", ignoreCase = true) ||
            (script.isEmpty() && region in setOf("HK", "MO", "TW"))
        return when {
            traditional && region in setOf("HK", "MO") -> "zh-HK"
            traditional -> "zh-TW"
            else -> "zh-CN"
        }
    }
}
