package com.example.romajijp.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String?,
    val lyrics: String?
)