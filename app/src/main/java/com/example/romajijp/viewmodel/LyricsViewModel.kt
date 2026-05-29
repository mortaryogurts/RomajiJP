package com.example.romajijp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.romajijp.model.Song
import com.example.romajijp.repository.LyricsRepository
import com.example.romajijp.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class LyricsViewModel : ViewModel() {
    private val repository = LyricsRepository(
        RetrofitClient.api
    )

    private val mutableLiveDataSongsList = MutableLiveData<List<Song>>()
    val songLiveData : LiveData<List<Song>> = mutableLiveDataSongsList

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun searchSongs(q : String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val results = repository.searchSongs(q)
                mutableLiveDataSongsList.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}