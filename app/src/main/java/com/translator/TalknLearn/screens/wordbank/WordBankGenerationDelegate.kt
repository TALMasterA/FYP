package com.translator.TalknLearn.screens.wordbank

import com.translator.TalknLearn.core.AiConfig
import com.translator.TalknLearn.core.AppLogger
import com.translator.TalknLearn.data.history.SharedHistoryDataSource
import com.translator.TalknLearn.data.wordbank.FirestoreWordBankRepository
import com.translator.TalknLearn.data.wordbank.WordBankGenerationRepository
import com.translator.TalknLearn.domain.learning.GenerationEligibility
import com.translator.TalknLearn.model.TranslationRecord
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID

/** Debounce time for word bank generation to prevent duplicate API calls. */
private const val GENERATION_DEBOUNCE_MS = 2000L

/**
 * AI-driven word-bank generation and regeneration logic.
 * Extracted from [WordBankViewModel] for maintainability.
 */
internal class WordBankGenerationDelegate(
    private val uiState: MutableStateFlow<WordBankUiState>,
    private val scope: CoroutineScope,
    private val wordBankRepo: FirestoreWordBankRepository,
    private val wordBankGenRepo: WordBankGenerationRepository,
    private val sharedHistoryDataSource: SharedHistoryDataSource,
    private val getCurrentUserId: () -> String?,
    private val getPrimaryLanguageCode: () -> String,
    private val getRecords: () -> List<TranslationRecord>,
    private val invalidateWordBankCache: (String) -> Unit,
    private val refreshClusters: () -> Unit
) {
    private var lastGenerationTime = 0L
    private var generationJob: Job? = null

    fun canRegenerate(targetLanguageCode: String): Boolean {
        val currentWordBank = uiState.value.currentWordBank ?: return true
        val currentCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
        val savedCount = currentWordBank.historyCountAtGenerate
        return GenerationEligibility.canRegenerateWordBank(currentCount, savedCount)
    }

    fun getNewRecordCount(targetLanguageCode: String): Int {
        val currentWordBank = uiState.value.currentWordBank ?: return 0
        val currentCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
        return currentCount - currentWordBank.historyCountAtGenerate
    }

    fun getCurrentHistoryCount(targetLanguageCode: String): Int {
        return sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        uiState.value = uiState.value.copy(isGenerating = false, error = null)
    }

    fun consumeWordBankGenerationCompleted() {
        uiState.value = uiState.value.copy(wordBankGenerationCompleted = null)
    }

    fun refreshLanguageCounts() {
        scope.launch {
            sharedHistoryDataSource.forceRefreshLanguageCounts(getPrimaryLanguageCode())
            refreshClusters()
        }
    }

    fun generateWordBank(targetLanguageCode: String) {
        val uid = getCurrentUserId() ?: return

        val now = System.currentTimeMillis()
        if (now - lastGenerationTime < GENERATION_DEBOUNCE_MS) return
        lastGenerationTime = now

        val existingWordBank = uiState.value.currentWordBank
        if (existingWordBank != null && !canRegenerate(targetLanguageCode)) {
            uiState.value = uiState.value.copy(
                error = "Need ${GenerationEligibility.MIN_RECORDS_FOR_REGEN} more records to refresh word bank"
            )
            return
        }

        generationJob?.cancel()

        generationJob = scope.launch {
            uiState.value = uiState.value.copy(isGenerating = true, error = null)

            try {
                val relevantRecords = getRecords().filter {
                    it.sourceLang == targetLanguageCode || it.targetLang == targetLanguageCode
                }

                if (relevantRecords.isEmpty()) {
                    uiState.value = uiState.value.copy(
                        isGenerating = false,
                        error = "No translation history found for this language"
                    )
                    return@launch
                }

                val primaryLang = getPrimaryLanguageCode()
                val deployment = AiConfig.DEFAULT_DEPLOYMENT
                val rawContent = wordBankGenRepo.generateWordBank(
                    deployment = deployment,
                    primaryLanguageCode = primaryLang,
                    targetLanguageCode = targetLanguageCode,
                    records = relevantRecords
                )

                ensureActive()

                val words = parseWordBankResponse(rawContent)

                if (words.isEmpty()) {
                    uiState.value = uiState.value.copy(
                        isGenerating = false,
                        error = "Failed to generate word bank. Please try again."
                    )
                    return@launch
                }

                ensureActive()

                val unifiedCount = sharedHistoryDataSource.getCountForLanguage(targetLanguageCode)

                wordBankRepo.appendWords(
                    uid = uid,
                    primary = primaryLang,
                    target = targetLanguageCode,
                    newWords = words,
                    historyCount = unifiedCount
                )

                val existingWords = uiState.value.currentWordBank?.words ?: emptyList()
                val existingOriginals = existingWords.map { it.originalWord.lowercase().trim() }.toSet()
                val uniqueNew = words.filter { it.originalWord.lowercase().trim() !in existingOriginals }
                val mergedWords = existingWords + uniqueNew

                val updatedWordBank = WordBank(
                    primaryLanguageCode = primaryLang,
                    targetLanguageCode = targetLanguageCode,
                    words = mergedWords,
                    historyCountAtGenerate = unifiedCount
                )

                invalidateWordBankCache(targetLanguageCode)
                refreshClusters()

                uiState.value = uiState.value.copy(
                    isGenerating = false,
                    currentWordBank = updatedWordBank,
                    wordBankGenerationCompleted = targetLanguageCode
                )
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    isGenerating = false,
                    error = "Generation failed: ${e.message}"
                )
            } finally {
                generationJob = null
            }
        }
    }

    internal suspend fun parseWordBankResponse(content: String): List<WordBankItem> {
        return withContext(Dispatchers.Default) {
            try {
                var jsonStr = content.trim()

                if (jsonStr.contains("```")) {
                    val startIdx = jsonStr.indexOf("{")
                    val endIdx = jsonStr.lastIndexOf("}") + 1
                    if (startIdx >= 0 && endIdx > startIdx) {
                        jsonStr = jsonStr.substring(startIdx, endIdx)
                    }
                }

                val firstBrace = jsonStr.indexOf("{")
                val lastBrace = jsonStr.lastIndexOf("}")
                if (firstBrace >= 0 && lastBrace > firstBrace) {
                    jsonStr = jsonStr.substring(firstBrace, lastBrace + 1)
                }

                val json = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                val jsonObject = json.parseToJsonElement(jsonStr).jsonObject
                val wordsArray = jsonObject["words"]?.jsonArray ?: return@withContext emptyList()

                wordsArray.map { element ->
                    val wordObj = element.jsonObject
                    WordBankItem(
                        id = UUID.randomUUID().toString(),
                        originalWord = wordObj["original"]?.jsonPrimitive?.content ?: "",
                        translatedWord = wordObj["translated"]?.jsonPrimitive?.content ?: "",
                        pronunciation = wordObj["pronunciation"]?.jsonPrimitive?.content ?: "",
                        example = wordObj["example"]?.jsonPrimitive?.content ?: "",
                        category = wordObj["category"]?.jsonPrimitive?.content ?: "",
                        difficulty = wordObj["difficulty"]?.jsonPrimitive?.content ?: ""
                    )
                }.filter { it.originalWord.isNotBlank() && it.translatedWord.isNotBlank() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLogger.e("WordBankVM", "Failed to parse word bank: ${e.message}", e)
                throw IllegalStateException("Failed to parse generated content: ${e.message}", e)
            }
        }
    }

    fun cancelJob() {
        generationJob?.cancel()
    }
}
