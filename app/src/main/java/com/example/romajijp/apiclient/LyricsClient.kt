package com.example.romajijp.apiclient

import com.example.romajijp.model.LyricsResponse
import com.example.romajijp.model.SongResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsClient {
    @GET("search")
    suspend fun searchSongs(
        @Query("q") query: String
    ) : List<SongResponse>
}