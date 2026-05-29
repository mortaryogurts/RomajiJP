package com.example.romajijp.model

data class SongResponse(
    val id: Int,
    val trackName: String,
    val artistName: String,
    val albumName: String?,
    val plainLyrics: String?
)