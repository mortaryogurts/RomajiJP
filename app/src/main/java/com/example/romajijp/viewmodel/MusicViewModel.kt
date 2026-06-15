package com.example.romajijp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import com.example.romajijp.uistate.MusicUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    private val repository by lazy { MusicRepository(application) }

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState : StateFlow<MusicUiState> = _uiState.asStateFlow()
    fun searchSongs(query: String) {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading

            val result = repository.fetchSongData(query)

            _uiState.value = result

            if (result is MusicUiState.Success){
                preFetchTopLyrics(result.songs.take(20))
            }
        }
    }

    fun resetState() {
        _uiState.value = MusicUiState.Idle
    }

    suspend fun getLyrics(song: Song): String? {
        return repository.fetchLyrics(song)
    }

    private fun preFetchTopLyrics(songs: List<Song>){
        songs.forEach { song ->
            viewModelScope.launch {
                repository.fetchLyrics(song)
            }
        }
    }
}


