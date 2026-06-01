package com.example.romajijp.apiclient

import com.example.romajijp.model.ItunesResponse
import com.example.romajijp.model.LrclibTrack
import retrofit2.http.GET
import retrofit2.http.Query

interface LrClibClient {
    @GET("search")
    suspend fun searchLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String
    ) : List<LrclibTrack>

    @GET("get")
    suspend fun getLyrics(
        @Query("track_name") trackName: String,
        @Query("artist_name") artistName: String,
        @Query("album_name") albumName: String,
        @Query("duration") durationSeconds: Int
    ) : LrclibTrack

}

interface ITunesClient {
    @GET("search")
    suspend fun searchTrack(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 20
    ) : ItunesResponse
}