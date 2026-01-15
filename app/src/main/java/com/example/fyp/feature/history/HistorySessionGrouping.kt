package com.example.fyp.feature.history

import com.example.fyp.model.TranslationRecord

data class HistorySessionUi(
    val sessionId: String,
    val records: List<TranslationRecord>
)

fun groupContinuousSessions(records: List<TranslationRecord>): List<HistorySessionUi> {
    return records
        .asSequence()
        .filter { it.mode == "continuous" }
        .filter { it.sessionId.isNotBlank() }
        .groupBy { it.sessionId }
        .map { (sid, recs) -> HistorySessionUi(sid, recs) }
        .sortedByDescending { session -> session.records.lastOrNull()?.timestamp }
        .toList()
}

/**
 * Supports both styles:
 * - New: "Session {id}"
 * - Old: "Session %s" (or any translated variant that still uses %s)
 */
fun formatSessionTitle(template: String, sessionId: String): String {
    val shortId = sessionId.take(8)
    return when {
        template.contains("{id}") -> template.replace("{id}", shortId)
        else -> safeFormat(template, shortId)
    }
}

/**
 * Supports both styles:
 * - New: "{count} item(s)"
 * - Old: "%d item(s)" (or any translated variant that still uses %d)
 */
fun formatItemsCount(template: String, count: Int): String {
    return when {
        template.contains("{count}") -> template.replace("{count}", count.toString())
        else -> safeFormat(template, count)
    }
}

private fun safeFormat(template: String, vararg args: Any): String {
    return try {
        String.format(template, *args)
    } catch (_: Exception) {
        // Escape unknown % patterns then try again.
        val escaped = template.replace(Regex("%(?!([0-9]+\\$)?[sd%])"), "%%")
        try {
            String.format(escaped, *args)
        } catch (_: Exception) {
            template
        }
    }
}