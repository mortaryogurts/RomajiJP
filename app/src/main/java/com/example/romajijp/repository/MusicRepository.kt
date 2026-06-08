package com.example.romajijp.repository

import com.example.romajijp.apiclient.ITunesClient
import com.example.romajijp.apiclient.LrClibClient
import com.example.romajijp.model.Song
import com.example.romajijp.retrofit.ITunesRetrofitClient
import com.example.romajijp.retrofit.LrClibRetrofitClient
import com.example.romajijp.uistate.MusicUiState

class MusicRepository(context: android.content.Context? = null) {
    private val lrclibApi: LrClibClient = LrClibRetrofitClient.instance
    private val iTunesApi: ITunesClient = ITunesRetrofitClient.instance
    private val songDao by lazy { context?.let { com.example.romajijp.db.AppDatabase.getDatabase(it).songDao() } }

    suspend fun fetchSongData(userQuery: String): MusicUiState {
        return try {
            val itunesResult = iTunesApi.searchTrack(userQuery)
            if (itunesResult.results.isEmpty())
                return MusicUiState.Empty

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
        val cacheId = "${title.lowercase()}_${artist.lowercase()}"
        
        // 1. Try Cache
        val cached = songDao?.getSong(cacheId)
        if (cached?.lyrics != null) {
            return cached.lyrics
        }

        // 2. Try Network
        return try {
            val durationSeconds = (durationMillis / 1000).toInt()
            val lyrics = try {
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

            // 3. Save to Cache if found
            if (lyrics != null) {
                songDao?.insertSong(
                    com.example.romajijp.db.SongCache(
                        cacheId = cacheId,
                        title = title,
                        artist = artist,
                        album = album,
                        lyrics = lyrics,
                        artworkUrl = null, // We can store this too if needed
                        durationMillis = durationMillis
                    )
                )
            }
            lyrics
        } catch (e: Exception) {
            null
        }
    }

    suspend fun downloadSong(song: Song){
        val cacheId = "${song.title.lowercase()}_${song.artist.lowercase()}"
        songDao?.insertSong(
            com.example.romajijp.db.SongCache(
                cacheId = cacheId,
                title = song.title,
                artist = song.artist,
                album = song.album,
                lyrics = song.lyrics,
                artworkUrl = song.artworkUrl,
                durationMillis = song.durationMillis
            )
        )
    }
}