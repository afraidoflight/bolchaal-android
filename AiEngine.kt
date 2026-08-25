package com.bolchaal.app.engines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Calls the Anthropic Messages API directly.
 *
 * You must supply your own Anthropic API key (from console.anthropic.com).
 * NEVER ship a real key inside a distributed APK — in production, proxy
 * this call through your own backend so the key never sits on-device.
 */
class AiEngine(private val apiKey: String) : TranslationEngine {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    override suspend fun translate(englishText: String): TranslationResult =
        withContext(Dispatchers.IO) {
            val prompt =
                "Translate the following English into casual, Latinized (Roman-script) Urdu, " +
                    "the way friends actually text each other — natural code-switching with " +
                    "English words where that's realistic, no formal vocabulary. Reply with " +
                    "ONLY the translation, nothing else, no quotes.\n\nEnglish: $englishText"

            val body = JSONObject().apply {
                put("model", "claude-sonnet-4-6")
                put("max_tokens", 300)
                put(
                    "messages",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        }
                    )
                )
            }

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("AI request failed (${response.code})")
                }
                val json = JSONObject(response.body?.string().orEmpty())
                val content = json.getJSONArray("content")
                var text: String? = null
                for (i in 0 until content.length()) {
                    val block = content.getJSONObject(i)
                    if (block.getString("type") == "text") {
                        text = block.getString("text").trim()
                        break
                    }
                }
                TranslationResult(text ?: throw Exception("No translation returned"))
            }
        }
}
