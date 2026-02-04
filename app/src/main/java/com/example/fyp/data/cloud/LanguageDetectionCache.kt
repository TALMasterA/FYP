package com.example.fyp.data.cloud

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.fyp.data.clients.DetectedLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDetectionCacheDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "language_detection_cache"
)

@Serializable
data class CachedDetection(
    val text: String,
    val language: String,
    val score: Double,
    val isTranslationSupported: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class LanguageDetectionCacheData(
    val entries: Map<String, CachedDetection> = emptyMap()
)

/**
 * Local cache for language detection results.
 * Reduces API calls by caching detected languages for text that has been analyzed before.
 * Uses normalized text (trimmed, lowercase) as cache key.
 */
@Singleton
class LanguageDetectionCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("detection_cache")
        private const val MAX_CACHE_SIZE = 200
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Generate cache key from text (normalized)
     */
    private fun cacheKey(text: String): String {
        return text.trim().lowercase().take(500) // Limit key size
    }

    /**
     * Get cached detection result if available and not expired
     */
    suspend fun getCached(text: String): DetectedLanguage? {
        val key = cacheKey(text)
        val cacheData = loadCache()
        val entry = cacheData.entries[key] ?: return null

        // Check if expired
        if (System.currentTimeMillis() - entry.timestamp > CACHE_TTL_MS) {
            removeEntry(key)
            return null
        }

        return DetectedLanguage(
            language = entry.language,
            score = entry.score,
            isTranslationSupported = entry.isTranslationSupported,
            alternatives = emptyList()
        )
    }

    /**
     * Cache a detection result
     */
    suspend fun cache(text: String, result: DetectedLanguage) {
        val key = cacheKey(text)
        val entry = CachedDetection(
            text = text.trim().take(500),
            language = result.language,
            score = result.score,
            isTranslationSupported = result.isTranslationSupported
        )

        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        newEntries[key] = entry

        // Evict old entries if cache is full
        if (newEntries.size > MAX_CACHE_SIZE) {
            val sortedEntries = newEntries.entries.sortedBy { it.value.timestamp }
            val entriesToRemove = sortedEntries.take(newEntries.size - MAX_CACHE_SIZE)
            entriesToRemove.forEach { newEntries.remove(it.key) }
        }

        saveCache(LanguageDetectionCacheData(newEntries))
    }

    /**
     * Clear all cached detections
     */
    suspend fun clearAll() {
        context.languageDetectionCacheDataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
        }
    }

    private suspend fun loadCache(): LanguageDetectionCacheData {
        return try {
            context.languageDetectionCacheDataStore.data
                .map { prefs ->
                    val jsonString = prefs[CACHE_KEY]
                    if (jsonString != null) {
                        json.decodeFromString<LanguageDetectionCacheData>(jsonString)
                    } else {
                        LanguageDetectionCacheData()
                    }
                }
                .first()
        } catch (e: Exception) {
            LanguageDetectionCacheData()
        }
    }

    private suspend fun saveCache(data: LanguageDetectionCacheData) {
        try {
            context.languageDetectionCacheDataStore.edit { prefs ->
                prefs[CACHE_KEY] = json.encodeToString(data)
            }
        } catch (e: Exception) {
            // Ignore cache save errors
        }
    }

    private suspend fun removeEntry(key: String) {
        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        newEntries.remove(key)
        saveCache(LanguageDetectionCacheData(newEntries))
    }
}
