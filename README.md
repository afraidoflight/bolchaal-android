# BolChaal — Android (Kotlin + Jetpack Compose)

Native Android scaffold for the casual English → Roman Urdu translator, with
three swappable translation engines behind one `TranslationEngine` interface:

- `AiEngine` — calls the Anthropic Messages API for natural, code-switched casual Urdu
- `PhrasebookEngine` — offline curated phrase lookup, no network
- `GoogleEngine` — Google Cloud Translation, then a local Urdu-script → Roman transliteration pass

## Setup

1. Open this folder in Android Studio (Koala or newer).
2. Let Gradle sync — it will pull dependencies from Google/Maven Central.
3. Wire up API keys — **do not hardcode them**. For a quick local test you can set them at
   runtime, e.g. in `MainActivity.onCreate`:
   ```kotlin
   viewModel.anthropicApiKey = "sk-ant-..."
   viewModel.googleApiKey = "AIza..."
   ```
   For anything you'd actually ship, proxy both calls through your own backend instead —
   an API key embedded in an APK can be extracted.
4. Run on a device or emulator (min SDK 24).

## What's here

```
app/src/main/java/com/bolchaal/app/
  MainActivity.kt          — Compose UI (chat thread, engine picker, composer)
  TranslatorViewModel.kt   — holds message list, picks the active engine
  Message.kt               — message + engine-type models
  engines/
    TranslationEngine.kt   — shared interface
    AiEngine.kt
    PhrasebookEngine.kt
    GoogleEngine.kt
```

## Known gaps (this is a scaffold, not a finished app)

- No persistent storage yet — history clears when the app process dies. Add Room
  or DataStore to persist `messages`.
- No voice input yet — wire up `SpeechRecognizer` in `MainActivity`.
- `PhrasebookEngine`'s dictionary is a starting set (~40 entries) — the web
  prototype has ~90; copy more over as needed.
- The Urdu→Roman transliteration in `GoogleEngine` is a rough character map,
  not linguistically accurate (no vowel disambiguation, no izafat handling).
- No app icon / branding assets included.
