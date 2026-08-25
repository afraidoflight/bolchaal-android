package com.bolchaal.app

data class Message(
    val id: Long = System.currentTimeMillis(),
    val english: String,
    val urdu: String,
    val approx: Boolean = false,
    val favorite: Boolean = false
)

enum class EngineType(val label: String) {
    AI("AI"),
    PHRASEBOOK("Phrasebook"),
    GOOGLE("Google")
}
