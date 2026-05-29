package com.example.romajijp.repository

import com.example.romajijp.apiclient.LyricsClient
import com.example.romajijp.model.Lyrics
import com.example.romajijp.model.LyricsResponse
import com.example.romajijp.model.Song
import com.example.romajijp.model.toSong

class LyricsRepository(
    private val api : LyricsClient
    ){
    suspend fun searchSongs(q : String) : List<Song>{
        return api.searchSongs(q)
            .map { it.toSong() }
    }
}