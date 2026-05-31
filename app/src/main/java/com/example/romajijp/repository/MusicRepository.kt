package com.example.romajijp.repository

import com.example.romajijp.apiclient.ITunesClient
import com.example.romajijp.apiclient.LrClibClient
import com.example.romajijp.model.Song
import com.example.romajijp.retrofit.ITunesRetrofitClient
import com.example.romajijp.retrofit.LrClibRetrofitClient
import com.example.romajijp.uistate.MusicUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MusicRepository {
    private val lrclibApi: LrClibClient = LrClibRetrofitClient.instance
    private val iTunesApi: ITunesClient = ITunesRetrofitClient.instance

    suspend fun fetchSongData(
        userQuery: String,
    ): MusicUiState = coroutineScope {
        try {
            val itunesResult = iTunesApi.searchTrack(userQuery)
            if (itunesResult.results.isEmpty())
                return@coroutineScope MusicUiState.Error("No results found for \"$userQuery\"")

            val deferredSongs = itunesResult.results.map { track ->
                async {
                    var lyrics: String? = null
                    try {
                        val durationSeconds = (track.trackTimeMillis / 1000).toInt()
                        val lrclibTrack = lrclibApi.getLyrics(
                            trackName = track.trackName,
                            artistName = track.artistName,
                            albumName = track.albumName,
                            durationSeconds = durationSeconds
                        )
                        lyrics = lrclibTrack.plainLyrics
                    } catch (e: Exception) {
                        // If exact match fails, try a search as fallback
                        try {
                            val searchResults = lrclibApi.searchLyrics(
                                trackName = track.trackName,
                                artistName = track.artistName
                            )
                            if (searchResults.isNotEmpty()) {
                                lyrics = searchResults[0].plainLyrics
                            }
                        } catch (inner: Exception) {
                            // No lyrics found
                        }
                    }

                    Song(
                        id = 0,
                        title = track.trackName,
                        artist = track.artistName,
                        album = track.albumName,
                        lyrics = lyrics,
                        artworkUrl = track.getArtworkUrl(512)
                    )
                }
            }

            val songs = deferredSongs.awaitAll()
            MusicUiState.Success(songs)
        } catch (e: Exception) {
            MusicUiState.Error(e.message ?: "Something went wrong")
        }
    }
}