package com.example.romajijp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
            _uiState.value = repository.fetchSongData(userQuery = query)
        }
    }
}


