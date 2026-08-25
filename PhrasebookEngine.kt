package com.bolchaal.app.engines

/** Offline, curated casual-phrase lookup. No network required. */
class PhrasebookEngine : TranslationEngine {

    private val phrasebook = mapOf(
        "hi" to "hi", "hello" to "hi bhai", "hey" to "oye",
        "how are you" to "kya haal hai", "i am fine" to "mein theek hoon",
        "whats up" to "kya scene hai", "what are you doing" to "kya kar rahe ho",
        "where are you" to "kahan ho", "i am coming" to "mein aa raha hoon",
        "come here" to "idhar aao", "i am busy" to "mein busy hoon",
        "call me" to "call kar dena", "text me" to "message kar dena",
        "let's go" to "chalo chalte hain", "i am hungry" to "mujhe bhookh lagi hai",
        "i am tired" to "mein thak gaya hoon", "good morning" to "subah bakhair",
        "good night" to "shab bakhair", "thank you" to "shukriya",
        "you're welcome" to "koi baat nahi", "sorry" to "sorry yaar",
        "it's okay" to "koi baat nahi", "i love you" to "mein tumse pyar karta hoon",
        "i miss you" to "tumhari yaad aa rahi hai", "i am happy" to "mein khush hoon",
        "i am sad" to "mein udaas hoon", "what happened" to "kya hua",
        "are you okay" to "tum theek ho", "yes" to "haan", "no" to "nahi",
        "maybe" to "shayad", "i don't know" to "pata nahi",
        "i understand" to "mujhe samajh aa gaya", "listen to me" to "meri baat suno",
        "give me that" to "woh do mujhe", "how much is this" to "yeh kitne ka hai",
        "let's meet" to "milte hain", "see you later" to "phir milte hain",
        "take care" to "apna khayal rakhna", "hold on" to "ruko zara",
        "congratulations" to "mubarak ho", "happy birthday" to "janamdin mubarak",
        "stop it" to "bas karo", "please help me" to "please meri madad karo",
        "this is great" to "yeh bohat acha hai", "what do you think" to "tumhara kya khayal hai"
        // Extend freely — this is a starting set, not exhaustive.
    )

    private fun normalize(s: String) = s.trim().lowercase().trimEnd('.', '!', '?')

    override suspend fun translate(englishText: String): TranslationResult {
        val key = normalize(englishText)
        phrasebook[key]?.let { return TranslationResult(it) }

        val words = key.split(Regex("\\s+"))
        if (words.isNotEmpty() && words.all { phrasebook.containsKey(it) }) {
            return TranslationResult(words.joinToString(" ") { phrasebook.getValue(it) }, approx = true)
        }
        throw Exception("Not in the phrasebook yet — try AI mode for this one.")
    }
}
