package com.example.fyp.data

import android.util.Log
import com.example.fyp.BuildConfig
import com.example.fyp.model.SpeechResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object TranslatorClient {

    private const val ENDPOINT = "https://api.cognitive.microsofttranslator.com"
    private const val API_VERSION = "3.0"

    suspend fun translateText(
        text: String,
        toLanguage: String,
        fromLanguage: String? = null
    ): SpeechResult = withContext(Dispatchers.IO) {

        if (text.isBlank()) {
            return@withContext SpeechResult.Error("No text to translate")
        }

        try {
            val key = BuildConfig.AZURE_TRANSLATOR_KEY
            val region = BuildConfig.AZURE_TRANSLATOR_REGION

            val urlBuilder = StringBuilder("$ENDPOINT/translate?api-version=$API_VERSION")
            if (!fromLanguage.isNullOrBlank()) {
                urlBuilder.append("&from=$fromLanguage")
            }
            urlBuilder.append("&to=$toLanguage")
            val url = urlBuilder.toString()

            val requestBodyJson = JSONArray().apply {
                put(JSONObject().apply { put("Text", text) })
            }.toString()

            val body = requestBodyJson.toRequestBody(
                "application/json; charset=utf-8".toMediaTypeOrNull()
            )

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", key)
                .addHeader("Ocp-Apim-Subscription-Region", region)
                .build()

            val client = NetworkClient.okHttpClient
            client.newCall(request).execute().use { response ->
                val respBody = response.body?.string()
                if (!response.isSuccessful || respBody.isNullOrEmpty()) {
                    val msg = "HTTP ${response.code}: ${respBody ?: "empty body"}"
                    Log.e("Translator", msg)
                    return@withContext SpeechResult.Error("Translator error, please check key/region or network. ($msg)")
                }

                val arr = JSONArray(respBody)
                if (arr.length() == 0) {
                    return@withContext SpeechResult.Error("No translation result")
                }
                val translations =
                    arr.getJSONObject(0).getJSONArray("translations")
                if (translations.length() == 0) {
                    return@withContext SpeechResult.Error("No translation result")
                }
                val translatedText =
                    translations.getJSONObject(0).getString("text")
                SpeechResult.Success(translatedText)
            }
        } catch (e: Exception) {
            Log.e("Translator", "Error: ${e.message}", e)
            SpeechResult.Error("Translator error: ${e.message}")
        }
    }

    suspend fun translateTexts(
        texts: List<String>,
        toLanguage: String,
        fromLanguage: String = "en"
    ): SpeechResult = withContext(Dispatchers.IO) {

        if (texts.isEmpty()) {
            return@withContext SpeechResult.Error("No texts to translate")
        }

        try {
            val key = BuildConfig.AZURE_TRANSLATOR_KEY
            val region = BuildConfig.AZURE_TRANSLATOR_REGION

            val url = buildString {
                append("$ENDPOINT/translate?api-version=$API_VERSION")
                append("&from=$fromLanguage")
                append("&to=$toLanguage")
            }

            // Body: [{"Text":"..."}, {"Text":"..."}]
            val arr = JSONArray()
            texts.forEach { t ->
                arr.put(JSONObject().apply { put("Text", t) })
            }
            val body = arr.toString().toRequestBody(
                "application/json; charset=utf-8".toMediaTypeOrNull()
            )

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", key)
                .addHeader("Ocp-Apim-Subscription-Region", region)
                .build()

            val client = NetworkClient.okHttpClient
            client.newCall(request).execute().use { response ->
                val respBody = response.body?.string()
                if (!response.isSuccessful || respBody.isNullOrEmpty()) {
                    return@withContext SpeechResult.Error("Translator error: HTTP ${response.code}")
                }

                // Response example: [ { "translations":[{"text":"..."}, ...] }, ... ]
                val jsonArray = JSONArray(respBody)
                if (jsonArray.length() != texts.size) {
                    return@withContext SpeechResult.Error("Unexpected translation result size")
                }

                val translatedList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val translations = jsonArray.getJSONObject(i).getJSONArray("translations")
                    if (translations.length() == 0) {
                        return@withContext SpeechResult.Error("Missing translation")
                    }
                    translatedList.add(
                        translations.getJSONObject(0).getString("text")
                    )
                }

                SpeechResult.Success(translatedList.joinToString("\u0001")) // join with separator
            }
        } catch (e: Exception) {
            SpeechResult.Error("Translator error: ${e.message}")
        }
    }
}
