package com.example.romajijp.model

data class Lyrics(
    val title: String,
    val artist: String,
    val lyrics: String,
    val romanizedLyrics: String? = null,
    val language: String? = null
)