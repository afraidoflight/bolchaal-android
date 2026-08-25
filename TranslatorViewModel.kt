package com.bolchaal.app

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bolchaal.app.engines.AiEngine
import com.bolchaal.app.engines.GoogleEngine
import com.bolchaal.app.engines.PhrasebookEngine
import com.bolchaal.app.engines.TranslationEngine
import kotlinx.coroutines.launch

class TranslatorViewModel : ViewModel() {

    val messages = mutableStateListOf<Message>()
    var engineType = mutableStateOf(EngineType.AI)
    var loading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    // Set these from a settings screen / secure storage — never hardcode.
    var anthropicApiKey: String = ""
    var googleApiKey: String = ""

    private fun currentEngine(): TranslationEngine = when (engineType.value) {
        EngineType.AI -> AiEngine(anthropicApiKey)
        EngineType.PHRASEBOOK -> PhrasebookEngine()
        EngineType.GOOGLE -> GoogleEngine(googleApiKey)
    }

    fun send(englishText: String) {
        if (englishText.isBlank() || loading.value) return
        error.value = null
        loading.value = true
        viewModelScope.launch {
            try {
                val result = currentEngine().translate(englishText)
                messages.add(Message(english = englishText, urdu = result.text, approx = result.approx))
            } catch (e: Exception) {
                error.value = e.message ?: "Something went wrong."
            } finally {
                loading.value = false
            }
        }
    }

    fun toggleFavorite(id: Long) {
        val idx = messages.indexOfFirst { it.id == id }
        if (idx >= 0) messages[idx] = messages[idx].copy(favorite = !messages[idx].favorite)
    }

    fun remove(id: Long) {
        messages.removeAll { it.id == id }
    }
}
