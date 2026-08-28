package com.example.romajijp.uistate

data class LyricsUiState(
    val isLoading: Boolean = true,
    val originalLyrics: String? = null,
    val romanizedLyrics: String? = null,
    val isJapanese: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)