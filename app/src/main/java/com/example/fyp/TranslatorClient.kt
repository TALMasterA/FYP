package com.example.fyp

import android.util.Log
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
                    Log.e("Translator", "HTTP ${response.code}: $respBody")
                    return@withContext SpeechResult.Error("Translator error: HTTP ${response.code}")
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
}
