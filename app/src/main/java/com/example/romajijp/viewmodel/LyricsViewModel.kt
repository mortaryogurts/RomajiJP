package com.example.romajijp.viewmodel

import android.app.Application
import android.icu.text.Transliterator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.atilika.kuromoji.ipadic.Tokenizer
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import com.example.romajijp.uistate.LyricsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LyricsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val tokenizer = Tokenizer()
    private val transliterator = Transliterator.getInstance("Any-Latin; NFD; [:NonSpacing Mark:] Remove; NFC")

    private val _uiState = MutableStateFlow(LyricsUiState())
    val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

    fun loadLyrics(song: Song) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Check if saved
            val isSaved = repository.isSongSaved(song.title, song.artist)
            _uiState.value = _uiState.value.copy(isSaved = isSaved)

            val lyrics = song.lyrics ?: repository.fetchLyrics(song)

            if (lyrics != null) {
                processLyrics(lyrics)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Lyrics not found"
                )
            }
        }
    }

    private suspend fun processLyrics(lyrics: String) {
        withContext(Dispatchers.Default) {
            val isJP = containsJapanese(lyrics)
            val romaji = if (isJP) romanizeWithKuromoji(lyrics) else null

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                originalLyrics = lyrics,
                romanizedLyrics = romaji,
                isJapanese = isJP
            )
        }
    }

    fun downloadSong(song: Song) {
        viewModelScope.launch {
            repository.downloadSong(song.copy(lyrics = _uiState.value.originalLyrics))
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    private fun containsJapanese(text: String): Boolean {
        return text.any { char ->
            char.code in 0x3040..0x309F ||  // Hiragana
                    char.code in 0x30A0..0x30FF ||  // Katakana
                    char.code in 0x4E00..0x9FFF ||  // Kanji
                    char.code in 0xFF66..0xFF9F     // Half-width Katakana
        }
    }

    private fun romanizeWithKuromoji(text: String): String {
        val tokens = tokenizer.tokenize(text)
        val result = StringBuilder()

        for (token in tokens) {
            val surface = token.surface
            val reading = token.reading
            val pos = token.partOfSpeechLevel1

            if (reading == null || reading == "*" || reading.isBlank()) {
                result.append(surface)
                continue
            }

            var romaji = if (pos == "助詞") {
                when (surface) {
                    "は" -> "wa"
                    "へ" -> "e"
                    "を" -> "o"
                    else -> transliterator.transliterate(reading)
                }
            } else {
                transliterator.transliterate(reading)
            }

            romaji = romaji.lowercase()

            if (result.isNotEmpty() && !result.endsWith("\n") && !result.endsWith(" ")) {
                if (!isPunctuation(surface)) {
                    result.append(" ")
                }
            }

            result.append(romaji)
        }

        return result.toString()
            .replace(Regex(" +"), " ")
            .replace(Regex(" \n"), "\n")
            .trim()
    }

    private fun isPunctuation(text: String): Boolean {
        return text.all { it in "、。！？（）「」『』,.;:!?()[]{}<>\"' " || it == '　' || it.isWhitespace() }
    }
}
