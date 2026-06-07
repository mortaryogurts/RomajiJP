package com.example.romajijp.uistate

import com.example.romajijp.model.Song

sealed class MusicUiState {
    object Idle    : MusicUiState()
    object Loading : MusicUiState()
    object Empty   : MusicUiState()
    data class Success(val songs: List<Song>) : MusicUiState()
    data class Error(val message: String) : MusicUiState()
}