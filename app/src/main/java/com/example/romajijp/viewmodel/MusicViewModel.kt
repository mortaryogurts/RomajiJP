package com.example.romajijp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.romajijp.model.Song
import com.example.romajijp.repository.MusicRepository
import com.example.romajijp.retrofit.ITunesRetrofitClient
import com.example.romajijp.retrofit.LrClibRetrofitClient
import com.example.romajijp.uistate.MusicUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel : ViewModel() {
    private val repository = MusicRepository()

    private val _uiState = MutableStateFlow<MusicUiState>(MusicUiState.Idle)
    val uiState : StateFlow<MusicUiState> = _uiState.asStateFlow()
    fun searchSongs(query: String) {
        viewModelScope.launch {
            _uiState.value = MusicUiState.Loading
            _uiState.value = repository.fetchSongData(userQuery = query)
        }
    }
}


