package com.translator.TalknLearn.domain.learning

import com.translator.TalknLearn.model.TranslationRecord

/**
 * Picks the translation records that go into an AI prompt (learning sheet
 * or word bank).
 *
 * Rules:
 * - Only records that use [languageCode] (as source or target) are kept.
 * - The newest records win. History arrives newest-first from Firestore,
 *   but this helper sorts by timestamp itself, so the input order does not matter.
 * - At most `limit` records are kept, newest first.
 */
object PromptRecordSelector {

    /** Max records in a learning-sheet prompt. */
    const val LEARNING_SHEET_LIMIT = 20

    /** Max records in a word-bank prompt. */
    const val WORD_BANK_LIMIT = 30

    fun select(
        records: List<TranslationRecord>,
        languageCode: String,
        limit: Int
    ): List<TranslationRecord> =
        records.asSequence()
            .filter { it.sourceLang == languageCode || it.targetLang == languageCode }
            .sortedByDescending { it.timestamp }
            .take(limit)
            .toList()

    fun formatLine(record: TranslationRecord): String =
        "- [${record.sourceLang}→${record.targetLang}] ${record.sourceText} => ${record.targetText}"

    /** The selected records as prompt lines, one per line, newest first. */
    fun buildBlock(
        records: List<TranslationRecord>,
        languageCode: String,
        limit: Int
    ): String = select(records, languageCode, limit).joinToString("\n", transform = ::formatLine)
}
