package com.example.fyp.data.cloud

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.translationCacheDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "translation_cache"
)

@Serializable
data class CachedTranslation(
    val sourceText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class TranslationCacheData(
    val entries: Map<String, CachedTranslation> = emptyMap()
)

/**
 * Local translation cache using DataStore.
 * Caches translations to reduce API calls and improve responsiveness.
 * Uses a two-tier cache: in-memory for fast access, DataStore for persistence.
 */
@Singleton
class TranslationCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("translation_cache")
        private const val MAX_CACHE_SIZE = 1000 // Increased from 500 for better cache coverage
        private const val CACHE_TTL_MS = 30 * 24 * 60 * 60 * 1000L // 30 days (increased from 7)
        private const val IN_MEMORY_CACHE_SIZE = 200 // Fast in-memory cache
    }

    private val json = Json { ignoreUnknownKeys = true }

    // In-memory cache for frequently accessed translations (LRU-style)
    private val inMemoryCache = object : LinkedHashMap<String, CachedTranslation>(
        IN_MEMORY_CACHE_SIZE, 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CachedTranslation>?): Boolean {
            return size > IN_MEMORY_CACHE_SIZE
        }
    }

    /**
     * Generate cache key from source text and language pair
     */
    private fun cacheKey(sourceText: String, sourceLang: String, targetLang: String): String {
        return "${sourceLang}|${targetLang}|${sourceText.trim().lowercase()}"
    }

    /**
     * Get cached translation if available and not expired.
     * Checks in-memory cache first, then DataStore.
     */
    suspend fun getCached(
        sourceText: String,
        sourceLang: String,
        targetLang: String
    ): String? {
        val key = cacheKey(sourceText, sourceLang, targetLang)
        val now = System.currentTimeMillis()

        // Check in-memory cache first (fast path)
        synchronized(inMemoryCache) {
            val memEntry = inMemoryCache[key]
            if (memEntry != null && (now - memEntry.timestamp <= CACHE_TTL_MS)) {
                return memEntry.translatedText
            }
        }

        // Fall back to DataStore
        val cacheData = loadCache()
        val entry = cacheData.entries[key] ?: return null

        // Check if expired
        if (now - entry.timestamp > CACHE_TTL_MS) {
            // Remove expired entry
            removeEntry(key)
            return null
        }

        // Add to in-memory cache for future fast access
        synchronized(inMemoryCache) {
            inMemoryCache[key] = entry
        }

        return entry.translatedText
    }

    /**
     * Cache a translation result
     */
    suspend fun cache(
        sourceText: String,
        translatedText: String,
        sourceLang: String,
        targetLang: String
    ) {
        val key = cacheKey(sourceText, sourceLang, targetLang)
        val entry = CachedTranslation(
            sourceText = sourceText.trim(),
            translatedText = translatedText,
            sourceLang = sourceLang,
            targetLang = targetLang
        )

        // Update in-memory cache immediately
        synchronized(inMemoryCache) {
            inMemoryCache[key] = entry
        }

        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        newEntries[key] = entry

        // Evict old entries if cache is full
        if (newEntries.size > MAX_CACHE_SIZE) {
            val sortedEntries = newEntries.entries.sortedBy { it.value.timestamp }
            val entriesToRemove = sortedEntries.take(newEntries.size - MAX_CACHE_SIZE)
            entriesToRemove.forEach { newEntries.remove(it.key) }
        }

        saveCache(TranslationCacheData(newEntries))
    }

    /**
     * Clear all cached translations
     */
    suspend fun clearAll() {
        synchronized(inMemoryCache) {
            inMemoryCache.clear()
        }
        context.translationCacheDataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
        }
    }

    /**
     * Get cache statistics
     */
    suspend fun getStats(): CacheStats {
        val cacheData = loadCache()
        val now = System.currentTimeMillis()
        val validEntries = cacheData.entries.values.filter {
            now - it.timestamp <= CACHE_TTL_MS
        }
        return CacheStats(
            totalEntries = cacheData.entries.size,
            validEntries = validEntries.size,
            expiredEntries = cacheData.entries.size - validEntries.size
        )
    }

    /**
     * Batch get cached translations.
     * Returns a map of text -> translation for found entries, and list of texts not in cache.
     */
    suspend fun getBatchCached(
        texts: List<String>,
        sourceLang: String,
        targetLang: String
    ): BatchCacheResult {
        val cacheData = loadCache()
        val now = System.currentTimeMillis()
        val found = mutableMapOf<String, String>()
        val notFound = mutableListOf<String>()

        for (text in texts) {
            val key = cacheKey(text, sourceLang, targetLang)
            val entry = cacheData.entries[key]

            if (entry != null && (now - entry.timestamp <= CACHE_TTL_MS)) {
                found[text] = entry.translatedText
            } else {
                notFound.add(text)
            }
        }

        return BatchCacheResult(found, notFound)
    }

    /**
     * Batch cache multiple translations at once.
     * More efficient than caching one at a time.
     */
    suspend fun cacheBatch(
        translations: Map<String, String>, // sourceText -> translatedText
        sourceLang: String,
        targetLang: String
    ) {
        if (translations.isEmpty()) return

        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        val now = System.currentTimeMillis()

        for ((sourceText, translatedText) in translations) {
            val key = cacheKey(sourceText, sourceLang, targetLang)
            newEntries[key] = CachedTranslation(
                sourceText = sourceText.trim(),
                translatedText = translatedText,
                sourceLang = sourceLang,
                targetLang = targetLang,
                timestamp = now
            )
        }

        // Evict old entries if cache is full
        if (newEntries.size > MAX_CACHE_SIZE) {
            val sortedEntries = newEntries.entries.sortedBy { it.value.timestamp }
            val entriesToRemove = sortedEntries.take(newEntries.size - MAX_CACHE_SIZE)
            entriesToRemove.forEach { newEntries.remove(it.key) }
        }

        saveCache(TranslationCacheData(newEntries))
    }

    private suspend fun loadCache(): TranslationCacheData {
        return try {
            context.translationCacheDataStore.data
                .map { prefs ->
                    val jsonString = prefs[CACHE_KEY]
                    if (jsonString != null) {
                        json.decodeFromString<TranslationCacheData>(jsonString)
                    } else {
                        TranslationCacheData()
                    }
                }
                .first()
        } catch (e: Exception) {
            TranslationCacheData()
        }
    }

    private suspend fun saveCache(data: TranslationCacheData) {
        try {
            context.translationCacheDataStore.edit { prefs ->
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
        saveCache(TranslationCacheData(newEntries))
    }
}

data class CacheStats(
    val totalEntries: Int,
    val validEntries: Int,
    val expiredEntries: Int
)

/**
 * Result of batch cache lookup.
 * @param found Map of sourceText -> translatedText for cached entries
 * @param notFound List of texts not found in cache (need API call)
 */
data class BatchCacheResult(
    val found: Map<String, String>,
    val notFound: List<String>
)

