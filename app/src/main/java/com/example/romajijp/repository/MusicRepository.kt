package com.example.romajijp.repository

import com.example.romajijp.apiclient.ITunesClient
import com.example.romajijp.apiclient.LrClibClient
import com.example.romajijp.model.Song
import com.example.romajijp.retrofit.ITunesRetrofitClient
import com.example.romajijp.retrofit.LrClibRetrofitClient
import com.example.romajijp.uistate.MusicUiState

class MusicRepository {
    private val lrclibApi: LrClibClient = LrClibRetrofitClient.instance
    private val iTunesApi: ITunesClient = ITunesRetrofitClient.instance

    suspend fun fetchSongData(userQuery: String): MusicUiState {
        return try {
            val itunesResult = iTunesApi.searchTrack(userQuery)
            if (itunesResult.results.isEmpty())
                return MusicUiState.Error("No results found for \"$userQuery\"")

            val songs = itunesResult.results.map { track ->
                Song(
                    id = 0,
                    title = track.trackName,
                    artist = track.artistName,
                    album = track.albumName,
                    lyrics = null,
                    artworkUrl = track.getArtworkUrl(512),
                    durationMillis = track.trackTimeMillis
                )
            }
            MusicUiState.Success(songs)
        } catch (e: Exception) {
            MusicUiState.Error(e.message ?: "Something went wrong")
        }
    }

    suspend fun fetchLyrics(title: String, artist: String, album: String?, durationMillis: Long): String? {
        return try {
            val durationSeconds = (durationMillis / 1000).toInt()
            try {
                // Exact match
                val track = lrclibApi.getLyrics(title, artist, album ?: "", durationSeconds)
                track.plainLyrics
            } catch (e: Exception) {
                // Fallback search
                val searchResults = lrclibApi.searchLyrics(title, artist)
                if (searchResults.isNotEmpty()) {
                    searchResults[0].plainLyrics
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
}