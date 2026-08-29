package com.example.romajijp.uistate

enum class LyricsDisplayMode {
    ORIGINAL, ROMAJI, FURIGANA
}

data class LyricsToken(
    val surface: String,
    val reading: String? = null
)

data class LyricsUiState(
    val isLoading: Boolean = true,
    val originalLyrics: String? = null,
    val romanizedLyrics: String? = null,
    val furiganaLyrics: List<LyricsToken>? = null,
    val displayMode: LyricsDisplayMode = LyricsDisplayMode.ROMAJI,
    val isJapanese: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val translationResult : String? = null
)
