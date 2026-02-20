package com.example.fyp.data.wordbank

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

private val Context.wordBankCacheDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "word_bank_cache"
)

@Serializable
data class WordBankCacheEntry(
    val exists: Boolean,
    val wordCount: Int = 0,
    val historyCountAtGenerate: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class WordBankCacheData(
    val entries: Map<String, WordBankCacheEntry> = emptyMap()
)

/**
 * Persisted cache for word bank existence and metadata.
 * Reduces Firestore reads by caching whether a word bank exists for a language pair.
 * Cache survives app restart.
 */
@Singleton
class WordBankCacheDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val CACHE_KEY = stringPreferencesKey("word_bank_cache")
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours
    }

    private val json = Json { ignoreUnknownKeys = true }

    // In-memory mirror of the DataStore contents — avoids re-parsing JSON on every read.
    // Null means "not yet loaded"; invalidated on every write.
    @Volatile private var memCache: WordBankCacheData? = null

    /**
     * Generate cache key for a language pair
     */
    private fun cacheKey(userId: String, primaryLang: String, targetLang: String): String {
        return "${userId}|${primaryLang}|${targetLang}"
    }

    /**
     * Check if word bank exists (from cache)
     * Returns null if not cached or expired
     */
    suspend fun getWordBankExists(
        userId: String,
        primaryLang: String,
        targetLang: String
    ): Boolean? {
        val key = cacheKey(userId, primaryLang, targetLang)
        val cacheData = loadCache()
        val entry = cacheData.entries[key] ?: return null

        // Check if expired
        if (System.currentTimeMillis() - entry.timestamp > CACHE_TTL_MS) {
            return null
        }

        return entry.exists
    }

    /**
     * Get cached word bank metadata
     */
    suspend fun getWordBankMetadata(
        userId: String,
        primaryLang: String,
        targetLang: String
    ): WordBankCacheEntry? {
        val key = cacheKey(userId, primaryLang, targetLang)
        val cacheData = loadCache()
        val entry = cacheData.entries[key] ?: return null

        // Check if expired
        if (System.currentTimeMillis() - entry.timestamp > CACHE_TTL_MS) {
            return null
        }

        return entry
    }

    /**
     * Cache word bank existence and metadata
     */
    suspend fun cacheWordBank(
        userId: String,
        primaryLang: String,
        targetLang: String,
        exists: Boolean,
        wordCount: Int = 0,
        historyCountAtGenerate: Int = 0
    ) {
        val key = cacheKey(userId, primaryLang, targetLang)
        val entry = WordBankCacheEntry(
            exists = exists,
            wordCount = wordCount,
            historyCountAtGenerate = historyCountAtGenerate
        )

        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        newEntries[key] = entry

        saveCache(WordBankCacheData(newEntries))
    }

    /**
     * Invalidate cache for a specific language pair (e.g., after generation)
     */
    suspend fun invalidate(userId: String, primaryLang: String, targetLang: String) {
        val key = cacheKey(userId, primaryLang, targetLang)
        val cacheData = loadCache()
        val newEntries = cacheData.entries.toMutableMap()
        newEntries.remove(key)
        saveCache(WordBankCacheData(newEntries))
    }

    /**
     * Invalidate all cache for a user (e.g., on logout)
     */
    suspend fun invalidateAllForUser(userId: String) {
        val cacheData = loadCache()
        val newEntries = cacheData.entries.filterKeys { !it.startsWith("$userId|") }
        saveCache(WordBankCacheData(newEntries))
    }

    /**
     * Clear all cached data
     */
    suspend fun clearAll() {
        memCache = null
        context.wordBankCacheDataStore.edit { prefs ->
            prefs.remove(CACHE_KEY)
        }
    }

    private suspend fun loadCache(): WordBankCacheData {
        // Serve from the in-memory mirror when available — avoids DataStore + JSON overhead.
        memCache?.let { return it }
        return try {
            context.wordBankCacheDataStore.data
                .map { prefs ->
                    val jsonString = prefs[CACHE_KEY]
                    if (jsonString != null) json.decodeFromString<WordBankCacheData>(jsonString)
                    else WordBankCacheData()
                }
                .first()
                .also { memCache = it }
        } catch (e: Exception) {
            WordBankCacheData()
        }
    }

    private suspend fun saveCache(data: WordBankCacheData) {
        memCache = data          // update mirror immediately
        try {
            context.wordBankCacheDataStore.edit { prefs ->
                prefs[CACHE_KEY] = json.encodeToString(data)
            }
        } catch (e: Exception) {
            // Ignore cache save errors
        }
    }
}
