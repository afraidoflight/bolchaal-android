package com.bolchaal.app.engines

/**
 * Result of a translation attempt.
 * @param approx true when the spelling is a best-effort romanization
 *               rather than a natural casual rendering (e.g. Google mode).
 */
data class TranslationResult(val text: String, val approx: Boolean = false)

interface TranslationEngine {
    /** Throws an exception with a user-facing message on failure. */
    suspend fun translate(englishText: String): TranslationResult
}
