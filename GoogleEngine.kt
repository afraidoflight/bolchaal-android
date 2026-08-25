package com.bolchaal.app.engines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Uses Google Cloud Translation to get Urdu (Nastaliq script), then
 * romanizes it locally. The romanization is a rough character-map
 * approximation, not a linguistically accurate transliteration —
 * flagged via TranslationResult.approx.
 */
class GoogleEngine(private val apiKey: String) : TranslationEngine {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    private val urduMap = mapOf(
        'آ' to "aa", 'ا' to "a", 'ب' to "b", 'پ' to "p", 'ت' to "t", 'ٹ' to "t",
        'ث' to "s", 'ج' to "j", 'چ' to "ch", 'ح' to "h", 'خ' to "kh", 'د' to "d",
        'ڈ' to "d", 'ذ' to "z", 'ر' to "r", 'ڑ' to "r", 'ز' to "z", 'ژ' to "zh",
        'س' to "s", 'ش' to "sh", 'ص' to "s", 'ض' to "z", 'ط' to "t", 'ظ' to "z",
        'ع' to "a", 'غ' to "gh", 'ف' to "f", 'ق' to "q", 'ک' to "k", 'گ' to "g",
        'ل' to "l", 'م' to "m", 'ن' to "n", 'ں' to "n", 'و' to "o", 'ہ' to "h",
        'ھ' to "h", 'ء' to "", 'ی' to "i", 'ے' to "e", 'ۓ' to "e", 'ؤ' to "o",
        'ئ' to "i", '۔' to ".", '،' to ",", '؟' to "?"
    )

    private fun transliterate(urdu: String): String =
        urdu.map { urduMap[it] ?: it.toString() }.joinToString("").replace(Regex("\\s+"), " ").trim()

    override suspend fun translate(englishText: String): TranslationResult =
        withContext(Dispatchers.IO) {
            val body = JSONObject().apply {
                put("q", englishText)
                put("target", "ur")
                put("format", "text")
            }
            val request = Request.Builder()
                .url("https://translation.googleapis.com/language/translate2?key=$apiKey")
                .post(body.toString().toRequestBody(JSON))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Google request failed (${response.code})")
                }
                val json = JSONObject(response.body?.string().orEmpty())
                val urduScript = json.getJSONObject("data")
                    .getJSONArray("translations")
                    .getJSONObject(0)
                    .getString("translatedText")
                TranslationResult(transliterate(urduScript), approx = true)
            }
        }
}
